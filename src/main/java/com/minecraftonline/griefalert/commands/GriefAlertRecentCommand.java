/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.util.*;

import java.util.*;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class GriefAlertRecentCommand extends AbstractCommand {

  GriefAlertRecentCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_RECENT,
        Text.of("Get information regarding recent cached grief alerts")
    );
    addAlias("recent");
    addAlias("n");
    setCommandElement(GenericArguments.flags()
        .valueFlag(GenericArguments.string(Text.of("player")), "-player", "p")
        .valueFlag(GenericArguments.catalogedElement(Text.of("event"), GriefEvent.class), "-event", "e")
        .valueFlag(GenericArguments.string(Text.of("target")), "-target", "t")
        .valueFlag(GenericArguments.integer(Text.of("maximum")), "-max", "m")
        .buildWith(GenericArguments.none()));
  }

  @Override
  @NonnullByDefault
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    List<Alert> matching = new LinkedList<>();

    List<Alert> cacheReversed = GriefAlert.getInstance().getAlertQueue().getDataByTime();
    Collections.reverse(cacheReversed);

    int max = (int) args.getOne("maximum").orElse(20);

    for (Alert alert : cacheReversed) {
      if (matching.size() >= max) {
        break;
      }

      if (args.getOne("player").isPresent()
          && !((String) args.getOne("player").get()).equalsIgnoreCase((alert.getGriefer().getName()))) {
        continue;
      }

      if (args.getOne("event").isPresent()
          && !args.getOne("event").get().equals(alert.getGriefEvent())) {
        continue;
      }

      if (args.getOne("target").isPresent()
          && !args.getOne("target").map(
              (s) ->
                  General.ensureIdFormat((String) s)).get().equals(alert.getTarget())) {
        continue;
      }

      matching.add(alert);
    }

    if (matching.isEmpty()) {
      src.sendMessage(Format.info("No alerts matching those parameters"));
    } else {
      src.sendMessage(Format.heading("Showing last ", Format.bonus(max), " alerts:"));
    }
    Collections.reverse(matching);

    LinkedList<Integer> indices = new LinkedList<>();
    for (Alert alert : matching) {
      if (!indices.isEmpty() && !GriefAlert.getInstance().getAlertQueue().get(indices.getLast()).isRepeatOf(alert)) {
        src.sendMessage(GriefAlert.getInstance().getAlertQueue().get(indices.getFirst()).getTextWithIndices(indices));
        indices.clear();
      }
      indices.add(alert.getStackIndex());
    }

    if (!indices.isEmpty()) {
      src.sendMessage(GriefAlert.getInstance().getAlertQueue().get(indices.getFirst()).getTextWithIndices(indices));
    }

    return CommandResult.success();
  }

}
