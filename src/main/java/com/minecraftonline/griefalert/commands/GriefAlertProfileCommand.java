package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.api.data.Permission;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

public class GriefAlertProfileCommand extends AbstractCommand {

  // TODO: implement

  public GriefAlertProfileCommand(Permission permission, Text description) {
    super(permission, description);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    return null;
  }
}
