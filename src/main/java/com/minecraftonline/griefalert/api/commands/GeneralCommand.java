/* Created by PietElite */

package com.minecraftonline.griefalert.api.commands;

import com.minecraftonline.griefalert.api.data.Permission;
import com.minecraftonline.griefalert.util.Format;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * An abstract class for all MinecraftOnline commands from which to extend. This
 * class provides consistency to commands structure and visuals.
 */
public abstract class GeneralCommand implements CommandExecutor {

  private final Permission permission;
  private final Text description;
  private final List<GeneralCommand> commandChildren = new LinkedList<>();
  private final List<String> aliases = new LinkedList<>();
  private CommandElement commandElement = GenericArguments.none();

  /**
   * A general Grief Alert command object.
   *
   * @param permission  The permission which is required for this command
   * @param description The general description of this command functionality
   */
  public GeneralCommand(Permission permission,
                        Text description) {
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

  protected final void addChild(GeneralCommand generalCommand) {
    this.commandChildren.add(generalCommand);
  }

  protected final void setCommandElement(CommandElement commandElement) {
    this.commandElement = commandElement;
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

  /**
   * Send a help message about this command to the given <code>CommandSource</code>.
   *
   * @param source The source of the help message
   */
  protected final void sendHelp(CommandSource source) {
    PaginationList.builder().title(Format.info("Command Help : ", Format.bonus(
        "{",
        Text.joinWith(
            Text.of(", "),
            getAliases().stream().map(Text::of).collect(Collectors.toList())),
        "}")))
        .header(Text.of(
            TextColors.LIGHT_PURPLE, "Parameters:",
            Format.space(),
            TextColors.GRAY,
            buildCommandSpec().getUsage(source),
            Format.endLine(),
            TextColors.LIGHT_PURPLE, "Description:",
            Format.space(),
            TextColors.YELLOW, getDescription()))
        .contents(getChildren().stream()
            .filter(command -> source.hasPermission(command.getPermission().toString()))
            .map(command -> Text.of(
                TextColors.AQUA, Format.hover(
                    command.getAliases().get(0),
                    "Aliases: " + String.join(", ", command.getAliases())),
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
        .arguments(commandElement);
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
}
