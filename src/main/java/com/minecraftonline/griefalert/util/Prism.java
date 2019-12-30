package com.minecraftonline.griefalert.util;

import com.helion3.prism.api.data.PrismEvent;
import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.util.DataQueries;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helion3.prism.util.PrismEvents;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


public abstract class Prism {

  // TODO: Finish these Prism helper functions to access PrismRecord data

  /**
   * Get the player as a cause of the event from the PrismRecord. Return null
   * if none exists.
   *
   * @param record The PrismRecord which houses all event data
   * @return The player which caused the event, or null if none exists
   */
  public static Optional<String> getPlayerUuid(PrismRecord record) {
    return record.getDataContainer().getString(DataQueries.Player);
  }

  public static Optional<String> getTarget(PrismRecord record) {
    return record.getDataContainer().getString(DataQueries.Target);
  }

  public static Optional<Location<World>> getLocation(PrismRecord record) {
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
  public static Optional<EntityType> getEntityType(PrismRecord record) {
    return record.getDataContainer().getObject(DataQueries.EntityType, EntityType.class);
  }

  public static Optional<List<String>> getPlacedSignLines(PrismRecord record) {
    return Optional.of(Arrays.asList("Line1", "Line2", "Line3", "Line4"));
  }

  public static Optional<List<String>> getBrokenSignLines(PrismRecord record) {
    return Optional.of(Arrays.asList("Line1", "Line2", "Line3", "Line4"));
  }

  public static String printRecord(PrismRecord record) {
    List<String> lines = new LinkedList<>();
    for (DataQuery query : record.getDataContainer().getKeys(false)) {
      lines.add(query.asString(" ") + ": " + record.getDataContainer().get(query));
    }
    return String.join("\n", lines);
  }


  @Nullable
  public static class Registry {

    /**
     * Return the PrismEvent associated with the given event name.
     *
     * @param eventId The name of the PrismEvent
     * @return The PrismEvent or null if none exists
     */
    public static Optional<PrismEvent> getPrismEvent(@Nonnull String eventId) {
      switch (eventId.toLowerCase()) {
        case "break":
          return Optional.of(PrismEvents.BLOCK_BREAK);
        case "decay":
          return Optional.of(PrismEvents.BLOCK_DECAY);
        case "grow":
          return Optional.of(PrismEvents.BLOCK_GROW);
        case "place":
          return Optional.of(PrismEvents.BLOCK_PLACE);
        case "death":
          return Optional.of(PrismEvents.ENTITY_DEATH);
        case "command":
          return Optional.of(PrismEvents.COMMAND_EXECUTE);
        case "close":
          return Optional.of(PrismEvents.INVENTORY_CLOSE);
        case "open":
          return Optional.of(PrismEvents.INVENTORY_OPEN);
        case "drop":
          return Optional.of(PrismEvents.ITEM_DROP);
        case "insert":
          return Optional.of(PrismEvents.ITEM_INSERT);
        case "pickup":
          return Optional.of(PrismEvents.ITEM_PICKUP);
        case "remove":
          return Optional.of(PrismEvents.ITEM_REMOVE);
        case "disconnect":
          return Optional.of(PrismEvents.PLAYER_DISCONNECT);
        case "join":
          return Optional.of(PrismEvents.PLAYER_JOIN);
        default:
          return Optional.empty();
      }
    }
  }

}
