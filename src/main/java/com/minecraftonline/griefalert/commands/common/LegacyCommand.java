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

package com.minecraftonline.griefalert.commands.common;

import com.minecraftonline.griefalert.api.data.Permission;
import com.minecraftonline.griefalert.util.Format;
import javax.annotation.Nonnull;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

/**
 * A command class for an old command on MinecraftOnline. The command will be
 * generated but it will simply route the user to a new command.
 */
public final class LegacyCommand extends GeneralCommand {

  private final String newCommand;

  private LegacyCommand(Permission permission, String oldCommand, String newCommand) {
    super(
        permission,
        Text.of(
            "The command was replaced with ",
            Format.command(
                "/" + newCommand,
                "/" + newCommand + " help", Text.of("Run help command"))),
        oldCommand);
    setCommandElement(GenericArguments.optional(
        GenericArguments.remainingJoinedStrings(
            Text.of("arguments"))));
    this.newCommand = newCommand;
  }

  /**
   * <code>LegacyCommand</code> factory method.
   * @param permission The permission required to perform this command
   * @param oldCommand The old command, which is what this command will be registered as.
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
    src.sendMessage(Format.info(
        "The command was replaced with ",
        Format.command("/" + newCommand, "/" + newCommand + " help", Text.of("Run help command"))));
    return CommandResult.success();
  }

}
