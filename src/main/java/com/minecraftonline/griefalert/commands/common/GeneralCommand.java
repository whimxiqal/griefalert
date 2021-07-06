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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

/**
 * An abstract class for all MinecraftOnline commands from which to extend. This
 * class provides consistency to commands structure and visuals.
 */
public abstract class GeneralCommand implements CommandExecutor {

  private final Permission permission;
  private final Text description;
  private final List<GeneralCommand> commandChildren = new LinkedList<>();
  private final List<String> aliases = new LinkedList<>();
  private final Map<String, Text> flagDescriptions = new HashMap<>();
  private final Map<String, Boolean> flagValueState = new HashMap<>();
  private CommandElement commandElement = GenericArguments.none();

  /**
   * A general Grief Alert command object.
   *
   * @param permission  The permission which is required for this command
   * @param description The general description of this command functionality
   */
  public GeneralCommand(@Nonnull Permission permission,
                        @Nonnull Text description) {
    this.permission = permission;
    this.description = description;
    if (!(this instanceof HelpSubCommand)) {
      this.addChild(new HelpSubCommand(this));
    }
  }


  public GeneralCommand(Permission permission,
                        Text description,
                        String primaryAlias) {
    this(permission, description);
    addAlias(primaryAlias);
  }

  protected final void addAlias(String alias) {
    this.aliases.add(alias);
  }

  protected final void addChild(GeneralCommand child) {
    this.commandChildren.add(child);
  }

  @SuppressWarnings("WeakerAccess")
  protected final List<GeneralCommand> getChildren() {
    return this.commandChildren;
  }

  public final List<String> getAliases() {
    return this.aliases;
  }

  @SuppressWarnings("unused")
  public final CommandElement getCommandElement() {
    return this.commandElement;
  }

  protected final void setCommandElement(CommandElement commandElement) {
    this.commandElement = commandElement;
  }

  public final void addFlagDescription(FlagDescription flagDescription) {
    addFlagDescription(flagDescription.flag, flagDescription.description, flagDescription.hasValue);
  }

  public final void addFlagDescription(String flag, Text description, boolean valueFlag) {
    this.flagDescriptions.put(flag, description);
    this.flagValueState.put(flag, valueFlag);
  }

  /**
   * Send a help message about this command to the given <code>CommandSource</code>.
   *
   * @param source The source of the help message
   */
  protected final void sendHelp(CommandSource source) {
    PaginationList.builder().title(Format.info("Command Help ", Format.bonus(
        "{",
        Text.joinWith(
            Text.of(", "),
            getAliases().stream().map(Text::of).collect(Collectors.toList())),
        "}")))
        .header(Text.of(
            TextColors.LIGHT_PURPLE, "Parameters >",
            Format.space(),
            TextColors.GRAY,
            buildCommandSpec().getUsage(source),
            Format.endLine(),
            TextColors.LIGHT_PURPLE, "Description >",
            Format.space(),
            TextColors.YELLOW, getDescription(),
            flagDescriptions.isEmpty()
                ? Text.EMPTY
                : Text.of(
                Format.endLine(),
                TextColors.LIGHT_PURPLE, "Flags > ",
                Text.joinWith(Text.of(" "), flagDescriptions.entrySet().stream()
                    .map(entry -> {
                      boolean isValueFlag = flagValueState.get(entry.getKey());
                      return Format.hover(Text.of(TextStyles.ITALIC,
                          TextColors.DARK_GRAY, "[",
                          isValueFlag
                              ? TextColors.GREEN
                              : TextColors.GOLD,
                          "-", entry.getKey(),
                          TextColors.DARK_GRAY, "]"),
                          Text.of(isValueFlag
                                  ? Text.of(TextColors.GREEN, "Flag - Requires Value")
                                  : Text.of(TextColors.GOLD, "Flag"),
                              Format.endLine(),
                              TextColors.RESET, entry.getValue()));
                    })
                    .collect(Collectors.toList())))))
        .contents(getChildren().stream()
            .filter(command -> source.hasPermission(command.getPermission().toString()))
            .map(command -> Text.of(
                TextColors.AQUA, Format.hover(
                    command.getAliases().get(0),
                    "Aliases: " + String.join(", ", command.getAliases()))
                    .toBuilder()
                    .onClick(TextActions.executeCallback(command::sendHelp))
                    .build(),
                Format.space(),
                TextColors.WHITE, command.getDescription()))
            .collect(Collectors.toList()))
        .padding(Format.bonus("="))
        .build()
        .sendTo(source);
  }

  /**
   * Build the Command Spec required by the Sponge command registrar.
   *
   * @return the Command Spec
   */
  public CommandSpec buildCommandSpec() {
    CommandSpec.Builder commandSpecBuilder = CommandSpec.builder()
        .description(this.description)
        .permission(this.permission.toString())
        .executor(this)
        .arguments(GenericArguments.optional(commandElement));
    commandChildren.forEach(
        (child) ->
            commandSpecBuilder.child(child.buildCommandSpec(), child.getAliases()));
    return commandSpecBuilder.build();
  }

  private Text getDescription() {
    return description;
  }

  Permission getPermission() {
    return permission;
  }

  /**
   * An enumeration of all descriptions of flags for better documentation
   * in help menus on commands.
   */
  public enum FlagDescription {
    AFTER("a",
        Text.of("Use events ", TextColors.AQUA, " after", TextColors.RESET, " this date"),
        true),
    BEFORE("b",
        Text.of("Use events ", TextColors.AQUA, " before", TextColors.RESET, " this date"),
        true),
    PLAYER("p",
        Text.of("Use events caused by this ", TextColors.AQUA, "player"),
        true),
    TARGET("t",
        Text.of("Use events in which this block or other object was the ", TextColors.AQUA, "target"),
        true),
    EVENT("e",
        Text.of("Use events caused via this ", TextColors.AQUA, "event"),
        true);
    String flag;
    Text description;
    boolean hasValue;

    FlagDescription(String flag, Text description, boolean hasValue) {
      this.flag = flag;
      this.description = description;
      this.hasValue = hasValue;
    }
  }

}
