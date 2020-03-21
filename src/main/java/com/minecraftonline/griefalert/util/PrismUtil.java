/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helion3.prism.util.DataQueries;
import com.minecraftonline.griefalert.api.data.SignText;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


public final class PrismUtil {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private PrismUtil() {
  }

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

  /**
   * Get the location from a <code>PrismRecord</code>, if it exists.
   *
   * @param record the record
   * @return the optional location
   */
  @SuppressWarnings("all")
  public static Optional<Location<World>> getLocation(PrismRecordArchived record) {
    Optional<DataView> locationView = record.getDataContainer().getView(DataQueries.Location);

    Optional<World> world = locationView
        .flatMap((view) -> view.getString(DataQueries.WorldUuid))
        .flatMap((s) -> Sponge.getServer().getWorld(UUID.fromString(s)));

    Optional<Integer> x = locationView.flatMap((view) -> view.getInt(DataQueries.X));
    Optional<Integer> y = locationView.flatMap((view) -> view.getInt(DataQueries.Y));
    Optional<Integer> z = locationView.flatMap((view) -> view.getInt(DataQueries.Z));
    if (!Optionals.allPresent(world, x, y, z)) {
      return Optional.empty();
    }

    return Optional.of(new Location<>(world.get(), x.get(), y.get(), z.get()));

  }

  /**
   * If a broken sign exists, parse its lines and add it to a <code>SignText</code>.
   *
   * @param record the record to search through
   * @return an optional of the <code>SignText</code>
   */
  public static Optional<SignText> getBrokenSignText(PrismRecordArchived record) {
    Optional<DataView> originalOptional = record.getDataContainer()
        .getView(DataQueries.OriginalBlock);

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

  /**
   * Print a readable version of all data within the <code>PrismRecord</code>.
   *
   * @param record the record
   * @return a string with all data
   */
  public static String printRecord(PrismRecordArchived record) {
    List<String> lines = new LinkedList<>();
    for (DataQuery query : record.getDataContainer().getKeys(false)) {
      lines.add(query.asString(" ") + ": " + record.getDataContainer().get(query));
    }
    return String.join("\n", lines);
  }

  /**
   * Get the original state of the block from a <code>PrismRecord</code>.
   *
   * @param record the record
   * @return the <code>BlockState</code> found
   */
  public static Optional<BlockState> getOriginalBlockState(PrismRecordArchived record) {
    return record.getDataContainer().getView(DataQueries.OriginalBlock)
        .flatMap(view -> view.getView(DataQueries.BlockState))
        .flatMap(view -> BlockState.builder().build(view));

  }

  /**
   * Get the replacement state of the block from a <code>PrismRecord</code>.
   *
   * @param record the record
   * @return the <code>BlockState</code> found
   */
  public static Optional<BlockState> getReplacementBlock(PrismRecordArchived record) {
    return record.getDataContainer().getView(DataQueries.ReplacementBlock)
        .flatMap(view -> view.getView(DataQueries.BlockState))
        .flatMap(view -> BlockState.builder().build(view));

  }

  public static Optional<Date> getCreated(PrismRecordArchived prismRecord) {
    Optional<Object> dateOptional = prismRecord.getDataContainer().get(DataQueries.Created);
    return dateOptional.map(object -> (Date) object);
  }
}
