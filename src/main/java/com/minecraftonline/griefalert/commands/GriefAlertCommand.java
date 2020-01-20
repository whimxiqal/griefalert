/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Permissions;
import com.minecraftonline.griefalert.util.Reference;

import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class GriefAlertCommand extends AbstractCommand {

  /**
   * The base command for all Grief Alert sub-commands.
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
    addChild(new GriefAlertProfileCommand());
    addChild(new GriefAlertQueryCommand());
    addChild(new GriefAlertReloadCommand());
    addChild(new GriefAlertReturnCommand());
    addChild(new GriefAlertShowCommand());
    addChild(new GriefAlertRollbackCommand());

  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) {
    src.sendMessage(Text.of(
        Format.bonus("=============")));
    src.sendMessage(Text.of(
        Format.GRIEF_ALERT_THEME, TextStyles.BOLD, "GriefAlert",
        Format.space(),
        TextStyles.RESET, Format.bonus("by MinecraftOnline")));
    src.sendMessage(Text.of(
        TextColors.AQUA, "Authors: ",
        Format.bonus(String.join(", ", Reference.AUTHORS))));
    src.sendMessage(Format.bonus(
        "Check out the",
        Format.space(),
        Format.url("website", Reference.WEBSITE),
        Format.space(),
        "or",
        Format.space(),
        Format.url("source code", Reference.SOURCE),
        "."));
    src.sendMessage(Format.bonus(
        "Try: ",
        TextColors.YELLOW, Format.command(
            String.format("/%s help", getAliases().get(0)),
            "/griefalert help",
            Text.EMPTY)));
    return CommandResult.success();
  }

}
