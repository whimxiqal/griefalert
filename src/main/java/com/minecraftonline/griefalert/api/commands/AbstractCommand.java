/* Created by PietElite */

package com.minecraftonline.griefalert.api.commands;

import java.util.LinkedList;
import java.util.List;

import com.minecraftonline.griefalert.api.data.Permission;
import com.minecraftonline.griefalert.commands.HelpCommand;
import com.minecraftonline.griefalert.util.Permissions;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;


public abstract class AbstractCommand implements CommandExecutor {

  public final Permission permission;
  private final Text description;
  private List<AbstractCommand> commandChildren = new LinkedList<>();
  private List<String> aliases = new LinkedList<>();
  private List<CommandElement> commandElements = new LinkedList<>();

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

  protected boolean addAlias(String alias) {
    return this.aliases.add(alias);
  }

  protected void addChild(AbstractCommand abstractCommand) {
    this.commandChildren.add(abstractCommand);
  }

  protected void addCommandElement(CommandElement commandElement) {
    this.commandElements.add(commandElement);
  }

  private List<AbstractCommand> getChildren() {
    return this.commandChildren;
  }

  public List<String> getAliases() {
    return this.aliases;
  }

  public List<CommandElement> getCommandElements() {
    return this.commandElements;
  }

  public void sendHelp(CommandSource source) {
    source.sendMessage(Text.of(TextColors.GOLD, "==============="));
    source.sendMessage(Text.of(TextColors.GOLD, getAliases().get(0) + " : Command Help"));
    source.sendMessage(Text.of(TextColors.YELLOW, getDescription()));
    source.sendMessage(Text.of(
        TextColors.LIGHT_PURPLE,
        "Usage: ",
        TextColors.GRAY,
        buildCommandSpec().getUsage(source)));
    getChildren().forEach((command) -> source.sendMessage(Text.of(
        TextColors.AQUA,
        command.getAliases().get(0),
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
        .executor(this);
    for (AbstractCommand command : commandChildren) {
      commandSpecBuilder.child(command.buildCommandSpec(), command.getAliases());
    }
    for (CommandElement element : commandElements) {
      commandSpecBuilder.arguments(element);
    }
    return commandSpecBuilder.build();
  }

  private Text getDescription() {
    return description;
  }
}
