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

package com.minecraftonline.griefalert.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helion3.prism.api.query.ConditionGroup;
import com.helion3.prism.api.query.FieldCondition;
import com.helion3.prism.api.query.MatchRule;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.util.DataQueries;
import com.minecraftonline.griefalert.api.data.SignText;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.sponge.SpongeWorld;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
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
   * Using a {@link Player} as an extension of a
   * {@link org.spongepowered.api.command.CommandSource},
   * build a <code>Prism</code> session to query through Prism. This is used for querying records
   * and rollback.
   *
   * @param player the player executing the command
   * @param args   the arguments of the commmand
   * @param flags  the map of flags to store information about what command was run
   * @return An optional of the built {@link QuerySession}. Empty if it couldn't be created.
   */
  public static CompletableFuture<Optional<QuerySession>> buildSession(Player player,
                                                                       CommandContext args,
                                                                       Map<Text, Text> flags) {
    return CompletableFuture.supplyAsync(() -> {
      final QuerySession session = new QuerySession(player);

      Query query = session.newQuery();
      // Add location query with WE
      World world = player.getLocation().getExtent();
      SpongeWorld spongeWorld = SpongeWorldEdit.inst().getWorld(world);
      try {
        query.addCondition(ConditionGroup.from(
            world,
            WorldEditUtil.convertVector(
                SpongeWorldEdit.inst().getSession(player)
                    .getSelection(spongeWorld).getMinimumPoint()),
            WorldEditUtil.convertVector(
                SpongeWorldEdit.inst().getSession(player)
                    .getSelection(spongeWorld).getMaximumPoint())));
      } catch (IncompleteRegionException e) {
        player.sendMessage(Format.error("No region selected"));
        return Optional.empty();
      }

      // Parse the 'since' with the given date format, or just do a year ago
      Date since = args.<String>getOne(CommandKeys.SINCE.get()).flatMap(str -> {
        try {
          Date out = DateUtil.parseAnyDate(str);
          flags.put(CommandKeys.SINCE.get(), Text.of(Format.date(out)));
          return Optional.of(out);
        } catch (IllegalArgumentException e) {
          player.sendMessage(Format.error(e.getMessage()));
          return Optional.empty();
        }
      }).orElseGet(() -> {
        Date out = Date.from(Instant.now().minus(Duration.ofDays(5)));
        flags.put(CommandKeys.SINCE.get(), Format.date(out));
        return out;
      });
      query.addCondition(FieldCondition.of(
          DataQueries.Created,
          MatchRule.GREATER_THAN_EQUAL,
          since));

      args.<String>getOne(CommandKeys.BEFORE.get()).ifPresent(str -> {
        Date before = DateUtil.parseAnyDate(str);
        flags.put(CommandKeys.BEFORE.get(), Text.of(Format.date(before)));
        query.addCondition(FieldCondition.of(
            DataQueries.Created,
            MatchRule.LESS_THAN_EQUAL,
            before));
      });

      args.<String>getOne(CommandKeys.PRISM_TARGET.get()).ifPresent(str -> {
        flags.put(CommandKeys.PRISM_TARGET.get(), Text.of(str));
        query.addCondition(FieldCondition.of(
            DataQueries.Target,
            MatchRule.EQUALS,
            str.replaceAll("_", " ")));
      });

      Optional<String> playerOptional = args.getOne(CommandKeys.PLAYER.get());
      if (playerOptional.isPresent()) {
        try {
          GameProfile gameProfile = Sponge.getServer()
              .getGameProfileManager()
              .get(playerOptional.get())
              .get();
          flags.put(CommandKeys.PLAYER.get(), Text.of(playerOptional.get()));
          query.addCondition(FieldCondition.of(
              DataQueries.Player,
              MatchRule.EQUALS,
              gameProfile.getUniqueId().toString()));
        } catch (ExecutionException e) {
          player.sendMessage(Format.error("Player not found"));
          return Optional.empty();
        } catch (Exception e) {
          player.sendMessage(Format.error("Error trying to access game profile"));
          e.printStackTrace();
        }
      }

      args.<String>getOne(CommandKeys.PRISM_EVENT.get()).ifPresent(str -> {
        flags.put(CommandKeys.PRISM_EVENT.get(), Text.of(str));
        query.addCondition(FieldCondition.of(
            DataQueries.EventName,
            MatchRule.EQUALS,
            str));
      });

      return Optional.of(session);
    });
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
