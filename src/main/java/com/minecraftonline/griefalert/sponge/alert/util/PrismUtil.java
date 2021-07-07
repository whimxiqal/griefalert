/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert.sponge.alert.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.minecraftonline.griefalert.common.alert.struct.SignText;
import com.minecraftonline.griefalert.sponge.data.util.DataQueries;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A utility class for managing Prism objects.
 */
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
  public static Optional<String> getPlayerUuid(DataContainer record) {
    return record.getString(DataQueries.Player);
  }

  public static Optional<String> getTarget(DataContainer record) {
    return record.getString(DataQueries.Target);
  }

  /**
   * Get the location from a <code>PrismRecord</code>, if it exists.
   *
   * @param record the record
   * @return the optional location
   */
  @SuppressWarnings("all")
  public static Optional<Location<World>> getLocation(DataContainer record) {
    Optional<DataView> locationView = record.getView(DataQueries.Location);

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
  public static Optional<SignText> getBrokenSignText(DataContainer record) {
    Optional<DataView> originalOptional = record.getView(DataQueries.OriginalBlock);

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
  public static String printRecord(DataContainer record) {
    List<String> lines = new LinkedList<>();
    for (DataQuery query : record.getKeys(false)) {
      lines.add(query.asString(" ") + ": " + record.get(query));
    }
    return String.join("\n", lines);
  }

  /**
   * Get the original state of the block from a <code>PrismRecord</code>.
   *
   * @param record the record
   * @return the <code>BlockState</code> found
   */
  public static Optional<BlockState> getOriginalBlockState(DataContainer record) {
    return record.getView(DataQueries.OriginalBlock)
        .flatMap(view -> view.getView(DataQueries.BlockState))
        .flatMap(view -> BlockState.builder().build(view));

  }

  /**
   * Get the replacement state of the block from a <code>PrismRecord</code>.
   *
   * @param record the record
   * @return the <code>BlockState</code> found
   */
  public static Optional<BlockState> getReplacementBlock(DataContainer record) {
    return record.getView(DataQueries.ReplacementBlock)
        .flatMap(view -> view.getView(DataQueries.BlockState))
        .flatMap(view -> BlockState.builder().build(view));

  }

  public static Optional<Date> getCreated(DataContainer prismRecord) {
    Optional<Object> dateOptional = prismRecord.get(DataQueries.Created);
    return dateOptional.map(object -> (Date) object);
  }

}
