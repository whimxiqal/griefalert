package com.minecraftonline.griefalert.util;

import com.helion3.prism.api.data.PrismEvent;
import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.api.data.GriefEvent;

public final class GriefEvents {

  private GriefEvents() {
  }

  public static final GriefEvent BREAK = GriefEvent.of(PrismEvents.BLOCK_BREAK);
  public static final GriefEvent PLACE = GriefEvent.of(PrismEvents.BLOCK_PLACE);
  public static final GriefEvent DEATH = GriefEvent.of(PrismEvents.ENTITY_DEATH);
  public static final GriefEvent ITEM_USE = GriefEvent.of("use", "Item Use", "used");
  public static final GriefEvent INTERACT = GriefEvent.of("interact", "Interact", "interacted");

  public static class Registry {

    public static GriefEvent of(String event) throws IllegalArgumentException {
      switch (event.toLowerCase()) {
        case "break":
          return GriefEvents.BREAK;
        case "place":
          return GriefEvents.PLACE;
        case "death":
          return GriefEvents.DEATH;
        case "use":
          return GriefEvents.ITEM_USE;
        case "interact":
          return GriefEvents.INTERACT;
        default:
          throw new IllegalArgumentException("Invalid event name: " + event);
      }
    }

    public static GriefEvent of(PrismEvent prismEvent) throws IllegalArgumentException {
      if (prismEvent == PrismEvents.BLOCK_BREAK) {
        return GriefEvents.BREAK;
      } else if (prismEvent == PrismEvents.BLOCK_PLACE) {
        return GriefEvents.PLACE;
      } else if (prismEvent == PrismEvents.ENTITY_DEATH) {
        return GriefEvents.DEATH;
      } else {
        throw new IllegalArgumentException("Incompatible PrismEvent with GriefEvent: " + prismEvent.getName());
      }
    }

  }

}
