package com.minecraftonline.griefalert.util;

import com.helion3.prism.api.data.PrismEvent;
import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.api.data.GriefEvent;

import java.util.Optional;

public final class GriefEvents {

  private GriefEvents() {
  }

  public static final GriefEvent BREAK = GriefEvent.of(PrismEvents.BLOCK_BREAK);
  public static final GriefEvent PLACE = GriefEvent.of(PrismEvents.BLOCK_PLACE);
  public static final GriefEvent DEATH = GriefEvent.of(PrismEvents.ENTITY_DEATH);
  public static final GriefEvent ITEM_USE = GriefEvent.of("use", "Item Use", "used");
  public static final GriefEvent INTERACT = GriefEvent.of("interact", "Interact", "interacted");

  public static class Registry {

    public static Optional<GriefEvent> of(String id) throws IllegalArgumentException {
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
        default:
          return Optional.empty();
      }
    }

    public static Optional<GriefEvent> of(PrismEvent prismEvent) throws IllegalArgumentException {
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

  }

}
