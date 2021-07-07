/*
 * This file is part of Prism, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.minecraftonline.griefalert.sponge.data.storage.mongodb;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.minecraftonline.griefalert.SpongeGriefAlert;
import com.minecraftonline.griefalert.common.data.flags.Flag;
import com.minecraftonline.griefalert.common.data.query.Condition;
import com.minecraftonline.griefalert.common.data.query.ConditionGroup;
import com.minecraftonline.griefalert.common.data.query.FieldCondition;
import com.minecraftonline.griefalert.common.data.query.MatchRule;
import com.minecraftonline.griefalert.common.data.query.Query;
import com.minecraftonline.griefalert.common.data.query.QuerySession;
import com.minecraftonline.griefalert.sponge.data.records.Result;
import com.minecraftonline.griefalert.common.data.storage.StorageAdapterRecords;
import com.minecraftonline.griefalert.common.data.storage.StorageDeleteResult;
import com.minecraftonline.griefalert.common.data.storage.StorageWriteResult;
import com.minecraftonline.griefalert.sponge.data.util.DataQueries;
import com.minecraftonline.griefalert.sponge.data.util.DataUtil;
import com.minecraftonline.griefalert.sponge.data.util.DateUtil;
import com.minecraftonline.griefalert.sponge.data.util.PrimitiveArray;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bson.Document;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;


import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author viveleroi
 */
public class MongoRecords implements StorageAdapterRecords {

  private final BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().ordered(false);
  private final String expiration = SpongeGriefAlert.getSpongeInstance().getConfig().getStorageCategory().getExpireRecords();
  private final boolean expires = SpongeGriefAlert.getSpongeInstance().getConfig().getStorageCategory().isShouldExpire();

  /**
   * Converts a DataView to a Document, recursively if needed.
   *
   * @param view Data view/container.
   * @return Document for Mongo storage.
   */
  private Document documentFromView(DataView view) {
    Document document = new Document();

    Set<DataQuery> keys = view.getKeys(false);
    for (DataQuery query : keys) {
      String key = DataUtil.escapeQuery(query);
      Object value = view.get(query).orElse(null);

      if (value == null) {
        // continue
      } else if (value instanceof Collection) {
        List<Object> convertedList = Lists.newArrayList();
        for (Object object : (Collection<?>) value) {
          if (object == null) {
            // continue
          } else if (object instanceof DataView) {
            convertedList.add(documentFromView((DataView) object));
          } else if (DataUtil.isPrimitiveType(object)) {
            convertedList.add(object);
          } else if (object.getClass().isArray()) {
            document.append(key, new PrimitiveArray(object));
          } else if (object.getClass().isEnum()) {
            // Ignoring, this data should exist elsewhere in the document.
            // this is ConnectedDirections and other vanilla manipulators
            // convertedList.add(object.toString());
          } else {
            SpongeGriefAlert.getSpongeInstance().getLogger().error("Unsupported list data type: " + object.getClass().getName());
          }
        }

        if (!convertedList.isEmpty()) {
          document.append(key, convertedList);
        }
      } else if (value instanceof DataView) {
        document.append(key, documentFromView((DataView) value));
      } else if (value.getClass().isArray()) {
        document.append(key, new PrimitiveArray(value));
      } else {
        if (key.equals(DataQueries.Player.toString())) {
          document.append(DataQueries.Player.toString(), value);
        } else {
          document.append(key, value);
        }
      }
    }

    return document;
  }

  /**
   * Convert a mongo Document to a DataContainer.
   *
   * @param document Mongo document.
   * @return Data container.
   */
  private DataContainer documentToDataContainer(Document document) {
    DataContainer result = DataContainer.createNew();

    for (String key : document.keySet()) {
      DataQuery query = DataUtil.unescapeQuery(key);
      Object value = document.get(key);

      if (value instanceof Document) {
        PrimitiveArray primitiveArray = PrimitiveArray.of((Document) value);
        if (primitiveArray != null) {
          result.set(query, primitiveArray.getArray());
          continue;
        }

        result.set(query, documentToDataContainer((Document) value));
      } else {
        result.set(query, value);
      }
    }

    return result;
  }

  @Override
  public StorageWriteResult write(List<DataContainer> containers) throws Exception {
    MongoCollection<Document> collection = MongoStorageAdapter.getCollection(MongoStorageAdapter.collectionEventRecordsName);

    // Build an array of documents
    List<WriteModel<Document>> documents = new ArrayList<>();
    for (DataContainer container : containers) {
      Document document = documentFromView(container);

      // SpongeGriefAlert.getSpongeInstance().getLogger().debug(DataUtil.jsonFromDataView(container).toString());

      // TTL
      if (expires) {
        document.append("Expires", DateUtil.parseTimeStringToDate(expiration, true));
      }

      // Insert
      documents.add(new InsertOneModel<>(document));
    }

    // Write
    collection.bulkWrite(documents, bulkWriteOptions);

    // @todo implement real results, BulkWriteResult

    return new StorageWriteResult();
  }

  /**
   * Recursive method of building condition documents.
   *
   * @param fieldsOrGroups List<Condition>
   * @return Document
   */
  private Document buildConditions(List<Condition> fieldsOrGroups) {
    Document conditions = new Document();

    for (Condition fieldOrGroup : fieldsOrGroups) {
      if (fieldOrGroup instanceof ConditionGroup) {
        ConditionGroup group = (ConditionGroup) fieldOrGroup;
        Document subDoc = buildConditions(group.getConditions());

        if (group.getOperator().equals(ConditionGroup.Operator.OR)) {
          conditions.append("$or", subDoc);
        } else {
          conditions.putAll(subDoc);
        }
      } else {
        FieldCondition field = (FieldCondition) fieldOrGroup;

        Document matcher;
        if (conditions.containsKey(field.getFieldName().toString())) {
          matcher = (Document) conditions.get(field.getFieldName().toString());
        } else {
          matcher = new Document();
        }

        // Match an array of items
        if (field.getValue() instanceof List) {
          matcher.append(field.getMatchRule().equals(MatchRule.INCLUDES) ? "$in" : "$nin", field.getValue());
          conditions.put(field.getFieldName().toString(), matcher);
        } else if (field.getMatchRule().equals(MatchRule.EQUALS)) {
          conditions.put(field.getFieldName().toString(), field.getValue());
        } else if (field.getMatchRule().equals(MatchRule.GREATER_THAN_EQUAL)) {
          matcher.append("$gte", field.getValue());
          conditions.put(field.getFieldName().toString(), matcher);
        } else if (field.getMatchRule().equals(MatchRule.LESS_THAN_EQUAL)) {
          matcher.append("$lte", field.getValue());
          conditions.put(field.getFieldName().toString(), matcher);
        } else if (field.getMatchRule().equals(MatchRule.BETWEEN)) {
          if (!(field.getValue() instanceof Range)) {
            throw new IllegalArgumentException("\"Between\" match value must be a Range.");
          }

          Range<?> range = (Range<?>) field.getValue();

          Document between = new Document("$gte", range.lowerEndpoint()).append("$lte", range.upperEndpoint());
          conditions.put(field.getFieldName().toString(), between);
        }
      }
    }

    return conditions;
  }

  @Override
  public CompletableFuture<List<Result>> query(QuerySession session, boolean translate) throws Exception {
    Query query = session.getQuery();
    checkNotNull(query);

    // Prepare results
    List<Result> results = new ArrayList<>();
    CompletableFuture<List<Result>> future = new CompletableFuture<>();

    // Get collection
    MongoCollection<Document> collection = MongoStorageAdapter.getCollection(MongoStorageAdapter.collectionEventRecordsName);

    // Append all conditions
    Document matcher = new Document("$match", buildConditions(query.getConditions()));

    // Sorting. Newest first for rollback and oldest first for restore.
    Document sortFields = new Document();
    sortFields.put(DataQueries.Created.toString(), session.getSortBy().getValue());
    Document sorter = new Document("$sort", sortFields);

    // Offset/Limit
    Document limit = new Document("$limit", query.getLimit());

    // Build aggregators
    final AggregateIterable<Document> aggregated;
    if (!session.hasFlag(Flag.NO_GROUP)) {
      // Grouping fields
      Document groupFields = new Document();
      groupFields.put(DataQueries.EventName.toString(), "$" + DataQueries.EventName);
      groupFields.put(DataQueries.Player.toString(), "$" + DataQueries.Player);
      groupFields.put(DataQueries.Cause.toString(), "$" + DataQueries.Cause);
      groupFields.put(DataQueries.Target.toString(), "$" + DataQueries.Target);
      // Entity
      groupFields.put(DataQueries.Entity.toString(), "$" + DataQueries.Entity.then(DataQueries.EntityType));
      // Day
      groupFields.put("dayOfMonth", new Document("$dayOfMonth", "$" + DataQueries.Created));
      groupFields.put("month", new Document("$month", "$" + DataQueries.Created));
      groupFields.put("year", new Document("$year", "$" + DataQueries.Created));

      Document groupHolder = new Document("_id", groupFields);
      groupHolder.put(DataQueries.Count.toString(), new Document("$sum", 1));

      Document group = new Document("$group", groupHolder);

      // Aggregation pipeline
      List<Document> pipeline = new ArrayList<>();
      pipeline.add(matcher);
      pipeline.add(group);
      pipeline.add(sorter);
      pipeline.add(limit);

      aggregated = collection.aggregate(pipeline);
      SpongeGriefAlert.getSpongeInstance().getLogger().debug("MongoDB Query: " + pipeline);
    } else {
      // Aggregation pipeline
      List<Document> pipeline = new ArrayList<>();
      pipeline.add(matcher);
      pipeline.add(sorter);
      pipeline.add(limit);

      aggregated = collection.aggregate(pipeline);
      SpongeGriefAlert.getSpongeInstance().getLogger().debug("MongoDB Query: " + pipeline);
    }

    // Iterate results and build our event record list
    try (MongoCursor<Document> cursor = aggregated.iterator()) {
      List<UUID> uuidsPendingLookup = new ArrayList<>();

      while (cursor.hasNext()) {
        // Mongo document
        Document wrapper = cursor.next();
        Document document = session.hasFlag(Flag.NO_GROUP) ? wrapper : (Document) wrapper.get("_id");

        DataContainer data = documentToDataContainer(document);

        if (!session.hasFlag(Flag.NO_GROUP)) {
          data.set(DataQueries.Count, wrapper.get(DataQueries.Count.toString()));
        }

        // Build our result object
        Result result = Result.from(wrapper.getString(DataQueries.EventName.toString()), !session.hasFlag(Flag.NO_GROUP));

        // Determine the final name of the event source
        if (document.containsKey(DataQueries.Player.toString())) {
          String uuid = document.getString(DataQueries.Player.toString());
          data.set(DataQueries.Cause, uuid);

          if (translate) {
            uuidsPendingLookup.add(UUID.fromString(uuid));
          }
        } else {
          data.set(DataQueries.Cause, document.getString(DataQueries.Cause.toString()));
        }

        result.data = data;
        results.add(result);
      }

      if (translate && !uuidsPendingLookup.isEmpty()) {
        DataUtil.translateUuidsToNames(results, uuidsPendingLookup).thenAccept(future::complete);
      } else {
        future.complete(results);
      }
    }

    return future;
  }

  /**
   * Given a list of parameters, will remove all matching records.
   *
   * @param query Query conditions indicating what we're purging
   * @return
   */
  // @todo implement
  @Override
  public StorageDeleteResult delete(Query query) {
    return new StorageDeleteResult();
  }
}
