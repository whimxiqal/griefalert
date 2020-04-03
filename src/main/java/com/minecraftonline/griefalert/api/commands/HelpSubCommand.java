/* Created by PietElite */

package com.minecraftonline.griefalert.api.commands;

import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

/**
 * A sub-command for any {@link GeneralCommand} which allows for the viewing of
 * other sub-commands for usage.
 */
public class HelpSubCommand extends GeneralCommand {

  private final GeneralCommand parentCommand;

  HelpSubCommand(GeneralCommand parent) {
    super(parent.getPermission(), Text.of("Show all sub-commands"));
    this.parentCommand = parent;
    addAlias("help");
    addAlias("?");
  }

  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) {
    parentCommand.sendHelp(src);
    return CommandResult.success();
  }

}
