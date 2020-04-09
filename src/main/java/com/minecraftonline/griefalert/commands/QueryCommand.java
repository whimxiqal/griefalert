/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert.commands;

import com.google.common.collect.Maps;
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
import java.util.Map;
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

  private static final int DEFAULT_MAXIMUM_QUERIES = 100;
  private static final int MAXIMUM_MAXIMUM_QUERIES = 1000;

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

    Map<Text, Text> flags = Maps.newHashMap();

    List<Alert> cacheReversed = GriefAlert.getInstance()
        .getAlertManager()
        .getAlertCache()
        .getDataByTime();
    Collections.reverse(cacheReversed);

    int max = args.<Integer>getOne(CommandKeys.MAXIMUM.get())
        .map(i -> Math.max(0, Math.min(i, MAXIMUM_MAXIMUM_QUERIES)))
        .orElse(DEFAULT_MAXIMUM_QUERIES);
    flags.put(CommandKeys.MAXIMUM.get(), Text.of(max));

    args.<String>getOne(CommandKeys.PLAYER.get()).ifPresent(p ->
        flags.put(CommandKeys.PLAYER.get(), Text.of(p)));
    args.<GriefEvent>getOne(CommandKeys.GA_EVENT.get()).ifPresent(e ->
        flags.put(CommandKeys.GA_EVENT.get(), e.toText()));
    args.<String>getOne(CommandKeys.GA_TARGET.get()).ifPresent(t ->
        flags.put(CommandKeys.GA_TARGET.get(), Text.of(t)));

    List<Alert> matching = cacheReversed
        .stream()
        .filter(alert ->
            args.<String>getOne(CommandKeys.PLAYER.get()).map(player ->
                alert.getGriefer().getName().toLowerCase().contains(player.toLowerCase()))
                .orElse(true)
                && args.<GriefEvent>getOne(CommandKeys.GA_EVENT.get()).map(event ->
                alert.getGriefEvent().getId().toLowerCase().contains(event.getId().toLowerCase()))
                .orElse(true)
                && args.<String>getOne(CommandKeys.TARGET.get()).map(target ->
                alert.getTarget().toLowerCase().contains(target.toLowerCase()))
                .orElse(true))
        .limit(max)
        .collect(Collectors.toList());

    if (matching.isEmpty()) {
      src.sendMessage(Format.info("No alerts matching those parameters"));
      return CommandResult.empty();
    }

    final boolean group = args.hasAny("group");
    flags.put(Text.of("group"), Text.of(group));
    final boolean spread = args.hasAny("spread");
    flags.put(Text.of("spread"), Text.of(spread));

    List<Text> pageContents;
    if (spread) {
      pageContents = matching.stream().map(Alert::getTextWithIndex).collect(Collectors.toList());
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
        pageContents = nested.stream()
            .filter(list -> !list.isEmpty())
            .map(list -> list.get(0).getMessageText().concat(Format.bonus(
                Format.space(),
                "x",
                list.size())))
            .collect(Collectors.toList());
      } else {
        pageContents = nested.stream()
            .filter(list -> !list.isEmpty())
            .map(list -> list.getFirst().getTextWithIndices(
                list.stream()
                    .map(Alert::getCacheIndex)
                    .collect(Collectors.toList())))
            .collect(Collectors.toList());
      }
    }

    if (pageContents.isEmpty()) {
      src.sendMessage(Format.info("No results found with those parameters"));
    } else {
      PaginationList.builder()
          .contents(pageContents)
          .title(Text.of(TextColors.YELLOW, "Alert Query"))
          .header(Format.info(
              "Using parameters: ",
              Text.joinWith(
                  Format.bonus(", "),
                  flags.entrySet()
                      .stream()
                      .map(entry ->
                          Format.bonus("{", entry.getKey(), ": ", entry.getValue(), "}"))
                      .collect(Collectors.toList()))))
          .padding(Text.of(TextColors.DARK_GRAY, "="))
          .build()
          .sendTo(src);
    }

    return CommandResult.success();
  }

}
