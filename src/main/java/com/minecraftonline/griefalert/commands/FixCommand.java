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

import com.google.common.collect.ImmutableMap;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.alerts.Fixable;
import com.minecraftonline.griefalert.api.templates.Arg;
import com.minecraftonline.griefalert.api.templates.Templates;
import com.minecraftonline.griefalert.commands.common.GeneralCommand;
import com.minecraftonline.griefalert.util.Alerts;
import com.minecraftonline.griefalert.util.Communication;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.enums.AlertTags;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.util.enums.Permissions;

import java.time.Instant;
import java.util.Date;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextElement;
import org.spongepowered.api.text.format.TextColors;

public class FixCommand extends GeneralCommand {

  private static final int FIX_TIMEOUT_SECONDS = 1800;  // 30 minutes

  FixCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_FIX,
        Text.of("Use an alert id to restore the event triggering this alert")
    );
    addAlias("fix");
    addAlias("f");
    setCommandElement(GenericArguments.onlyOne(
        GenericArguments.integer(CommandKeys.ALERT_INDEX.get())));
  }


  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) throws CommandException {

    int index;
    try {
      index = args.<Integer>requireOne(CommandKeys.ALERT_INDEX.get());
    } catch (NoSuchElementException e) {
      sendHelp(src);
      return CommandResult.success();
    }

    // Find PrismAlert to manipulate
    try {
      Alert alert = GriefAlert.getInstance().getAlertService().getAlert(index);
      if (alert.getCreated().before(Date.from(Instant.now().minusSeconds(FIX_TIMEOUT_SECONDS)))
          && Permissions.has(src, Permissions.GRIEFALERT_COMMAND_ROLLBACK)) {
        throw new CommandException(Format.error(String.format(
            "You can only do that %d minutes after the alert",
            Math.floorDiv(FIX_TIMEOUT_SECONDS, 60))));
      }
      if (alert instanceof Fixable) {

        if (((Fixable) alert).fixed()) {
          throw new CommandException(Format.error("This alert was already fixed!"));
        }

        if ((src instanceof Player) && ((Player) src).getUniqueId().equals(alert.getGrieferUuid())) {
          throw new CommandException(Format.error("You can't undo your own actions"));
        }

        if (((Fixable) alert).fix(src)) {
          Text message = Templates.FIX.getTextTemplate().apply(
              ImmutableMap.<String, TextElement>builder()
                  .put(Arg.PREFIX.name(), Format.prefix())
                  .put(Arg.OFFICER.name(), Format.userName(Alerts.getGriefer(alert)))
                  .put(Arg.TARGET.name(), Grammar.addIndefiniteArticle(Format.item(alert.getTarget())))
                  .put(Arg.GRIEFER.name(), Format.userName(Alerts.getGriefer(alert)))
                  .put(Arg.EVENT.name(), Format.action(alert.getGriefEvent()))
                  .put(Arg.X.name(), Text.of(alert.getGriefPosition().getX()))
                  .put(Arg.Y.name(), Text.of(alert.getGriefPosition().getY()))
                  .put(Arg.Z.name(), Text.of(alert.getGriefPosition().getZ()))
                  .put(Arg.DIMENSION.name(), Format.dimension(Alerts.getWorld(alert).getDimension().getType()))
                  .put(Arg.SUFFIX.name(), Text.of(Format.space(), AlertTags.getTagInfo(index)))
                  .build())
              .build();
          Communication.getStaffBroadcastChannelWithout(src).send(message);
          return CommandResult.success();
        } else {
          throw new CommandException(Format.error("Operation failed."));
        }
      } else {
        throw new CommandException(Format.error("This event cannot be automatically fixed"));
      }
    } catch (IllegalArgumentException e) {
      throw Errors.noAlertException();
    }

  }

}
