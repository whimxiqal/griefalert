/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.SpongeEvents;

import javax.annotation.Nonnull;

import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;


public class AttackItemFrameAlert extends AttackEntityAlert {

  AttackItemFrameAlert(final GriefProfile griefProfile, final InteractEntityEvent.Primary event) {
    super(griefProfile, event);
    addSummaryContent(
        "Content",
        SpongeEvents.getItemFrameContent(getEntitySnapshot())
            .map(Format::item)
            .orElse(Text.of("none")));
  }

}
