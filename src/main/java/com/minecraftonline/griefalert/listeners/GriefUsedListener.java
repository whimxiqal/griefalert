package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.core.GriefInstance;

import com.minecraftonline.griefalert.core.GriefAction.GriefType;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.DimensionType;

import javax.annotation.Nonnull;

import java.util.List;

/**
 * Event listener for using items.
 */
public class GriefUsedListener implements EventListener<Event> {
	// TODO combine with Placement Listener
	// TODO Test
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
    public void handle(@Nonnull Event baseEvent) {
    	// Make sure the event was caused by a player
    	if (!(baseEvent.getCause().root() instanceof Player)) return;
    	if (baseEvent instanceof UseItemStackEvent.Start) {
    		handleUseItemStackEvent((UseItemStackEvent.Start) baseEvent);
    	} else if (baseEvent instanceof ChangeBlockEvent.Place) {
    		handleChangeBlockEvent_Place((ChangeBlockEvent.Place) baseEvent);
    	} else if (baseEvent instanceof InteractItemEvent.Secondary) {
    		handleInteractItemEvent_Secondary((InteractItemEvent.Secondary) baseEvent);
    	}
    }

	private void handleUseItemStackEvent(UseItemStackEvent.Start event) {
    	plugin.getDebugLogger().log("Someone used an ItemStack.");
    	event.getCause().first(Player.class).ifPresent(player -> {
            ItemStackSnapshot itemStackSnapshot = event.getItemStackInUse();
            String itemID = itemStackSnapshot.getType().getId();
            DimensionType dType = player.getLocation().getExtent().getDimension().getType();
            plugin.getDebugLogger().log(player.getName() + " used a " + itemID + " in the " + dType.getName());
            if (plugin.isGriefAction(GriefType.USED, itemID, dType)) {
            	plugin.getDebugLogger().log("This is a registered Grief Action.");
                if (!plugin.getGriefAction(GriefType.USED, itemID, dType).isDenied()) {
                    plugin.getGriefManager().processGriefInstance(new GriefInstance(
                    		plugin.getGriefAction(GriefType.USED, itemID, dType),
                    		itemStackSnapshot,
                    		player));
                } else {
                    event.setCancelled(true);
                }
            } else {
            	plugin.getDebugLogger().log("This is not a registered Grief Action. Ignoring!");
            }
        });
    }
    
    private void handleChangeBlockEvent_Place(ChangeBlockEvent.Place event) {
    	plugin.getDebugLogger().log("Someone placed a block.");
    	event.getCause().first(Player.class).ifPresent(player -> {
            List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
            for (Transaction<BlockSnapshot> transaction : transactions) {
                BlockSnapshot blockSnapshot = transaction.getFinal();
                String blockID = blockSnapshot.getState().getType().getName();
                DimensionType dType = blockSnapshot.getLocation().get().getExtent().getDimension().getType();
                plugin.getDebugLogger().log(player.getName() + " placed a " + blockID + " in the " + dType.getName());
                if (plugin.isGriefAction(GriefType.USED, blockID, dType)) {
                	plugin.getDebugLogger().log("This is a registered Grief Action.");
                    if (!plugin.getGriefAction(GriefType.USED, blockID, dType).isDenied()) {
                        plugin.getGriefManager().processGriefInstance(new GriefInstance(
                        		plugin.getGriefAction(GriefType.USED, blockID, dType),
                        		blockSnapshot,
                        		player));
                    } else {
                        event.setCancelled(true);
                    }
                } else {
                	plugin.getDebugLogger().log("This is not a registered Grief Action. Ignoring!");
                }
            }
        });
    }
    
    private void handleInteractItemEvent_Secondary(InteractItemEvent.Secondary event) {
    	plugin.getDebugLogger().log("Someone interacted with an item in their hand.");
    	event.getCause().first(Player.class).ifPresent(player -> {
            ItemStackSnapshot itemStackSnapshot = event.getItemStack();
            String itemID = itemStackSnapshot.getType().getName();
            DimensionType dType = player.getLocation().getExtent().getDimension().getType();
            plugin.getDebugLogger().log(player.getName() + " interacted using a " + itemID + " in the " + dType.getName());
            if (plugin.isGriefAction(GriefType.USED, itemID, dType)) {
            	plugin.getDebugLogger().log("This is a registered Grief Action.");
                if (!plugin.getGriefAction(GriefType.USED, itemID, dType).isDenied()) {
                    plugin.getGriefManager().processGriefInstance(new GriefInstance(
                    		plugin.getGriefAction(GriefType.USED, itemID, dType),
                    		itemStackSnapshot,
                    		player));
                } else {
                    event.setCancelled(true);
                }
            } else {
            	plugin.getDebugLogger().log("This is not a registered Grief Action. Ignoring!");
            }
        });
	}
}
