package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class GriefAlertCommand extends AbstractCommand {


  public GriefAlertCommand(GriefAlert plugin) {
    super(plugin, GriefAlert.Permission.GRIEFALERT_COMMAND, Text.of("The root command for all Grief Alert commands"));
    addAlias("griefalert");
    addAlias("ga");
    addChild(new GriefAlertCheckCommand(plugin));
    addChild(new GriefAlertInfoCommand(plugin));
    addChild(new GriefAlertLogsCommand(plugin));
    addChild(new GriefAlertRecentCommand(plugin));
    addChild(new GriefAlertReturnCommand(plugin));
    addChild(new GriefAlertRollbackCommand(plugin));
    addChild(new GriefAlertToggleCommand(plugin));
    addChild(new GriefAlertReloadCommand(plugin));

  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    src.sendMessage(Text.of(TextColors.GREEN, "Success!"));
    return CommandResult.success();
  }

}
