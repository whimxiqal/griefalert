/* Created by PietElite */

package com.minecraftonline.griefalert.api.commands;

import com.minecraftonline.griefalert.api.data.Permission;
import com.minecraftonline.griefalert.commands.HelpCommand;
import com.minecraftonline.griefalert.util.Format;
import java.util.LinkedList;
import java.util.List;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * An abstract class for all MinecraftOnline commands from which to extend. This
 * class provides consistency to commands structure and visuals.
 */
public abstract class AbstractCommand implements CommandExecutor {

  public final Permission permission;
  private final Text description;
  private final List<AbstractCommand> commandChildren = new LinkedList<>();
  private final List<String> aliases = new LinkedList<>();
  private CommandElement commandElement = GenericArguments.none();

  /**
   * A general Grief Alert command object.
   *
   * @param permission  The permission which is required for this command
   * @param description The general description of this command functionality
   */
  public AbstractCommand(Permission permission,
                         Text description) {
    this.permission = permission;
    this.description = description;
    if (!(this instanceof HelpCommand)) {
      this.addChild(new HelpCommand(this));
    }
  }

  public AbstractCommand(Permission permission,
                         Text description,
                         String primaryAlias) {
    this(permission, description);
    addAlias(primaryAlias);
  }

  protected final void addAlias(String alias) {
    this.aliases.add(alias);
  }

  protected final void addChild(AbstractCommand abstractCommand) {
    this.commandChildren.add(abstractCommand);
  }

  protected final void setCommandElement(CommandElement commandElement) {
    this.commandElement = commandElement;
  }

  @SuppressWarnings("WeakerAccess")
  protected final List<AbstractCommand> getChildren() {
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
   * @param source The source of the help message
   */
  public final void sendHelp(CommandSource source) {
    source.sendMessage(Format.heading(String.join("|", getAliases()) + " : Command Help"));
    source.sendMessage(Text.of(TextColors.YELLOW, getDescription()));
    source.sendMessage(Text.of(
        TextColors.LIGHT_PURPLE,
        "Usage: ",
        TextColors.GRAY,
        buildCommandSpec().getUsage(source)));
    getChildren().forEach((command) -> source.sendMessage(Text.of(
        TextColors.AQUA,
        String.join("|", command.getAliases()),
        TextColors.GRAY, ": ", command.getDescription())
    ));
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
}
