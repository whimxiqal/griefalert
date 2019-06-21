package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.core.GriefAction;
import com.minecraftonline.griefalert.core.GriefInstance;
import com.minecraftonline.griefalert.core.GriefAction.GriefType;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.world.DimensionType;

import javax.annotation.Nonnull;
import java.util.Optional;

public class GriefInteractListener implements EventListener<Event> {
	// TODO Combine with Entity Listener
	// TODO Test
	/** The main plugin object. */
    private final GriefAlert plugin;

    /**
     * The generic constructor.
     * @param plugin The main plugin object
     */
    public GriefInteractListener(GriefAlert plugin) {
    	this.plugin = plugin;
    }

	@Override
	public void handle(@Nonnull Event baseEvent) throws Exception {
		if (baseEvent instanceof InteractBlockEvent.Secondary) {
			handleInteractBlockEventSecondary((InteractBlockEvent.Secondary) baseEvent);
		} else if (baseEvent instanceof InteractEntityEvent) {
			handleInteractEntityEvent((InteractEntityEvent) baseEvent);
		}
	}
	
	public void handleInteractBlockEventSecondary(InteractBlockEvent.Secondary event) {
		if (event.getCause().root() instanceof Player) {
            Optional<Player> poption = event.getCause().first(Player.class);
            poption.ifPresent(player -> player.getItemInHand(HandTypes.MAIN_HAND).ifPresent(item -> {
                BlockSnapshot blockTarget = event.getTargetBlock();
                String blockID = blockTarget.getState().getType().getTranslation().get();
                if (!blockID.equals("minecraft:air")) {
                    DimensionType dType = blockTarget.getLocation().get().getExtent().getDimension().getType();
                    if (player.hasPermission("griefalert.degrief") && item.getType().getId().equals(plugin.getConfigString("degriefStickID"))) {
                        GriefInstance instance = new GriefInstance(GriefAction.DEGRIEF_ACTION,blockTarget,player);
                        plugin.getGriefManager().processGriefInstance(instance);
                        blockTarget.getLocation().get().getExtent().setBlockType(blockTarget.getPosition(), BlockTypes.AIR);
                    } else if (plugin.isGriefAction(GriefType.INTERACTED, blockID, dType)) {
                        if (!plugin.getGriefAction(GriefType.INTERACTED, blockID, dType).isDenied()) {
                            plugin.getGriefManager().processGriefInstance(new GriefInstance(
                            		plugin.getGriefAction(GriefType.INTERACTED, blockID, dType),
                            		blockTarget,
                            		player));
                        } else {
                            event.setCancelled(true);
                        }
                    }
                }
            }));
        }
	}
	
	public void handleInteractEntityEvent(InteractEntityEvent event) {
        if (event.getCause().root() instanceof Player) {
            Entity target = event.getTargetEntity();
            DimensionType dType = target.getLocation().getExtent().getDimension().getType();
            event.getCause().first(Player.class).ifPresent(player -> {
                String blockID = target.getType().getId();
                plugin.getDebugLogger().log(player.getName() + " interacted with an entity: " + blockID);
                if (plugin.isGriefAction(GriefType.INTERACTED, blockID, dType) && (event instanceof InteractEntityEvent.Secondary)) {
                	plugin.getDebugLogger().log("This is a registered grief action.");
                    GriefInstance instance = new GriefInstance(plugin.getGriefAction(GriefType.INTERACTED, blockID, dType),
                    		target,
                    		player);
                    if (!instance.isDenied()) {
                        plugin.getGriefManager().processGriefInstance(instance);
                    } else {
                        ((Cancellable) event).setCancelled(true);
                    }
                } else if (plugin.isGriefAction(GriefType.DESTROYED, blockID, dType) && (event instanceof InteractEntityEvent.Primary)) {
                	plugin.getDebugLogger().log("This is a registered grief action.");
                    GriefInstance instance = new GriefInstance(plugin.getGriefAction(GriefType.DESTROYED, blockID, dType),
                    		target,
                    		player);
                    if (!instance.isDenied()) {
                        plugin.getGriefManager().processGriefInstance(instance);
                    } else {
                        ((Cancellable) event).setCancelled(true);
                    }
                } else {
                	plugin.getDebugLogger().log("This is not a registered grief action.");
                }
            });
        }
	}
}
