package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;

import java.util.Optional;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Permissions;
import com.minecraftonline.griefalert.util.Prism;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

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
          if (alert.getTransform().isPresent() && player.setTransformSafely(alert.getTransform().get())) {

            // TODO: Save officers location
            Sponge.getServer().getBroadcastChannel()
                .send(Text.of(player.getName() + " is teleported to a grief alert."));

          } else {
            player.sendMessage(Text.of(
                TextColors.YELLOW,
                "A safe place to teleport could not be identified.")
            );
          }
          player.sendMessage(alert.getMessageText());

        } catch (IndexOutOfBoundsException e) {
          player.sendMessage(Text.of(
              TextColors.RED,
              "The Grief Event you tried to access could not be found.")
          );
        }
      } else {
        player.sendMessage(Text.of(
            TextColors.RED,
            "The queried Grief Event code could not be parsed.")
        );
      }
    } else {
      src.sendMessage(Text.of(
          TextColors.RED,
          "Only players may execute this command")
      );
    }
    return CommandResult.success();
  }
}
