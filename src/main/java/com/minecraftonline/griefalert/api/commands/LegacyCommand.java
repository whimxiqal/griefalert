/* Created by PietElite */

package com.minecraftonline.griefalert.api.commands;

import com.minecraftonline.griefalert.api.data.Permission;
import com.minecraftonline.griefalert.util.Format;
import javax.annotation.Nonnull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

/**
 * A command class for an old command on MinecraftOnline. The command will be
 * generated but it will simply route the user to a new command.
 */
public final class LegacyCommand extends AbstractCommand {

  private final String newCommand;

  private LegacyCommand(Permission permission, String oldCommand, String newCommand) {
    super(
        permission,
        Text.of("Replaced with /" + newCommand),
        oldCommand);
    this.newCommand = newCommand;
  }

  /**
   * <code>LegacyCommand</code> factory method.
   * @param permission The permission required to perform this command
   * @param oldCommand The old command, which is what this command will be registed as.
   *                   Use the format "command" instead of "/command"
   * @param newCommand The new command, which will perform the function which the old
   *                   command used to perform.
   * @return The corresponding LegacyCommand.
   */
  public static LegacyCommand of(Permission permission, String oldCommand, String newCommand) {
    return new LegacyCommand(permission, oldCommand, newCommand);
  }

  @Nonnull
  @Override
  public CommandResult execute(CommandSource src, @Nonnull CommandContext args) {
    src.sendMessage(Format.info("This command was replaced with /" + newCommand));
    return CommandResult.success();
  }

}
