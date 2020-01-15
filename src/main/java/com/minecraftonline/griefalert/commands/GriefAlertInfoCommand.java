/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Permissions;

import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class GriefAlertInfoCommand extends AbstractCommand {

  GriefAlertInfoCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_INFO,
        Text.of("Get info about a given grief alert")
    );
    addAlias("info");
    addAlias("i");
    setCommandElement(GenericArguments.onlyOne(GenericArguments.integer(Text.of("alert code"))));
  }

  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) {
    Player player = (Player) src;
    if (args.<Integer>getOne("alert code").isPresent()) {
      try {
        Alert alert = GriefAlert.getInstance()
            .getRotatingAlertList()
            .get(args.<Integer>getOne("alert code").get());
        player.sendMessage(Format.heading("Alert Info: ", Format.bonus(alert.getCacheIndex())));
        player.sendMessage(alert.getSummaryAll());
      } catch (IndexOutOfBoundsException e) {
        player.sendMessage(Format.error("That alert could not be found."));
      }
    } else {
      player.sendMessage(Format.error("The queried Grief Event code could not be parsed"));
    }
    return CommandResult.success();
  }
}
