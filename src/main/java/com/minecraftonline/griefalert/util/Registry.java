package com.minecraftonline.griefalert.util;

import com.helion3.prism.api.data.PrismEvent;
import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;

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

}
