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
import com.minecraftonline.griefalert.commands.common.GeneralCommand;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.services.AlertService;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.SpongeUtil;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.util.enums.Permissions;

import java.util.Optional;
import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

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
        .buildWith(
            GenericArguments.none()));
  }

  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) throws CommandException {

    AlertService.Request.Builder builder = AlertService.requestBuilder();

    for (String username : args.<String>getAll(CommandKeys.PLAYER.get())) {
      Optional<User> player = SpongeUtil.getUser(username);
      if (player.isPresent()) {
        builder.addPlayerUuid(player.get().getUniqueId());
      } else {
        throw Errors.noPlayerFoundException(username);
      }
    }

    args.<GriefEvent>getAll(CommandKeys.GA_EVENT.get()).forEach(builder::addEvent);
    args.<String>getAll(CommandKeys.GA_TARGET.get()).forEach(builder::addTarget);

    builder.setMaximum(args.<Integer>getOne(CommandKeys.MAXIMUM.get())
        .map(i -> Math.max(0, Math.min(i, MAXIMUM_MAXIMUM_QUERIES)))
        .orElse(DEFAULT_MAXIMUM_QUERIES));

    GriefAlert.getInstance().getAlertService().lookup(src,
        builder.build(),
        AlertService.Sort.REVERSE_CHRONOLOGICAL,
        args.hasAny("spread"));

    return CommandResult.success();

  }

}
