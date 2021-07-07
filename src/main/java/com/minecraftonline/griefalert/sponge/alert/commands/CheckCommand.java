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
import com.minecraftonline.griefalert.sponge.alert.util.Format;
import com.minecraftonline.griefalert.sponge.alert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Permissions;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * A command for an officer to check ("inspect") an alert.
 *
 * @author PietElite
 */
public class CheckCommand extends GeneralCommand {

  CheckCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_CHECK,
        Text.of("Teleport to the block location of the event")
    );
    addAlias("check");
    addAlias("c");
    setCommandElement(GenericArguments.seq(
        GenericArguments.integer(CommandKeys.ALERT_INDEX.get()),
        GenericArguments.flags()
            .flag("f")
            .flag("p")
            .buildWith(GenericArguments.none())));
    addFlagDescription("f",
        Text.of(TextColors.AQUA, "Force",
            TextColors.RESET, " the teleportation, even if no safe location could be found"),
        false);
    addFlagDescription("p",
        Text.of("Teleport to the",
            TextColors.AQUA, " player",
            TextColors.RESET, "'s location of the grief event \ninstead of the block location"),
        false);
  }

  /**
   * Get a clickable message that allows the officer to check the alert.
   *
   * @param index the index of the alert saved in the cache
   * @return the formatted <code>Text</code>
   */
  public static Text clickToCheck(int index) {
    return Format.command(String.valueOf(index),
        String.format("/griefalert check %s", index),
        Text.of("Check this alert"));
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;
      int index;
      try {
        index = args.requireOne(CommandKeys.ALERT_INDEX.get());
      } catch (NoSuchElementException e) {
        sendHelp(src);
        return CommandResult.success();
      }
      try {
        SpongeGriefAlert.getSpongeInstance().getAlertService().inspect(index, player,
            args.hasAny("f"),
            !args.hasAny("p"));
        return CommandResult.success();
      } catch (IndexOutOfBoundsException e) {
        throw Errors.noAlertException();
      }
    } else {
      throw Errors.playerOnlyException();
    }
  }

}
