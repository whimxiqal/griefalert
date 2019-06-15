package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.core.GriefInstance;
import com.minecraftonline.griefalert.core.GriefAction.GriefType;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.DimensionType;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Event listener for using items.
 */
public class GriefUsedListener implements EventListener<UseItemStackEvent.Start> {
	
	/** The main plugin object. */
    private final GriefAlert plugin;

    /**
     * The generic constructor.
     * @param plugin The main plugin object
     */
    public GriefUsedListener(GriefAlert plugin) {
    	this.plugin = plugin;
    }

    @Override
    public void handle(@Nonnull UseItemStackEvent.Start event) {
    	// Make sure the event was caused by a player
    	// TODO Simplify
        if (event.getCause().root() instanceof Player) {
            Optional<Player> poption = event.getCause().first(Player.class);
            if (poption.isPresent()) {
                Player player = poption.get();
                ItemStackSnapshot itemStackSnapshot = event.getItemStackInUse();
                String itemID = itemStackSnapshot.getType().getId();
                DimensionType dType = player.getLocation().getExtent().getDimension().getType();
                if (plugin.isGriefAction(GriefType.USED, itemID, dType)) {
                    if (!plugin.getGriefAction(GriefType.USED, itemID, dType).isDenied()) {
                        plugin.getRealtimeGriefInstanceManager().processGriefInstance(new GriefInstance(plugin.getGriefAction(GriefType.USED, itemID, dType)).
                        		assignItem(itemStackSnapshot).assignGriefer(player));
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
