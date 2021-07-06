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

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.GeneralAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.commands.common.GeneralCommand;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.util.enums.Permissions;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * A command to get formatted and itemized information about an {@link Alert}.
 */
public class InfoCommand extends GeneralCommand {

  InfoCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_INFO,
        Text.of("Get itemized information about an Alert")
    );
    addAlias("info");
    addAlias("i");
    setCommandElement(GenericArguments.onlyOne(
        GenericArguments.integer(CommandKeys.ALERT_INDEX.get())));
  }

  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) throws CommandException {
    int index;
    try {
      index = args.requireOne(CommandKeys.ALERT_INDEX.get());
    } catch (NoSuchElementException e) {
      sendHelp(src);
      return CommandResult.success();
    }
    Alert alert;
    try {
      alert = GriefAlert.getInstance().getAlertService().getAlert(index);
    } catch (IllegalArgumentException e) {
      throw Errors.noAlertException();
    }

    if (alert instanceof GeneralAlert) {
      GeneralAlert generalAlert = (GeneralAlert) alert;
      PaginationList.builder()
          .title(Text.of(
              TextColors.YELLOW,
              "Alert ",
              Format.bonus(index), " Info"))
          .padding(Format.bonus("="))
          .contents(generalAlert.getDetails().stream()
              .map(detail -> detail.get(generalAlert))
              .flatMap(optional -> optional.map(Stream::of).orElseGet(Stream::empty))
              .map(text -> Text.of(Format.GRIEF_ALERT_THEME, " - ", TextColors.RESET, text))
              .collect(Collectors.toList()))
          .build()
          .sendTo(src);
    } else {
      PaginationList.builder()
          .title(Text.of(
              TextColors.YELLOW,
              "Alert ",
              Format.bonus(index), " Info"))
          .padding(Format.bonus("="))
          .contents(alert.getSummary())
          .build()
          .sendTo(src);
    }
    return CommandResult.success();
  }
}
