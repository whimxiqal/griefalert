package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class GriefAlertRollbackCommand extends AbstractCommand {

  GriefAlertRollbackCommand(GriefAlert plugin) {
    super(
        plugin,
        GriefAlert.Permission.GRIEFALERT_COMMAND_ROLLBACK,
        Text.of("Rollback data using grief alert logs")
    );
    addAlias("rollback");
    addAlias("rb");
  }

  @Override
  @NonnullByDefault
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    src.sendMessage(Text.of(TextColors.GREEN, "Double success!"));
    return CommandResult.success();
  }
}
