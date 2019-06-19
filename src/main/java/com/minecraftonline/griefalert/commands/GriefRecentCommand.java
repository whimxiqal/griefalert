package com.minecraftonline.griefalert.commands;

import static org.spongepowered.api.text.format.TextColors.RED;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.core.GriefInstance;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;

@CommandPermission("griefalert.command.grecent")
@CommandAlias("grecent")
public class GriefRecentCommand extends BaseCommand {

	/** The main plugin object. */
    private final GriefAlert plugin;

    /**
     * Basic constructor.
     * @param plugin The main plugin object.
     */
    public GriefRecentCommand(GriefAlert plugin) {
        this.plugin = plugin;
    }

    @Default
    @Syntax("<player>")
    @Conditions("player")
    public CommandResult onGrecent(CommandSource src, String username) {
        Player player = (Player) src;
        player.sendMessage(Text.builder("Showing all recent grief alerts from player " + username).color(RED).build());
        for (Pair<Integer,GriefInstance> griefInstance : plugin.getRealtimeGriefInstanceManager().getRecentGriefInstances()) {
        	// TODO combine repeated griefalerts into the same line with different clickable numbers representing the specific grief instance ids
        	if (username.equals(griefInstance.getValue().getGrieferAsPlayer().getName()))
        		player.sendMessage(plugin.getRealtimeGriefInstanceManager().generateAlertMessage(griefInstance.getKey(), griefInstance.getValue()));
        }
        return CommandResult.success();
    }

}
