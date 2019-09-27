package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class GriefAlertInfoCommand extends AbstractCommand {

  public GriefAlertInfoCommand(GriefAlert plugin) {
    super(plugin, GriefAlert.Permission.GRIEFALERT_COMMAND_INFO, Text.of("Get info about a given grief alert"));
    addAlias("info");
    addAlias("i");
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    src.sendMessage(Text.of(TextColors.GREEN, "Double success!"));
    return CommandResult.success();
  }
}
