/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.prism;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.GriefEvents;
import com.minecraftonline.griefalert.util.Prism;
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

    Optional<List<String>> linesOptional = Prism.getBrokenSignLines(getPrismRecord());

    builder.append(Text.of(
        General.formatPlayerName(getGriefer()),
        Format.space(),
        getEventColor(), "broke",
        Format.space(),
        getTargetColor(), "a sign"));

    getTransform().ifPresent((transform -> builder.append(Text.of(
        TextColors.RED, " in the ",
        getDimensionColor(), transform.getExtent().getDimension().getType().getName()))));

    linesOptional.ifPresent((lines) -> builder.append(Text.of(
        TextColors.WHITE,
        String.format(
            "\nLine 1: %s\nLine 2: %s\nLine 3: %s\nLine 4: %s",
            lines.get(0),
            lines.get(1),
            lines.get(2),
            lines.get(3)))));

    return builder.build();
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.BREAK;
  }

}
