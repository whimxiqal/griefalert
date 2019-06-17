package com.minecraftonline.griefalert.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;

import com.minecraftonline.griefalert.GriefAlert;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Event listener for using items.
 */
public class GriefSignListener implements EventListener<ChangeSignEvent> {
	
	/** The main plugin object. */
    private final GriefAlert plugin;

    /**
     * The generic constructor.
     * @param plugin The main plugin object
     */
    public GriefSignListener(GriefAlert plugin) {
    	this.plugin = plugin;
    }

    @Override
    public void handle(@Nonnull ChangeSignEvent event) {
    	// Make sure the event was caused by a player
    	// TODO Simplify
        if (event.getCause().root() instanceof Player) {
        	plugin.getDebugLogger().log("Someone placed a sign. Checking if logging sign content...");
            if (plugin.getConfigBoolean("logSignsContent")) {
                Optional<Player> poption = event.getCause().first(Player.class);
                if (poption.isPresent()) {
                	plugin.getDebugLogger().log("Logging sign content.");
                	plugin.getRealtimeGriefInstanceManager().alert(poption.get(), event.getTargetTile(), event.getText());
                	return;
                } else {
                	plugin.getDebugLogger().log("Player who placed the sign not found. Ignored.");
                	return;
                }
            } else {
            	plugin.getDebugLogger().log("Not logging sign content. Ignored.");
            	return;
            }
        }
    }
}
