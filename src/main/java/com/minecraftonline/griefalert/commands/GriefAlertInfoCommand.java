package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.GriefEvent;
import java.util.Optional;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class GriefAlertInfoCommand extends AbstractCommand {

  GriefAlertInfoCommand(GriefAlert plugin) {
    super(
        plugin,
        GriefAlert.Permission.GRIEFALERT_COMMAND_INFO,
        Text.of("Get info about a given grief alert")
    );
    addAlias("info");
    addAlias("i");
    addCommandElement(GenericArguments.onlyOne(GenericArguments.integer(Text.of("alert code"))));
  }

  @Override
  @NonnullByDefault
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    Player player = (Player) src;
    if (args.<Integer>getOne("alert code").isPresent()) {
      Optional<GriefEvent> event = plugin.getGriefEventCache()
          .get(args.<Integer>getOne("alert code").get());
      if (event.isPresent()) {
        player.sendMessage(event.get().getSummary());
      } else {
        player.sendMessage(Text.of(
            TextColors.RED,
            "The Grief Event you tried to access could not be found")
        );
      }
    } else {
      player.sendMessage(Text.of(
          TextColors.RED,
          "The queried Grief Event code could not be parsed")
      );
    }
    return CommandResult.success();
  }
}
