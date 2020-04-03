/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.commands.GeneralCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.Permissions;
import javax.annotation.Nonnull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

public class ClearcacheCommand extends GeneralCommand {

  ClearcacheCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_RELOAD,
        Text.of("Clear the Alert cache")
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
