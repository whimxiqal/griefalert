/*
 * MIT License
 *
 * Copyright (c) 2021 Pieter Svenson
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

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.commands.common.GeneralCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;

public class EnabletoolCommand extends GeneralCommand {

  EnabletoolCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_ENABLETOOL,
        Text.of("Enable or disable the GriefAlert tool")
    );
    setCommandElement(GenericArguments.string(Text.of("player")));
    addAlias("enabletool");
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) throws CommandException {
    GriefAlert.getInstance().getToolHandler().setToolEnabled(!GriefAlert.getInstance().getToolHandler().isToolEnabled());
    src.sendMessage(Format.success("Tool command was ", GriefAlert.getInstance().getToolHandler().isToolEnabled()
        ? Text.of(TextColors.BLUE, "enabled")
        : Text.of(TextColors.RED, "disabled")));
    return CommandResult.success();
  }

}
