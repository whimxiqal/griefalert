package com.minecraftonline.griefalert.util;

import com.helion3.prism.api.data.PrismEvent;
import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;

import javax.annotation.Nonnull;
import java.util.Optional;

public class Registry {

  public static Optional<GriefEvent> lookupGriefEvent(String id) {
    switch (id.toLowerCase()) {
      case "break":
        return Optional.of(GriefEvents.BREAK);
      case "place":
        return Optional.of(GriefEvents.PLACE);
      case "death":
        return Optional.of(GriefEvents.DEATH);
      case "use":
        return Optional.of(GriefEvents.ITEM_USE);
      case "interact":
        return Optional.of(GriefEvents.INTERACT);
      case "attack":
        return Optional.of(GriefEvents.ATTACK);
      default:
        return Optional.empty();
    }
  }

  public static Optional<GriefEvent> lookupGriefEvent(PrismEvent prismEvent) {
    if (prismEvent == PrismEvents.BLOCK_BREAK) {
      return Optional.of(GriefEvents.BREAK);
    } else if (prismEvent == PrismEvents.BLOCK_PLACE) {
      return Optional.of(GriefEvents.PLACE);
    } else if (prismEvent == PrismEvents.ENTITY_DEATH) {
      return Optional.of(GriefEvents.DEATH);
    } else {
      return Optional.empty();
    }
  }

  public static Optional<DimensionType> lookupDimensionType(String id) {
    switch (id.toLowerCase().replace("minecraft:", "")) {
      case "overworld":
        return Optional.of(DimensionTypes.OVERWORLD);
      case "nether":
        return Optional.of(DimensionTypes.NETHER);
      case "the_end":
        return Optional.of(DimensionTypes.THE_END);
      default:
        return Optional.empty();
    }
  }

  public static Optional<TextColor> lookupTextColor(String id) {
    switch (id.toLowerCase()) {
      case "black":
        return Optional.of(TextColors.BLACK);
      case "dark_blue":
        return Optional.of(TextColors.DARK_BLUE);
      case "dark_green":
        return Optional.of(TextColors.DARK_GREEN);
      case "dark_aqua":
        return Optional.of(TextColors.DARK_AQUA);
      case "dark_red":
        return Optional.of(TextColors.DARK_RED);
      case "dark_purple":
        return Optional.of(TextColors.DARK_PURPLE);
      case "gold":
        return Optional.of(TextColors.GOLD);
      case "gray":
        return Optional.of(TextColors.GRAY);
      case "dark_gray":
        return Optional.of(TextColors.DARK_GRAY);
      case "blue":
        return Optional.of(TextColors.BLUE);
      case "green":
        return Optional.of(TextColors.GREEN);
      case "aqua":
        return Optional.of(TextColors.AQUA);
      case "red":
        return Optional.of(TextColors.RED);
      case "light_purple":
        return Optional.of(TextColors.LIGHT_PURPLE);
      case "yellow":
        return Optional.of(TextColors.YELLOW);
      case "white":
        return Optional.of(TextColors.WHITE);
      default:
        return Optional.empty();
    }
  }

  public static Optional<TextColor> lookupTextColor(char id) {
    switch (Character.toUpperCase(id)) {
      case '0':
        return Optional.of(TextColors.BLACK);
      case '1':
        return Optional.of(TextColors.DARK_BLUE);
      case '2':
        return Optional.of(TextColors.DARK_GREEN);
      case '3':
        return Optional.of(TextColors.DARK_AQUA);
      case '4':
        return Optional.of(TextColors.DARK_RED);
      case '5':
        return Optional.of(TextColors.DARK_PURPLE);
      case '6':
        return Optional.of(TextColors.GOLD);
      case '7':
        return Optional.of(TextColors.GRAY);
      case '8':
        return Optional.of(TextColors.DARK_GRAY);
      case '9':
        return Optional.of(TextColors.BLUE);
      case 'A':
        return Optional.of(TextColors.GREEN);
      case 'B':
        return Optional.of(TextColors.AQUA);
      case 'C':
        return Optional.of(TextColors.RED);
      case 'D':
        return Optional.of(TextColors.LIGHT_PURPLE);
      case 'E':
        return Optional.of(TextColors.YELLOW);
      case 'F':
        return Optional.of(TextColors.WHITE);
      default:
        return Optional.empty();
    }
  }

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
