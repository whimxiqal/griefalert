/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.prism;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.data.SignText;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.*;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class SignBreakAlert extends PrismAlert {

  SignBreakAlert(GriefProfile griefProfile, PrismRecordArchived prismRecord) {
    super(griefProfile, prismRecord);
  }

  @Override
  public Text getMessageText() {
    Text.Builder builder = Text.builder();

    Optional<SignText> signTextOptional = Prism.getBrokenSignLines(getPrismRecord());

    builder.append(Text.of(
        General.formatPlayerName(getGriefer()),
        Format.space(),
        getEventColor(), "broke",
        Format.space(),
        getTargetColor(), Grammar.addIndefiniteArticle(griefProfile.getTarget())));

    getTransform().ifPresent((transform -> builder.append(Text.of(
        TextColors.RED, " in the ",
        getDimensionColor(), transform.getExtent().getDimension().getType().getName()))));

    signTextOptional.ifPresent((sign) -> {

      sign.getText1().ifPresent((text) -> builder.append(Text.of(TextColors.GRAY, "\nLine 1: " + text)));
      sign.getText2().ifPresent((text) -> builder.append(Text.of(TextColors.GRAY, "\nLine 2: " + text)));
      sign.getText3().ifPresent((text) -> builder.append(Text.of(TextColors.GRAY, "\nLine 3: " + text)));
      sign.getText4().ifPresent((text) -> builder.append(Text.of(TextColors.GRAY, "\nLine 4: " + text)));
    });

    return builder.build();
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.BREAK;
  }

}
