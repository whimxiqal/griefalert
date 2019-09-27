package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class GriefAlertReloadCommand extends AbstractCommand {

  public GriefAlertReloadCommand(GriefAlert plugin) {
    super(plugin, GriefAlert.Permission.GRIEFALERT_COMMAND_RELOAD, Text.of("Reload all Grief Profiles from host"));
    addAlias("reload");
  }

  @NonnullByDefault
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    this.plugin.getMuseum().reload();
    src.sendMessage(Text.of(TextColors.GREEN, "Profiles reloaded"));
    return CommandResult.success();
  }

}
