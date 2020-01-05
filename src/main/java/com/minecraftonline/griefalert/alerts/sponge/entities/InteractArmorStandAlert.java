/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.SpongeEvents;
import java.util.Optional;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;


public class InteractArmorStandAlert extends InteractEntityAlert {

  InteractArmorStandAlert(
      final GriefProfile griefProfile,
      final InteractEntityEvent.Secondary event) {
    super(griefProfile, event);
  }

  @Override
  public Text getMessageText() {
    Text.Builder builder = Text.builder();
    builder.append(Text.of(
        General.formatPlayerName(getGriefer()),
        Format.space(),
        getEventColor(), "interacted with",
        Format.space(),
        getTargetColor(), "an armor_stand",
        Format.space(),
        TextColors.RED,
        String.format(
            "(%s)",
            SpongeEvents.getArmorStandContentMessage(getEntitySnapshot()))));
    getTransform().ifPresent((transform -> builder.append(Text.of(
        TextColors.RED, " in the ",
        getDimensionColor(), transform.getExtent().getDimension().getType().getName()))));
    return builder.build();
  }

  @Override
  public Optional<String> getExtraSummaryContent() {
    return SpongeEvents.getArmorStandContent(getEntitySnapshot()).map((list) -> String.join(", ", list));
  }

}
