/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;

import javax.annotation.Nonnull;

import org.spongepowered.api.event.entity.InteractEntityEvent;

public class InteractEntityAlert extends EntityAlert {

  public InteractEntityAlert(@Nonnull GriefProfile griefProfile,
                             @Nonnull InteractEntityEvent.Secondary event) {
    super(griefProfile, event);

    if (griefProfile.getTarget().equals("minecraft:item_frame")) {
      addDetail(getItemFrameDetail());
    }
    if (griefProfile.getTarget().equals("minecraft:armor_stand")) {
      addDetail(getArmorStandDetail());
    }

  }

  public InteractEntityAlert(@Nonnull GriefProfile griefProfile,
                             @Nonnull InteractEntityEvent.Secondary event,
                             @Nonnull final String tool) {
    this(griefProfile, event);
    addDetail(Detail.of(
        "Tool",
        "The item in the hand of the player at the time of the event.",
        Format.item(tool)));
  }

}
