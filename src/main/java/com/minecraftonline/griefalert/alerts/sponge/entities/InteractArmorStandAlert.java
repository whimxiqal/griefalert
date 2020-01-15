/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.SpongeEvents;

import java.util.stream.Collectors;

import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.text.Text;

public class InteractArmorStandAlert extends InteractEntityAlert {

  InteractArmorStandAlert(final GriefProfile griefProfile,
                          final InteractEntityEvent.Secondary event) {
    super(griefProfile, event);
    addSummaryContent("Contents", SpongeEvents.getArmorStandContent(getEntitySnapshot())
        .map(list -> list.stream()
            .map(Format::item)
            .collect(Collectors.toList()))
        .filter(list -> !list.isEmpty())
        .map(list -> Text.joinWith(Text.of(", ", list)))
        .orElse(Format.bonus("none")));
  }

}
