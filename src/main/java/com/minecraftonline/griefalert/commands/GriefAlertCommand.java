package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class GriefAlertCommand extends AbstractCommand {

  /**
   * The base command for all Grief Alert subcommands.
   */
  public GriefAlertCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND,
        Text.of("The root command for all Grief Alert commands")
    );
    addAlias("griefalert");
    addAlias("ga");
    addChild(new GriefAlertCheckCommand());
    addChild(new GriefAlertInfoCommand());
    addChild(new GriefAlertRecentCommand());
    addChild(new GriefAlertReturnCommand());
    addChild(new GriefAlertReloadCommand());
    addChild(new GriefAlertProfileCommand());

  }

  @Override
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    sendHelp(src);
    return CommandResult.success();
  }

}
