package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.GriefEvent;
import com.minecraftonline.griefalert.tools.ClickableMessage;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

public class GriefAlertRecentCommand extends AbstractCommand {

  public GriefAlertRecentCommand(GriefAlert plugin) {
    super(plugin, GriefAlert.Permission.GRIEFALERT_COMMAND_RECENT, Text.of("Get information regarding recent cached grief alerts"));
    addAlias("recent");
    addAlias("n");
    HashMap<String, ?> filterMap = new HashMap<>();
    filterMap.put("player", null);
    addCommandElement(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))));
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (args.getOne("player").isPresent()) {
      Player player = (Player) args.getOne("player").get();
      ClickableMessage.Builder messageBuilder = ClickableMessage.builder(Text.of(
          TextColors.GRAY, "Alerts: ",
          TextColors.LIGHT_PURPLE, player.getName(), "\n"));
      for (GriefEvent event : plugin.getGriefEventCache().getListByTime()) {
        if (event.getEvent().getGriefer().equals(player)) {
          messageBuilder.addClickableCommand(
              String.valueOf(event.getCacheCode()),
              "/griefalert check " + event.getCacheCode(),
              Text.of("Check griefalert number " + event.getCacheCode())
          );
        }
      }
      src.sendMessage(messageBuilder.build().toText());
    }
    return CommandResult.success();
  }
}
