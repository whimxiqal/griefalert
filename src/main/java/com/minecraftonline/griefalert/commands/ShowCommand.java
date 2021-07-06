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
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.alerts.inspections.AlertInspection;
import com.minecraftonline.griefalert.commands.common.GeneralCommand;
import com.minecraftonline.griefalert.util.Communication;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.util.enums.Permissions;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;


/**
 * Class for handling command which shows a hologram displaying information about
 * a player at the location of grief.
 */
class ShowCommand extends GeneralCommand {

  private static final float MINIMUM_DISTANCE_TO_SHOW = 25.0f;

  ShowCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_SHOW,
        Text.of("Create a Hologram at the location of grief"));
    addAlias("show");
    addAlias("s");
    setCommandElement(GenericArguments.optional(
        GenericArguments.integer(CommandKeys.ALERT_INDEX.get())));
  }

  @NotNull
  @Override
  public CommandResult execute(@NotNull CommandSource src,
                               @NotNull CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;
      Optional<Integer> index;
      try {
        index = args.getOne(CommandKeys.ALERT_INDEX.get());

      } catch (NoSuchElementException e) {
        sendHelp(src);
        return CommandResult.success();
      }

      if (!index.isPresent()) {
        index = GriefAlert.getInstance().getAlertService()
            .getLastInspection(player)
            .map(AlertInspection::getAlertIndex);
      }

      if (!index.isPresent()) {
        player.sendMessage(Format.error("No recent index could be found, so please specify an alert index"));
        return CommandResult.empty();
      }

      Alert alert;
      try {
        alert = GriefAlert.getInstance().getAlertService().getAlert(index.get());
      } catch (IllegalArgumentException e) {
        throw Errors.noAlertException();
      }

      boolean differentWorld = !alert.getWorldUuid().equals(player.getLocation().getExtent().getUniqueId());
      boolean tooFar = alert.getGriefPosition().distance(player.getLocation().getBlockPosition())
          > MINIMUM_DISTANCE_TO_SHOW;
      if (differentWorld || tooFar) {
        throw new CommandException(Format.error("You need to be closer to the grief location!"));
      }

      // Create temporary hologram of grief
      GriefAlert.getInstance().getHologramManager().createTemporaryHologram(alert);

      // Broadcast the attempt at command
      Communication.getStaffBroadcastChannel().send(Format.info(
          Format.userName(player),
          " is taking a closer look at alert ",
          CheckCommand.clickToCheck(index.get())));
      return CommandResult.success();
    } else {
      throw Errors.playerOnlyException();
    }
  }
}
