package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.GriefEvent;
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
import org.spongepowered.api.world.World;

import java.util.Optional;

public class GriefAlertCheckCommand extends AbstractCommand {

  public GriefAlertCheckCommand(GriefAlert plugin) {
    super(plugin, GriefAlert.Permission.GRIEFALERT_COMMAND_CHECK, Text.of("Check the grief alert with the given id"));
    addAlias("check");
    addAlias("c");
    addCommandElement(GenericArguments.onlyOne(GenericArguments.integer(Text.of("alert code"))));
  }

  @Override
  @NonnullByDefault
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;
      if (args.<Integer>getOne("alert code").isPresent()) {
        Optional<GriefEvent> event = plugin.getGriefEventCache().get(args.<Integer>getOne("alert code").get());
        if (event.isPresent()) {
          Optional<Transform<World>> grieferTransform = event.get().getEvent().getGrieferSnapshot().getTransform();
          if (grieferTransform.isPresent()) {
            plugin.getGriefEventCache().putSnapshot(player);
            player.setTransformSafely(grieferTransform.get());
            player.sendMessage(event.get().getSummary());
          } else {
            player.sendMessage(Text.of(TextColors.RED, "The location of the offending player could not be found."));
          }
        } else {
          player.sendMessage(Text.of(TextColors.RED, "The Grief Event you tried to access could not be found."));
        }
      } else {
        player.sendMessage(Text.of(TextColors.RED, "The queried Grief Event code could not be parsed."));
      }
    } else {
      src.sendMessage(Text.of(TextColors.RED, "Only players may execute this command"));
    }
    return CommandResult.success();
  }
}
