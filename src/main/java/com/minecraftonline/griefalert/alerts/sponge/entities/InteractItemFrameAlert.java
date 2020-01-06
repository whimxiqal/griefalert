/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.SpongeEvents;
import java.util.Optional;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;


public class InteractItemFrameAlert extends InteractEntityAlert {

  InteractItemFrameAlert(
      final GriefProfile griefProfile,
      final InteractEntityEvent.Secondary event) {
    super(griefProfile, event);
  }

  @Override
  public Text getMessageText() {
    Text.Builder builder = Text.builder();
    builder.append(Text.of(
        Format.playerName(getGriefer()),
        Format.space(),
        getEventColor(), "interacted with",
        Format.space(),
        getTargetColor(), "an item_frame",
        Format.space(),
        TextColors.RED,
        String.format(
            "(%s)",
            SpongeEvents.getItemFrameContentMessage(getEntitySnapshot())),
        TextColors.RED, " in the ",
        getDimensionColor(), getGrieferTransform().getExtent().getDimension().getType().getName()));
    return builder.build();
  }

  @Override
  public Optional<String> getExtraSummaryContent() {
    return SpongeEvents.getItemFrameContent(getEntitySnapshot());
  }

}
