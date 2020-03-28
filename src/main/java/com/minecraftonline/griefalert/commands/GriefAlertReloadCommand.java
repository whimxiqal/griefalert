/* Created by PietElite */

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

public class GriefAlertReloadCommand extends AbstractCommand {

  GriefAlertReloadCommand() {
    super(Permissions.GRIEFALERT_COMMAND_RELOAD, Text.of(
        "Reload all Grief Profiles from host"
    ));
    addAlias("reload");
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) {
    GriefAlert.getInstance().reload();
    src.sendMessage(Format.success("Grief Alert reloaded!"));
    return CommandResult.success();
  }

}
