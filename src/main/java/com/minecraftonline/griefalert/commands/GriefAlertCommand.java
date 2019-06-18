package com.minecraftonline.griefalert.commands;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.minecraftonline.griefalert.GriefAlert;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;

@CommandPermission("griefalert.command")
@CommandAlias("griefalert")
public class GriefAlertCommand extends BaseCommand {

	/** The main plugin object. */
    protected final GriefAlert plugin;

    /**
     * Basic constructor.
     * @param plugin The main plugin object.
     */
    public GriefAlertCommand(GriefAlert plugin) {
        this.plugin = plugin;
    }
    
    @Default
    @HelpCommand
    @Conditions("player")
    public void onHelp(CommandSource source, CommandHelp help){
    	Player player = (Player) source;
    	player.sendMessage(Text.of(TextColors.GOLD, "Help: /griefalert"));
    	// TODO finish help message
    }
}
    /*
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player)) {  // Check if a non-player is running the command
        	throw new CommandException(Text.of("Only in game players can use this command!"));
        }
		Player player = (Player) src;
		if (args.getOne("arg1").isPresent()) {
			String arg1 = (String) args.getOne("arg1").get();
			switch (arg1) {
			case "toggle":
				if (args.getOne("arg2").isPresent()) {
					String arg2 = (String) args.getOne("arg2").get();
					switch (arg2) {
					case "debugmode":
						if (args.getOne("arg3").isPresent()) {
							String arg3 = (String) args.getOne("arg3").get();
							try {
								boolean arg3Bool = Boolean.valueOf(arg3);
								plugin.debugMode = arg3Bool;
								player.sendMessage(Text.builder("Debug mode set to " + String.valueOf(arg3Bool)).color(TextColors.GREEN).build());
								return CommandResult.success();
							} catch (Exception e) {
								throw new CommandException(Text.of("Invalid arguments."));
							}
						} else {
							throw new CommandException(Text.of("Invalid arguments."));
						}
					default:
						throw new CommandException(Text.of("Invalid arguments."));
					}
				} else {
					throw new CommandException(Text.of("Invalid arguments."));
				}
			default:
				throw new CommandException(Text.of("Invalid arguments."));
			}
			
		} else {
			throw new CommandException(Text.of("Invalid arguments."));
		}
		
	}
	*/
