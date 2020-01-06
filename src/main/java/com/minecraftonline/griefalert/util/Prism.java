package com.minecraftonline.griefalert.util;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helion3.prism.api.data.PrismEvent;
import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.util.DataQueries;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.api.data.SignText;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


public abstract class Prism {

  /**
   * Get the player as a cause of the event from the PrismRecord. Return null
   * if none exists.
   *
   * @param record The PrismRecord which houses all event data
   * @return The player which caused the event, or null if none exists
   */
  public static Optional<String> getPlayerUuid(PrismRecordArchived record) {
    return record.getDataContainer().getString(DataQueries.Player);
  }

  public static Optional<String> getTarget(PrismRecordArchived record) {
    return record.getDataContainer().getString(DataQueries.Target);
  }

  public static Optional<Location<World>> getLocation(PrismRecordArchived record) {
    Optional<DataView> locationView = record.getDataContainer().getView(DataQueries.Location);
    if (!locationView.isPresent()) {
      return Optional.empty();
    }

    Optional<String> worldUuid = locationView.get().getString(DataQueries.WorldUuid);
    if (!worldUuid.isPresent()) {
      return Optional.empty();
    }

    Optional<World> world = Sponge.getServer().getWorld(UUID.fromString(worldUuid.get()));
    Optional<Integer> x = locationView.get().getInt(DataQueries.X);
    Optional<Integer> y = locationView.get().getInt(DataQueries.Y);
    Optional<Integer> z = locationView.get().getInt(DataQueries.Z);
    if (!Optionals.allPresent(world, x, y, z)) {
      return Optional.empty();
    }

    return Optional.of(new Location<>(world.get(), x.get(), y.get(), z.get()));

  }

  /**
   * Get the EntityType of the entity in the event from the PrismRecord.
   *
   * @param record The PrismRecord which houses all event data
   * @return The EntityType from the event, or null if none exists
   */
  public static Optional<EntityType> getEntityType(PrismRecordArchived record) {
    return record.getDataContainer().getObject(DataQueries.EntityType, EntityType.class);
  }

  public static Optional<SignText> getBrokenSignLines(PrismRecordArchived record) {
    Optional<DataView> originalOptional = record.getDataContainer().getView(DataQueries.OriginalBlock);

    if (!originalOptional.isPresent()) {
      return Optional.empty();
    }

    Optional<DataView> unsafeOptional = originalOptional.get().getView(DataQueries.UnsafeData);

    if (!unsafeOptional.isPresent()) {
      return Optional.empty();
    }

    Optional<String> text1 = unsafeOptional.get().getString(DataQuery.of("Text1")).map((text) ->
        new Gson().fromJson(
            text,
            JsonObject.class).get("text").getAsString());
    Optional<String> text2 = unsafeOptional.get().getString(DataQuery.of("Text2")).map((text) ->
        new Gson().fromJson(
            text,
            JsonObject.class).get("text").getAsString());
    Optional<String> text3 = unsafeOptional.get().getString(DataQuery.of("Text3")).map((text) ->
        new Gson().fromJson(
            text,
            JsonObject.class).get("text").getAsString());
    Optional<String> text4 = unsafeOptional.get().getString(DataQuery.of("Text4")).map((text) ->
        new Gson().fromJson(
            text,
            JsonObject.class).get("text").getAsString());
    return Optional.of(SignText.of(
        text1.orElse(null),
        text2.orElse(null),
        text3.orElse(null),
        text4.orElse(null)));


  }

  public static String printRecord(PrismRecordArchived record) {
    List<String> lines = new LinkedList<>();
    for (DataQuery query : record.getDataContainer().getKeys(false)) {
      lines.add(query.asString(" ") + ": " + record.getDataContainer().get(query));
    }
    return String.join("\n", lines);
  }

  public static Optional<BlockState> getOriginalBlock(PrismRecordArchived record) {
    return record.getDataContainer().getView(DataQueries.OriginalBlock)
        .flatMap(view -> view.getView(DataQueries.BlockState))
        .flatMap(view -> BlockState.builder().build(view));

  }

  public static Optional<BlockState> getReplacementBlock(PrismRecordArchived record) {
    return record.getDataContainer().getView(DataQueries.ReplacementBlock)
        .flatMap(view -> view.getView(DataQueries.BlockState))
        .flatMap(view -> BlockState.builder().build(view));

  }
}
