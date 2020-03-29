package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.Permissions;
import javax.annotation.Nonnull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

public class GriefAlertClearcacheCommand extends AbstractCommand {

  GriefAlertClearcacheCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_RELOAD,
        Text.of("Clear the runnng cache of alerts accessible by index")
    );
    addAlias("clearcache");
  }

  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) {
    GriefAlert.getInstance().getAlertManager().clearAll();
    src.sendMessage(Format.success("Alert cache cleared"));
    return CommandResult.success();
  }

}
