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

package com.minecraftonline.griefalert.sponge.alert.commands;

import com.minecraftonline.griefalert.SpongeGriefAlert;
import com.minecraftonline.griefalert.sponge.alert.commands.common.GeneralCommand;
import com.minecraftonline.griefalert.sponge.alert.util.Errors;
import com.minecraftonline.griefalert.sponge.alert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Permissions;
import javax.annotation.Nonnull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * A command for an officer to return a location prior to an inspection.
 */
public class ReturnCommand extends GeneralCommand {

  ReturnCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_CHECK,
        Text.of("Return to your location prior to an alert check")
    );
    addAlias("return");
    addAlias("r");
    setCommandElement(GenericArguments.optional(GenericArguments.integer(CommandKeys.ALERT_INDEX.get())));
  }

  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) throws CommandException {
    if (src instanceof Player) {
      boolean inspected = args.<Integer>getOne(CommandKeys.ALERT_INDEX.get()).isPresent()
          ? SpongeGriefAlert.getSpongeInstance().getAlertService()
          .uninspect((Player) src, args.<Integer>getOne(CommandKeys.ALERT_INDEX.get()).get())
          : SpongeGriefAlert.getSpongeInstance().getAlertService()
          .uninspect((Player) src);
      if (inspected) {
        return CommandResult.success();
      } else {
        return CommandResult.empty();
      }
    } else {
      throw Errors.playerOnlyException();
    }
  }
}
