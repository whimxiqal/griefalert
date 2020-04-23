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
import com.minecraftonline.griefalert.util.Communication;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.Permissions;

import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

public class FlushCommand extends GeneralCommand {

  FlushCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_RELOAD,
        Text.of("Clear the Alert cache")
    );
    addAlias("flush");
    setCommandElement(GenericArguments.flags().flag("-force", "f")
        .buildWith(GenericArguments.none()));
  }

  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) {
    if (!args.hasAny("force")) {
      src.sendMessage(Format.info("This will clear all cached Alert data! "
          + "Use --force if you're sure."));
      return CommandResult.success();
    }
    GriefAlert.getInstance().getAlertService().reset();
    src.sendMessage(Format.success("GriefAlert flushed!"));
    Communication.getStaffBroadcastChannelWithout(src).send(
        Format.info(
            (src instanceof User) ? Format.userName((User) src) : src.getName(),
            " just cleared out all local GriefAlert data."));
    return CommandResult.success();
  }

}
