/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.enums.Permissions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;


public class GriefAlertQueryCommand extends AbstractCommand {

  private static final int DEFAULT_MAXIMUM_QUERIES = 20;
  private static final int MAXIMUM_MAXIMUM_QUERIES = 100;

  GriefAlertQueryCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_QUERY,
        Text.of("Get information regarding recent cached grief alerts")
    );
    addAlias("query");
    addAlias("q");
    setCommandElement(GenericArguments.flags()
        .valueFlag(
            GenericArguments.string(Text.of("player")),
            "-player", "p")
        .valueFlag(
            GenericArguments.catalogedElement(Text.of("event"), GriefEvent.class),
            "-event", "e")
        .valueFlag(
            GenericArguments.string(Text.of("target")),
            "-target", "t")
        .valueFlag(
            GenericArguments.integer(Text.of("maximum")),
            "-max", "m")
        .flag("-group")
        .buildWith(
            GenericArguments.none()));
  }

  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) {

    List<Alert> cacheReversed = GriefAlert.getInstance()
        .getAlertManager()
        .getAlertCache()
        .getDataByTime();
    Collections.reverse(cacheReversed);

    int max = (int) args.getOne("maximum").orElse(DEFAULT_MAXIMUM_QUERIES);
    // ensure the result count isn't astronomical
    if (max > MAXIMUM_MAXIMUM_QUERIES) {
      src.sendMessage(Format.error("Search shortened to ", MAXIMUM_MAXIMUM_QUERIES, " results."));
      max = MAXIMUM_MAXIMUM_QUERIES;
    }

    final boolean group = args.hasAny("group");

    List<Alert> matching = cacheReversed
        .stream()
        .filter(alert ->
            args.<String>getOne("player").map(player ->
                player.equalsIgnoreCase(alert.getGriefer().getName())).orElse(true)
                && args.<GriefEvent>getOne("event").map(event ->
                event.equals(alert.getGriefEvent())).orElse(true)
                && args.<String>getOne("target").map(target ->
                General.ensureIdFormat(target).equalsIgnoreCase(alert.getTarget())).orElse(true))
        .limit(max)
        .collect(Collectors.toList());

    if (matching.isEmpty()) {
      src.sendMessage(Format.info("No alerts matching those parameters"));
    } else {
      src.sendMessage(Format.heading("Showing last ", Format.bonus(max), " alerts:"));
    }
    Collections.reverse(matching);

    LinkedList<LinkedList<Alert>> nested = new LinkedList<>();
    nested.add(new LinkedList<>());
    for (Alert alert : matching) {
      LinkedList<Alert> curr = nested.getLast();
      if (curr.isEmpty() || curr.getLast().isRepeatOf(alert)) {
        curr.add(alert);
      } else {
        LinkedList<Alert> next = new LinkedList<>();
        next.add(alert);
        nested.add(next);
      }
    }

    if (group) {
      nested.stream()
          .filter(list -> !list.isEmpty())
          .forEach(list -> src.sendMessage(list.getFirst()
          .getMessageText().concat(Format.bonus(
              Format.space(),
              "x",
              list.size()))));
    } else {
      nested.stream()
          .filter(list -> !list.isEmpty())
          .forEach(list -> src.sendMessage(list.getFirst().getTextWithIndices(
          list.stream()
              .map(Alert::getCacheIndex)
              .collect(Collectors.toList()))));
    }

    return CommandResult.success();
  }

}
