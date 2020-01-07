/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.SpongeEvents;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class InteractArmorStandAlert extends InteractEntityAlert {

  InteractArmorStandAlert(
      final GriefProfile griefProfile,
      final InteractEntityEvent.Secondary event) {
    super(griefProfile, event);
  }

  @Nonnull
  @Override
  public Text getMessageText() {
    Text.Builder builder = Text.builder();
    builder.append(Text.of(
        Format.playerName(getGriefer()),
        Format.space(),
        getEventColor(), "interacted with",
        Format.space(),
        getTargetColor(), "an armor_stand",
        Format.space(),
        TextColors.RED,
        "(containing ",
        Format.bonus(SpongeEvents.getArmorStandContent(
            getEntitySnapshot()).map(List::size).orElse(0)),
        " items)",
        TextColors.RED, " in the ",
        getDimensionColor(), getGrieferTransform().getExtent().getDimension().getType().getName()));
    return builder.build();
  }

  @Nonnull
  @Override
  public Optional<String> getExtraSummaryContent() {
    return SpongeEvents.getArmorStandContent(getEntitySnapshot())
        .map((list) -> String.join(", ", list));
  }

}
