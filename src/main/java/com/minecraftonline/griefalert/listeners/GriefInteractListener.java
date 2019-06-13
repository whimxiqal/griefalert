package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAction;
import com.minecraftonline.griefalert.GriefAction.GriefType;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.GriefInstance;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.world.DimensionType;

import javax.annotation.Nonnull;
import java.util.Optional;

public class GriefInteractListener implements EventListener<InteractBlockEvent.Secondary> {
    private final GriefAlert plugin;
    private final GriefAction degrief = new GriefAction(null, 'F', false, true, GriefAction.GriefType.DEGRIEFED);

    public GriefInteractListener(GriefAlert plugin) {
    	this.plugin = plugin;
    }


    @Override
    public void handle(@Nonnull InteractBlockEvent.Secondary event) {
        if (event.getCause().root() instanceof Player) {
            Optional<Player> poption = event.getCause().first(Player.class);
            poption.ifPresent(player -> player.getItemInHand(HandTypes.MAIN_HAND).ifPresent(item -> {
                BlockSnapshot blockTarget = event.getTargetBlock();
                String blockID = blockTarget.getState().getType().getId();
                if (!blockID.equals("minecraft:air")) {
                    DimensionType dType = blockTarget.getLocation().get().getExtent().getDimension().getType();
                    if (player.hasPermission("griefalert.degrief") && item.getType().getId().equals(plugin.getConfigString("degriefStickID"))) {
                        GriefInstance instance = new GriefInstance(degrief).assignBlock(blockTarget).assignGriefer(player);
                        plugin.getTracker().processGriefInstance(instance);
                        blockTarget.getLocation().get().getExtent().setBlockType(blockTarget.getPosition(), BlockTypes.AIR);
                    } else if (plugin.isGriefAction(GriefType.INTERACTED, blockID, dType)) {
                        if (!plugin.getGriefAction(GriefType.INTERACTED, blockID, dType).isDenied()) {
                            plugin.getTracker().processGriefInstance(new GriefInstance(plugin.getGriefAction(GriefType.INTERACTED, blockID, dType)).
                            		assignBlock(blockTarget).assignGriefer(player));
                        } else {
                            event.setCancelled(true);
                        }
                    }
                }
            }));
        }
    }
}
