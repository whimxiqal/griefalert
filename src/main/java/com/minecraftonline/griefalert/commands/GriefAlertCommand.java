package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
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
        GriefAlert.Permission.GRIEFALERT_COMMAND,
        Text.of("The root command for all Grief Alert commands")
    );
    addAlias("griefalert");
    addAlias("ga");
    addChild(new GriefAlertCheckCommand());
    addChild(new GriefAlertInfoCommand());
    addChild(new GriefAlertRecentCommand());
    addChild(new GriefAlertReturnCommand());
    addChild(new GriefAlertBuilderCommand());
    addChild(new GriefAlertReloadCommand());

  }

  @Override
  @NonnullByDefault
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    src.sendMessage(Text.of(TextColors.GREEN, "Success!"));
    return CommandResult.success();
  }

}
