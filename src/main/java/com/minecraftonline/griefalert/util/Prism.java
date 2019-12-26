package com.minecraftonline.griefalert.util;

import com.google.inject.internal.cglib.reflect.$FastClass;
import com.helion3.prism.api.data.PrismEvent;
import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.util.DataQueries;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.crypto.Data;

import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.GriefAlert;
import jdk.nashorn.internal.ir.Block;
import org.slf4j.Logger;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
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
  public static Optional<String> getPlayerUuid(PrismRecord record) {
    return record.getDataContainer().getString(DataQueries.Player);
  }

  public static Optional<Player> getPlayer(PrismRecord record) {
    return Optional.empty();
  }

  public static Optional<String> getTarget(PrismRecord record) {
    return record.getDataContainer().getString(DataQueries.Target);
  }

//  public static Optional<Location<World>> getLocation(PrismRecord record) {
//    Optional<Integer> x = record.getDataContainer().getInt(DataQueries.X);
//    Optional<Integer> y = record.getDataContainer().getInt(DataQueries.Y);
//    Optional<Integer> z = record.getDataContainer().getInt(DataQueries.Z);
//    Optional<String> worldUuid = record.getDataContainer().getString(DataQueries.WorldUuid);
//    if (!x.isPresent() || !y.isPresent() || !z.isPresent() || !worldUuid.isPresent()) {
//      return Optional.empty();
//    }
//    Optional<World> world = Sponge.getServer().getWorld(worldUuid.get());
//    if (!world.isPresent()) {
//      return Optional.empty();
//    }
//    // TODO Return the Location!
//
//  }

//  /**
//   * Get the BlockType of the event from the PrismRecord.
//   *
//   * @param record The PrismRecord which houses all event data
//   * @return The BlockType from the event, or null if none exists
//   */
//  public static Optional<BlockType> getBlockType(PrismRecord record) {
//    return record.getDataContainer().getObject(DataQueries.BlockType, BlockType.class);
//  }

//  /**
//   * Get the EventName of the event from the PrismRecord.
//   *
//   * @param record The PrismRecord which houses all event data
//   * @return The EventName from the event, or null if none exists
//   */
//  public static Optional<String> getEventName(PrismRecord record) {
//    return record.getDataContainer().getObject(DataQueries.EventName, String.class);
//  }

  /**
   * Get the BlockSnapshot of the original block of the event from the PrismRecord.
   *
   * @param record The PrismRecord which houses all event data
   * @return The BlockSnapshot from the event, or null if none exists
   */
  public static Optional<BlockType> getOriginalBlockType(PrismRecord record) {
    Sponge.getServer().getBroadcastChannel().send(Format.heading("getOriginalBlockType"));
    Optional<DataContainer> originalBlockDataContainer = record.getDataContainer().getObject(DataQueries.ReplacementBlock, DataContainer.class);
    if (originalBlockDataContainer.isPresent()) {
      Sponge.getServer().getBroadcastChannel().send(Format.message("originalBlockDataContainer found!"));
      Optional<DataContainer> blockStateDataContainer = originalBlockDataContainer.get().getObject(DataQueries.BlockState, DataContainer.class);
      if (blockStateDataContainer.isPresent()) {
        Sponge.getServer().getBroadcastChannel().send(Format.message("blockStateDataContainer found!"));
        return blockStateDataContainer.get().getObject(DataQueries.BlockType, BlockType.class);
      }
    }
    return Optional.empty();
  }

  /**
   * Get the BlockSnapshot of the replacement block of the event from the PrismRecord.
   *
   * @param record The PrismRecord which houses all event data
   * @return The BlockSnapshot from the event, or null if none exists
   */
  public static Optional<BlockType> getReplacementBlockType(PrismRecord record) {
    Optional<DataContainer> replacementBlockDataContainer = record.getDataContainer().getObject(DataQueries.ReplacementBlock, DataContainer.class);
    if (replacementBlockDataContainer.isPresent()) {
      Optional<DataContainer> blockStateDataContainer = replacementBlockDataContainer.get().getObject(DataQueries.BlockState, DataContainer.class);
      if (blockStateDataContainer.isPresent()) {
        return blockStateDataContainer.get().getObject(DataQueries.BlockType, BlockType.class);
      }
    }
    return Optional.empty();
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

  /**
   * Get the UUID of the world of the event from the PrismRecord.
   *
   * @param record The PrismRecord which houses all event data
   * @return The UUID from the event, or null if none exists
   */
  public static Optional<UUID> getWorldUuid(PrismRecord record) {
    return record.getDataContainer().getObject(DataQueries.WorldUuid, UUID.class);
  }

  public static Optional<Location<World>> getLocation(PrismRecord record) {
    try {
      return record.getDataContainer().getObject(DataQueries.Location, Location.class).map(location -> (Location<World>) location);
    } catch (ClassCastException e) {
      GriefAlert.getInstance().getLogger().error("Location from PrismRecord was not of generic type World");
      return Optional.empty();
    }
  }

  public static Optional<String> getGriefedObjectId(PrismRecord record) {
//    Optional<String> eventNameOptional = Prism.getEventName(record);
//    if (!eventNameOptional.isPresent()) {
//      Sponge.getServer().getBroadcastChannel().send(Format.message("No event name found within record, " + record.getEvent()));
//      return Optional.empty();
//    }
//    Sponge.getServer().getBroadcastChannel().send(
//        Format.heading("getGriefedObjectId"),
//        Format.message(": " + eventNameOptional.get())
//    );
    Optional<PrismEvent> eventOptional = Registry.getPrismEvent(record.getEvent());
    if (eventOptional.isPresent()) {
      PrismEvent event = eventOptional.get();
      Sponge.getServer().getBroadcastChannel().send(
          Format.heading("getGriefedObjectId"),
          Format.message(": " + event.getName())
      );
      if (event.equals(PrismEvents.BLOCK_PLACE)) {
        Optional<BlockType> blockTypeOptional = getReplacementBlockType(record);
        return blockTypeOptional.map(BlockType::getId);
      } else if (event.equals(PrismEvents.BLOCK_GROW)) {
        Optional<BlockType> blockTypeOptional = getOriginalBlockType(record);
        return blockTypeOptional.map(BlockType::getId);
      } else if (event.equals(PrismEvents.BLOCK_BREAK)) {
        Optional<BlockType> blockTypeOptional = getOriginalBlockType(record);
        if (!blockTypeOptional.isPresent()) {
          Sponge.getServer().getBroadcastChannel().send(Format.message("No BlockSnapshot found!"));
        }
        return blockTypeOptional.map(BlockType::getId);
      } else if (event.equals(PrismEvents.BLOCK_DECAY)) {
        Optional<BlockType> blockTypeOptional = getOriginalBlockType(record);
        return blockTypeOptional.map(BlockType::getId);
      } else if (event.equals(PrismEvents.ENTITY_DEATH)) {
        Optional<EntityType> blockTypeOptional = getEntityType(record);
        return blockTypeOptional.map(EntityType::getId);
      }
    }
    return Optional.empty();
  }

  public static Optional<String> getGriefedObjectName(PrismRecord record) {

    Logger l = GriefAlert.getInstance().getLogger();
    l.info(Format.message("getGriefedObjectName run").toPlain());

    Optional<PrismEvent> eventOptional = Registry.getPrismEvent(record.getEvent());
    if (eventOptional.isPresent()) {

      PrismEvent event = eventOptional.get();
      if (event.equals(PrismEvents.BLOCK_PLACE)) {
        l.info(Format.message("Found 'Place'").toPlain());
        return getReplacementBlockType(record).map(
            blockType -> blockType.getTranslation().get()
        );
      } else if (event.equals(PrismEvents.BLOCK_GROW)) {
        l.info(Format.message("Found 'Grow'").toPlain());
        return getOriginalBlockType(record).map(
            blockType -> blockType.getTranslation().get()
        );
      } else if (event.equals(PrismEvents.BLOCK_BREAK)) {
        l.info(Format.message("Found 'Break'").toPlain());
        return getOriginalBlockType(record).map(
            blockType -> blockType.getTranslation().get()
        );
      } else if (event.equals(PrismEvents.BLOCK_DECAY)) {
        l.info(Format.message("Found 'Decay'").toPlain());
        return getOriginalBlockType(record).map(
            blockType -> blockType.getTranslation().get()
        );
      } else if (event.equals(PrismEvents.ENTITY_DEATH)) {
        l.info(Format.message("Found 'Death'").toPlain());
        return getEntityType(record).map(
            entityType -> entityType.getTranslation().get()
        );
      }
    } else {
      l.info(Format.message("Registry could not find a valid GriefEvent").toPlain());
    }

    l.info(Format.message("Returning Optional.empty()").toPlain());
    return Optional.empty();

  }

  public static Optional<List<String>> getPlacedSignLines(PrismRecord record) {
    return Optional.of(Arrays.asList("Line1", "Line2", "Line3", "Line4"));
  }

  public static Optional<List<String>> getBrokenSignLines(PrismRecord record) {
    return Optional.of(Arrays.asList("Line1", "Line2", "Line3", "Line4"));
  }



  @Nullable
  public static class Registry {

    /**
     * Return the PrismEvent associated with the given event name.
     *
     * @param eventName The name of the PrismEvent
     * @return The PrismEvent or null if none exists
     */
    public static Optional<PrismEvent> getPrismEvent(@Nonnull String eventName) {
      switch (eventName.toLowerCase()) {
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
