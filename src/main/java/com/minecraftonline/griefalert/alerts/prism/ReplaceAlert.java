package com.minecraftonline.griefalert.alerts.prism;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.*;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ReplaceAlert extends PrismAlert {

  private final String originalBlockId;

  public ReplaceAlert(GriefProfile griefProfile, PrismRecordArchived prismRecord, String originalBlockId) {
    super(griefProfile, prismRecord);
    this.originalBlockId = originalBlockId;
  }

  public Text getMessageText() {
    Text.Builder builder = Text.builder();
    builder.append(Text.of(
        Format.playerName(getGriefer()),
        Format.space(),
        getEventColor(), "replaced",
        Format.space(),
        getTargetColor(), Grammar.addIndefiniteArticle(originalBlockId.replace("minecraft:", "")),
        Format.space(), "with",
        Format.space(),
        getTargetColor(), Grammar.addIndefiniteArticle(Prism.getTarget(getPrismRecord()).get().replace("minecraft:", ""))));
    getTransform().ifPresent((transform -> builder.append(Text.of(
        TextColors.RED, " in the ",
        getDimensionColor(), transform.getExtent().getDimension().getType().getName()))));
    return builder.build();
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.REPLACE;
  }

}
