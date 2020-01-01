/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.alerts.sponge.SpongeAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.event.entity.InteractEntityEvent;

public class InteractEntityAlert extends SpongeAlert {

  InteractEntityAlert(GriefProfile griefProfile, InteractEntityEvent.Secondary event) {
    super(griefProfile, event);
  }

  /**
   * Generator for the appropriate InteractEntityAlert.
   * @param griefProfile The GriefProfile flagging this event
   * @param event The event
   * @return The appropriate InteractEntityAlert
   */
  public static InteractEntityAlert of(
      GriefProfile griefProfile,
      InteractEntityEvent.Secondary event) {

    switch (griefProfile.getTarget()) {
      case "minecraft:item_frame":
        return new InteractItemFrameAlert(griefProfile, event);
      case "minecraft:armor_stand":
        return new InteractArmorStandAlert(griefProfile, event);
      default:
        return new InteractEntityAlert(griefProfile, event);
    }
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.INTERACT;
  }
}
