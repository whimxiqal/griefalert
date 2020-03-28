/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.SpongeEvents;

import org.spongepowered.api.event.entity.InteractEntityEvent;

class InteractItemFrameAlert extends InteractEntityAlert {

  InteractItemFrameAlert(final GriefProfile griefProfile,
                         final InteractEntityEvent.Secondary event) {
    super(griefProfile, event);
    addDetail(Detail.of(
        "Content",
        "The item in this item frame at the time of the event.",
        SpongeEvents.getItemFrameContent(getEntitySnapshot())
            .map(Format::item)
            .orElse(Format.bonus("none"))));
  }

}
