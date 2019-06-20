package com.minecraftonline.griefalert.commands;

import static org.spongepowered.api.text.format.TextColors.RED;

import java.util.LinkedList;
import java.util.List;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.core.GriefInstance;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
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

    
    // TODO make the username autocomplete
    @Default
    @Syntax("<player>")
    @Conditions("player")
    @CommandCompletion("@players")
    public void onGrecent(CommandSource src, String username) {
    	
        Player player = (Player) src;
        player.sendMessage(Text.builder("Showing all recent grief alerts from player " + username).color(RED).build());
        List<GriefInstance> repeatedIncidents = new LinkedList<GriefInstance>();
        for (GriefInstance griefInstance : plugin.getGriefManager().getRecentGriefInstances()) {
        	if (!username.equals(griefInstance.getGrieferAsPlayer().getName())) 
        		continue;
        	if (!repeatedIncidents.isEmpty() && 
        			!repeatedIncidents.get(0).isAnotherOf(griefInstance)) {
        		// Only if there is there is something in the repeatedInstance list and an instance in the list does not
        		// match with the new instance, will the list be cleared and a new one will start
        		player.sendMessage(plugin.getGriefManager().generateAlertMessage(repeatedIncidents));
        		repeatedIncidents.clear();
        	}
        	repeatedIncidents.add(griefInstance);
        }
        // We need to send the message one more time in case the last alert is a repeated alert
        player.sendMessage(plugin.getGriefManager().generateAlertMessage(repeatedIncidents));
    }
}
