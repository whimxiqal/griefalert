package com.minecraftonline.griefalert.griefevents.profiles;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.GriefEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.minecraftonline.griefalert.griefevents.comms.Messenger;
import com.minecraftonline.griefalert.util.General;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;


public class SpecialtyBehavior implements BiConsumer<GriefAlert, GriefEvent> {

  private static final String SIGN_LINE_DELIMITER = ":mcosigndelim:";

  private final BiConsumer<GriefAlert, GriefEvent> consumer;
  private final Function<GriefEvent, String> logBehavior;

  public static final SpecialtyBehavior SIGN_PLACEMENT = new SpecialtyBehavior(
      (plugin, griefEvent) -> {
        griefEvent.stealthy = true;
        Messenger.getStaffBroadcastChannel().send(Text.of(
            General.formatPlayerName(griefEvent.getEvent().getGriefer()),
            TextColors.YELLOW, " changed the contents of a sign."
            )
        );
      },
      (griefEvent) -> {
        if (griefEvent.getEvent().getEvent() instanceof ChangeSignEvent) {
          List<String> plainLines = new LinkedList<>();
          for (Text line : ((ChangeSignEvent) griefEvent.getEvent().getEvent())
              .getText().asList()) {
            plainLines.add(line.toPlain());
          }
          return String.join(SIGN_LINE_DELIMITER, plainLines);
        }
        return "";
      }
  );

  public static final SpecialtyBehavior NONE =
      new SpecialtyBehavior((plugin, griefEvent) -> {}, (griefEvent) -> "");

  private SpecialtyBehavior(
      BiConsumer<GriefAlert, GriefEvent> consumer,
      Function<GriefEvent, String> logBehavior) {
    this.consumer = consumer;
    this.logBehavior = logBehavior;
  }

  public String getSpecialLogString(GriefEvent griefEvent) {
    return logBehavior.apply(griefEvent);
  }

  @Override
  public void accept(GriefAlert griefAlert, GriefEvent griefEvent) {
    consumer.accept(griefAlert, griefEvent);
  }
}
