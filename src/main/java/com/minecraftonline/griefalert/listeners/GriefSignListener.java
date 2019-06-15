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
            if (plugin.getConfigBoolean("logSignsContent")) {
                Optional<Player> poption = event.getCause().first(Player.class);
                poption.ifPresent(player -> plugin.getRealtimeGriefInstanceManager().alert(player, event.getTargetTile(), event.getText()));
            }
        }
    }
}
