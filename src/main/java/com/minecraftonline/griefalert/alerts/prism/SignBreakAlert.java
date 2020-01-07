/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.prism;

import com.minecraftonline.griefalert.api.data.SignText;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.Prism;

import java.util.Optional;
import javax.annotation.Nonnull;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;


public class SignBreakAlert extends PrismAlert {

  public SignBreakAlert(GriefProfile griefProfile, PrismRecordArchived prismRecord) {
    super(griefProfile, prismRecord);
  }

  @Nonnull
  @Override
  public Text getMessageText() {
    Text.Builder builder = Text.builder();

    Optional<SignText> signTextOptional = Prism.getBrokenSignText(getPrismRecord());

    builder.append(Text.of(
        Format.playerName(getGriefer()),
        Format.space(),
        getEventColor(), "broke",
        Format.space(),
        getTargetColor(), Grammar.addIndefiniteArticle(getTarget())));

    builder.append(Text.of(
        TextColors.RED, " in the ",
        getDimensionColor(), getGrieferTransform().getExtent().getDimension().getType().getName()));

    signTextOptional.ifPresent((sign) -> {

      sign.getText1().ifPresent((text) ->
          builder.append(Text.of(TextColors.GRAY, "\nLine 1: " + text)));
      sign.getText2().ifPresent((text) ->
          builder.append(Text.of(TextColors.GRAY, "\nLine 2: " + text)));
      sign.getText3().ifPresent((text) ->
          builder.append(Text.of(TextColors.GRAY, "\nLine 3: " + text)));
      sign.getText4().ifPresent((text) ->
          builder.append(Text.of(TextColors.GRAY, "\nLine 4: " + text)));
    });

    return builder.build();
  }

}
