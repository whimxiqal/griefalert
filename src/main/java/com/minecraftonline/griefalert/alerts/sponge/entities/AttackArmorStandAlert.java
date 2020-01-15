/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.SpongeEvents;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;


public class AttackArmorStandAlert extends AttackEntityAlert {

  AttackArmorStandAlert(final GriefProfile griefProfile, final InteractEntityEvent.Primary event) {
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
