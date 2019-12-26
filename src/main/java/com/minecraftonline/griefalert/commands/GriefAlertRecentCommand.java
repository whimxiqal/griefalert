package com.minecraftonline.griefalert.commands;

import com.helion3.prism.api.data.PrismEvent;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Grammar;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.minecraftonline.griefalert.util.Permissions;
import com.minecraftonline.griefalert.util.Prism;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class GriefAlertRecentCommand extends AbstractCommand {

  GriefAlertRecentCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_RECENT,
        Text.of("Get information regarding recent cached grief alerts")
    );
    addAlias("recent");
    addAlias("n");
    HashMap<String, ?> filterMap = new HashMap<>();
    filterMap.put("player", null);
    addCommandElement(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))));
  }

  @Override
  @NonnullByDefault
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    if (args.getOne("player").isPresent()) {
      Player player = (Player) args.getOne("player").get();
      // TODO: Finish 'griefalert recent' command

    }
    return CommandResult.success();
  }

}
