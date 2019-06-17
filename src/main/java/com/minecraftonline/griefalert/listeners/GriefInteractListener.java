package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.core.GriefAction;
import com.minecraftonline.griefalert.core.GriefInstance;
import com.minecraftonline.griefalert.core.GriefAction.GriefType;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.world.DimensionType;

import javax.annotation.Nonnull;
import java.util.Optional;
//TODO: PietElite: Fix
public class GriefInteractListener implements EventListener<InteractBlockEvent.Secondary> {
	
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
    public void handle(@Nonnull InteractBlockEvent.Secondary event) {
    	// Make sure the event was caused by a player
    	// TODO Simplify
        if (event.getCause().root() instanceof Player) {
            Optional<Player> poption = event.getCause().first(Player.class);
            poption.ifPresent(player -> player.getItemInHand(HandTypes.MAIN_HAND).ifPresent(item -> {
                BlockSnapshot blockTarget = event.getTargetBlock();
                String blockID = blockTarget.getState().getType().getId();
                if (!blockID.equals("minecraft:air")) {
                    DimensionType dType = blockTarget.getLocation().get().getExtent().getDimension().getType();
                    if (player.hasPermission("griefalert.degrief") && item.getType().getId().equals(plugin.getConfigString("degriefStickID"))) {
                        GriefInstance instance = new GriefInstance(GriefAction.DEGRIEF_ACTION,blockTarget,player);
                        plugin.getRealtimeGriefInstanceManager().processGriefInstance(instance);
                        blockTarget.getLocation().get().getExtent().setBlockType(blockTarget.getPosition(), BlockTypes.AIR);
                    } else if (plugin.isGriefAction(GriefType.INTERACTED, blockID, dType)) {
                        if (!plugin.getGriefAction(GriefType.INTERACTED, blockID, dType).isDenied()) {
                            plugin.getRealtimeGriefInstanceManager().processGriefInstance(new GriefInstance(
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
}
