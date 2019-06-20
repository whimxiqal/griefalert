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
    	player.sendMessage(Text.of(TextColors.GOLD, "-------------"));
    	player.sendMessage(Text.of(TextColors.AQUA, "/griefalert toggle <debugmode> <true|false>"));
    }
}
