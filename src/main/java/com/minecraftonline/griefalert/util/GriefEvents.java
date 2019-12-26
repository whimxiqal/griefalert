package com.minecraftonline.griefalert.util;

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

}
