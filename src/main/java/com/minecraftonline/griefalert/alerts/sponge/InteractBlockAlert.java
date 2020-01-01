/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.event.block.InteractBlockEvent;

public class InteractBlockAlert extends SpongeAlert {

  private InteractBlockAlert(GriefProfile griefProfile, InteractBlockEvent event) {
    super(griefProfile, event);
  }

  public static InteractBlockAlert of(GriefProfile griefProfile, InteractBlockEvent event) {
    return new InteractBlockAlert(griefProfile, event);
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.INTERACT;
  }
}
