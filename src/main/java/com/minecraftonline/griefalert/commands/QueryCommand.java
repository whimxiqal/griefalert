/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.GeneralCommand;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
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
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;


public class QueryCommand extends GeneralCommand {

  private static final int DEFAULT_MAXIMUM_QUERIES = 1000;

  QueryCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_QUERY,
        Text.of("Show a filtered list of recent Alerts"));
    addAlias("query");
    addAlias("q");
    setCommandElement(GenericArguments.flags()
        .valueFlag(
            GenericArguments.string(CommandKeys.PLAYER.get()), "p")
        .valueFlag(
            GenericArguments.catalogedElement(CommandKeys.GA_EVENT.get(), GriefEvent.class), "e")
        .valueFlag(
            GenericArguments.string(CommandKeys.TARGET.get()), "t")
        .valueFlag(
            GenericArguments.integer(CommandKeys.MAXIMUM.get()), "m")
        .flag("-spread", "s")
        .flag("-group", "g")
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

    int max = (int) args.getOne(CommandKeys.MAXIMUM.get()).orElse(DEFAULT_MAXIMUM_QUERIES);

    final boolean group = args.hasAny("group");

    List<Alert> matching = cacheReversed
        .stream()
        .filter(alert ->
            args.<String>getOne(CommandKeys.PLAYER.get()).map(player ->
                alert.getGriefer().getName().toLowerCase().contains(player.toLowerCase()))
                .orElse(true)
                && args.<GriefEvent>getOne(CommandKeys.GA_EVENT.get()).map(event ->
                alert.getGriefEvent().getId().toLowerCase().contains(event.getId().toLowerCase()))
                .orElse(true)
                && args.<String>getOne(CommandKeys.GA_TARGET.get()).map(target ->
                alert.getTarget().toLowerCase().contains(target.toLowerCase()))
                .orElse(true))
        .limit(max)
        .collect(Collectors.toList());

    if (matching.isEmpty()) {
      src.sendMessage(Format.info("No alerts matching those parameters"));
      return CommandResult.empty();
    }

    List<Text> contents;
    if (args.hasAny("spread")) {
      contents = matching.stream().map(Alert::getTextWithIndex).collect(Collectors.toList());
    } else {
      LinkedList<LinkedList<Alert>> nested = new LinkedList<>();
      nested.add(new LinkedList<>());
      for (Alert alert : matching) {
        LinkedList<Alert> curr = nested.getLast();
        // Make a new message if its a new type of alert or the previous group has 20 alerts already
        if ((curr.isEmpty() || curr.getLast().isRepeatOf(alert)) && curr.size() < 20) {
          curr.add(alert);
        } else {
          LinkedList<Alert> next = new LinkedList<>();
          next.add(alert);
          nested.add(next);
        }
      }
      if (group) {
        contents = nested.stream()
            .filter(list -> !list.isEmpty())
            .map(list -> list.get(0).getMessageText().concat(Format.bonus(
                Format.space(),
                "x",
                list.size())))
            .collect(Collectors.toList());
      } else {
        contents = nested.stream()
            .filter(list -> !list.isEmpty())
            .map(list -> list.getFirst().getTextWithIndices(
                list.stream()
                    .map(Alert::getCacheIndex)
                    .collect(Collectors.toList())))
            .collect(Collectors.toList());
      }
    }

    if (contents.isEmpty()) {
      src.sendMessage(Format.info("No results found with those parameters"));
    } else {
      PaginationList.builder()
          .contents(contents)
          .title(Text.of(TextColors.YELLOW, "Alert Query"))
          .padding(Text.of(TextColors.DARK_GRAY, "="))
          .build()
          .sendTo(src);
    }

    return CommandResult.success();
  }

}
