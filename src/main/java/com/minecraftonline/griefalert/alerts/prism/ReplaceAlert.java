package com.minecraftonline.griefalert.alerts.prism;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Grammar;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class ReplaceAlert extends PrismAlert {

  private final String replacementBlockId;

  public ReplaceAlert(GriefProfile griefProfile,
                      PrismRecordArchived prismRecord,
                      String replacementBlockId) {
    super(griefProfile, prismRecord);
    this.replacementBlockId = replacementBlockId;
  }

  /**
   * Special constructor for <code>Text</code> for a <code>ReplaceAlert</code>.
   *
   * @return The <code>Text</code>
   */
  @Nonnull
  public Text getMessageText() {
    Text.Builder builder = Text.builder();
    builder.append(Text.of(
        Format.playerName(getGriefer()),
        Format.space(),
        getEventColor(), "replaced",
        Format.space(),
        getTargetColor(), Grammar.addIndefiniteArticle(getTarget()
            .replace("minecraft:", "")),
        Format.space(), "with",
        Format.space(),
        getTargetColor(), Grammar.addIndefiniteArticle(replacementBlockId
            .replace("minecraft:", ""))));
    builder.append(Text.of(
        TextColors.RED, " in the ",
        getDimensionColor(), getGrieferTransform().getExtent().getDimension().getType().getName()));
    return builder.build();
  }

}
