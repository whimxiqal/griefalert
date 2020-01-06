/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;

public class UseAlert extends SpongeAlert {

  private UseAlert(GriefProfile griefProfile, InteractItemEvent.Secondary event) {
    super(griefProfile, event);
  }

  public static UseAlert of(GriefProfile griefProfile, InteractItemEvent.Secondary event) {
    return new UseAlert(griefProfile, event);
  }

}
