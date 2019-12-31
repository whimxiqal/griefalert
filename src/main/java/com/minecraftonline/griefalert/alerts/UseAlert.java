package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;

public class UseAlert extends SpongeAlert {

  private UseAlert(GriefProfile griefProfile, InteractItemEvent.Secondary event) {
    super(griefProfile, event);
  }

  public static UseAlert of(GriefProfile griefProfile, InteractItemEvent.Secondary event) {
    return new UseAlert(griefProfile, event);
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.ITEM_USE;
  }

}
