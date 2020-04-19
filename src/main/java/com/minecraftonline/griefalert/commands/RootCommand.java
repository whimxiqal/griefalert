/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.api.commands.GeneralCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Reference;
import com.minecraftonline.griefalert.util.enums.Permissions;

import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class RootCommand extends GeneralCommand {

  /**
   * The base command for all Grief Alert sub-commands.
   */
  public RootCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND,
        Text.of("The root command for all Grief Alert commands")
    );
    addAlias("griefalert");
    addAlias("ga");
    addChild(new CheckCommand());
    addChild(new InfoCommand());
    addChild(new ProfileCommand());
    addChild(new QueryCommand());
    addChild(new ReloadCommand());
    addChild(new ReturnCommand());
    addChild(new ShowCommand());
    addChild(new ApplierCommand(
        Text.of("Rollback to previous states"),
        ApplierCommand.ApplyType.ROLLBACK,
        "rollback",
        "rb"));
    addChild(new ApplierCommand(
        Text.of("Restore to recent states"),
        ApplierCommand.ApplyType.RESTORE,
        "restore",
        "rs"));
    addChild(new LogsCommand());
    addChild(new ClearcacheCommand());
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) {
    src.sendMessage(Text.of(
        Format.bonus("==========================")));
    src.sendMessage(Text.of(
        Format.GRIEF_ALERT_THEME, TextStyles.BOLD, String.format(
            "GriefAlert v%s",
            Reference.VERSION),
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
        "Try the",
        Format.space(),
        Format.command(
            "help",
            "/griefalert help",
            Text.EMPTY),
        Format.space(),
        "command."));
    return CommandResult.success();
  }

}
