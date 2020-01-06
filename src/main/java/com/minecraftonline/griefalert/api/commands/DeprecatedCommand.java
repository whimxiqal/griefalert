/* Created by PietElite */

package com.minecraftonline.griefalert.api.commands;

import com.minecraftonline.griefalert.api.data.Permission;
import com.minecraftonline.griefalert.util.Format;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class DeprecatedCommand extends AbstractCommand {

  final String newCommand;

  private DeprecatedCommand(Permission permission, String oldCommand, String newCommand) {
    super(
        permission,
        Text.of("Replaced with /" + newCommand),
        oldCommand);
    this.newCommand = newCommand;
  }

  public static DeprecatedCommand of(Permission permission, String oldCommand, String newCommand) {
    return new DeprecatedCommand(permission, oldCommand, newCommand);
  }

  @Override
  @NonnullByDefault
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    src.sendMessage(Format.info("This command was replaced with /" + newCommand));
    return CommandResult.success();
  }

}
