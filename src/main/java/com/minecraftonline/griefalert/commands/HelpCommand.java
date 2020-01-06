/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class HelpCommand extends AbstractCommand {

  private final AbstractCommand parentCommand;

  public HelpCommand(AbstractCommand parent) {
    super(parent.permission, Text.of("Show list of all sub-commands"));
    this.parentCommand = parent;
    addAlias("help");
  }

  @Override
  @NonnullByDefault
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    parentCommand.sendHelp(src);
    return CommandResult.success();
  }
}
