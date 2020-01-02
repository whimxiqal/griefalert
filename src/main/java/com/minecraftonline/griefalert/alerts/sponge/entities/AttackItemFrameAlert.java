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


public class AttackItemFrameAlert extends AttackEntityAlert {

  AttackItemFrameAlert(final GriefProfile griefProfile, final InteractEntityEvent.Primary event) {
    super(griefProfile, event);
  }

  @Override
  public Text getMessageText() {
    Text.Builder builder = Text.builder();
    builder.append(Text.of(
        General.formatPlayerName(getGriefer()),
        Format.space(),
        getEventColor(), "attacked",
        Format.space(),
        getTargetColor(), "an item_frame",
        Format.space(),
        TextColors.RED,
        String.format(
            "(%s)",
            SpongeEvents.getItemFrameContentMessage(getEntitySnapshot()))));
    getTransform().ifPresent((transform -> builder.append(Text.of(
        TextColors.RED, " in the ",
        getDimensionColor(), transform.getExtent().getDimension().getType().getName()))));
    return builder.build();
  }

  @Override
  public Optional<String> getExtraSummaryContent() {
    return SpongeEvents.getItemFrameContent(getEntitySnapshot());
  }

}
