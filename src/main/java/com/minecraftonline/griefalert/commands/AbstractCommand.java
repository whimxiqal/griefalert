package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCommand implements CommandExecutor {

  protected final GriefAlert plugin;
  public final GriefAlert.Permission permission;
  private final Text description;
  private List<AbstractCommand> commandChildren = new LinkedList<>();
  private List<String> aliases = new LinkedList<>();
  private List<CommandElement> commandElements = new LinkedList<>();

  public AbstractCommand(GriefAlert plugin, GriefAlert.Permission permission, Text description) {
    this.plugin = plugin;
    this.permission = permission;
    this.description = description;
    if (!(this instanceof HelpCommand)) {
      this.addChild(new HelpCommand(this));
    }
  }

  public AbstractCommand(GriefAlert plugin, GriefAlert.Permission permission, Text description, String primaryAlias) {
    this(plugin, permission, description);
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

  public List<AbstractCommand> getChildren() {
    return this.commandChildren;
  }

  public List<String> getAliases() {
    return this.aliases;
  }

  public List<CommandElement> getCommandElements() {
    return this.commandElements;
  }

  protected void sendHelp(CommandSource source) {
    source.sendMessage(Text.of(TextColors.GOLD, "==============="));
    source.sendMessage(Text.of(TextColors.GOLD, getAliases().get(0) + " : Command Help"));
    source.sendMessage(Text.of(TextColors.YELLOW, getDescription()));
    for (AbstractCommand command : getChildren()) {
      source.sendMessage(Text.of(TextColors.AQUA, command.getAliases().get(0), TextColors.GRAY, ": ", command.getDescription()));
    }
  }

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

  public Text getDescription() {
    return description;
  }
}
