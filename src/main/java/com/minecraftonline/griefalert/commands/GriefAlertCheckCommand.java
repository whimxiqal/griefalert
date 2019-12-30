package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class GriefAlertCheckCommand extends AbstractCommand {

  GriefAlertCheckCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_CHECK,
        Text.of("Check the grief alert with the given id")
    );
    addAlias("check");
    addAlias("c");
    addCommandElement(GenericArguments.onlyOne(GenericArguments.integer(Text.of("alert code"))));
  }

  @Override
  @NonnullByDefault
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;
      if (args.<Integer>getOne("alert code").isPresent()) {

        try {

          Alert alert = GriefAlert.getInstance().getAlertQueue().get(args.<Integer>getOne("alert code").get());
          alert.check(player);

        } catch (IndexOutOfBoundsException e) {
          player.sendMessage(Format.error(Text.of(
              TextColors.RED,
              "That alert could not be found.")));
        }
      } else {
        player.sendMessage(Format.error(Text.of(
            TextColors.RED,
            "The alert code could not be parsed.")));
      }
    } else {
      src.sendMessage(Format.error(Text.of(
          TextColors.RED,
          "Only players may execute this command")));
    }
    return CommandResult.success();
  }
}
