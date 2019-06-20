package com.minecraftonline.griefalert.commands;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.minecraftonline.griefalert.GriefAlert;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;

@CommandAlias("griefalert")
@Description("Toggles settings related to GriefAlert")
@Subcommand("toggle")
@CommandPermission("toggle")
public class GriefAlert_Toggle_Command extends GriefAlertCommand {

	public GriefAlert_Toggle_Command(GriefAlert plugin) {
		super(plugin);
	}

    @Default
    @Syntax("/griefalert toggle <debugmode>")
    public void onHelp(CommandSource source, CommandHelp help) {
    	Player player = (Player) source;
    	player.sendMessage(Text.of(TextColors.GOLD, "Help: /griefalert toggle"));
    	player.sendMessage(Text.of(TextColors.GOLD, "-------------"));
    	player.sendMessage(Text.of(TextColors.AQUA, "/griefalert toggle <debugmode> <true|false>"));
    }
    
    @Subcommand("debugmode")
    @Syntax("/griefalert toggle debugmode <true|false>")
    @Conditions("player")
    public void onToggleDebugmode(CommandSource source, boolean debugmode) {
    	plugin.debugMode = debugmode;
    	Player player = (Player) source;
    	player.sendMessage(Text.of(TextColors.GREEN, "Debug mode set to ").concat(Text.of(TextColors.LIGHT_PURPLE, Boolean.toString(debugmode))));
    }
}
