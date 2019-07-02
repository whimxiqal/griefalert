package com.minecraftonline.griefalert.commands;

import java.util.NoSuchElementException;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.minecraftonline.griefalert.GriefAlert;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;

@CommandPermission("griefalert.command.greturn")
@CommandAlias("greturn")
public class GriefReturnCommand extends BaseCommand {

	/** The main plugin object. */
    private final GriefAlert plugin;

    /**
     * Basic constructor.
     * @param plugin The main plugin object.
     */
    public GriefReturnCommand(GriefAlert plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onGreturn(CommandSource src) {
        Player player = (Player) src;
        try {
        	EntitySnapshot snapshot = plugin.getGriefManager().getGriefCheckerPriorSnapshot(player);
        	if (snapshot == null) {
        		player.sendMessage(Text.of(TextColors.RED, "No location found from which you checked a grief report."));
        		return;
        	}
        	if (player.setLocationAndRotationSafely(
        			snapshot.getLocation().get(),
        			snapshot.getTransform().get().getRotation())) {
                player.sendMessage(Text.of(TextColors.GREEN, "You are returned to your previous location."));
        	} else {
        		player.sendMessage(Text.of(TextColors.RED, "A safe return location could not be found."));
        	}
        	return;
        } catch (NoSuchElementException e) {
        	player.sendMessage(Text.of(TextColors.RED, "An internal error occured."));
        	plugin.getLogger().warn("The information in their entity snapshot should not be accessed when " + player.getName() + " used /greturn. ");
        	return;
        }
    }
}
