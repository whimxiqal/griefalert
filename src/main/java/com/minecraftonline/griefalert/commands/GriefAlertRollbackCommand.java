package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.prism.PrismAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Communication;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Permissions;

import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;


public class GriefAlertRollbackCommand extends AbstractCommand {


  GriefAlertRollbackCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_CHECK,
        Text.of("Return to your previous location prior to a grief alert check")
    );
    addAlias("rollback");
    addAlias("rb");
    setCommandElement(GenericArguments.onlyOne(GenericArguments.integer(Text.of("index"))));
  }

  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) {

    PrismAlert alert;
    try {
      Alert alertGeneral = GriefAlert.getInstance().getRotatingAlertList()
          .get(args.<Integer>getOne("index").get());
      if (alertGeneral instanceof PrismAlert) {
        alert = (PrismAlert) alertGeneral;
      } else {
        src.sendMessage(Format.error("This alert can not be rolled back."));
        return CommandResult.empty();
      }
    } catch (IndexOutOfBoundsException e) {
      src.sendMessage(Format.error("That alert could not be found."));
      return CommandResult.empty();
    }

    if (alert.isReversed()) {
      src.sendMessage(Format.error("This alert has already be rolled back."));
      return CommandResult.empty();
    }

    if (alert.rollback(src)) {
      src.sendMessage(Format.success("Rollback successful!"));
      Communication.getStaffBroadcastChannelWithout(src).send(
          Format.info(
              (src instanceof Player) ? Format.playerName((Player) src) : src.getName(),
              Format.space(),
              "just rolled back alert number",
              Format.space(),
              Format.bonus(alert.getCacheIndex()),
              ".",
              Format.space(),
              Format.getTagInfo(alert.getCacheIndex())
          )
      );
      return CommandResult.success();
    } else {
      src.sendMessage(Format.error("Rollback failed."));
      return CommandResult.empty();
    }

  }
}
