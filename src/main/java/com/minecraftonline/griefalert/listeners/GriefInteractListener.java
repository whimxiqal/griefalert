package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertTracker;
import com.minecraftonline.griefalert.GriefAction;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.ImmutableGriefAction;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.InteractBlockEvent;

import javax.annotation.Nonnull;
import java.util.Optional;

public class GriefInteractListener implements EventListener<InteractBlockEvent.Secondary> {
    private final AlertTracker tracker;
    private final ImmutableGriefAction degrief = new ImmutableGriefAction("", 'F', false, true, 2, GriefAction.Type.DEGRIEFED);

    public GriefInteractListener(AlertTracker tracker) {
        this.tracker = tracker;
    }


    @Override
    public void handle(@Nonnull InteractBlockEvent.Secondary event) {
        Optional<Player> poption = event.getCause().first(Player.class);
        poption.ifPresent(player -> player.getItemInHand(HandTypes.MAIN_HAND).ifPresent(item -> {
            BlockSnapshot blockTarget = event.getTargetBlock();
            String blockID = blockTarget.getState().getType().getId();
            if (!blockID.equals("minecraft:air")) {
                if (player.hasPermission("griefalert.degrief") && item.getType().getId().equals(GriefAlert.readConfigStr("degriefStickID"))) {
                    GriefAction action = degrief.copy().assignBlock(blockTarget).assignGriefer(player);
                    tracker.log(player, action);
                    blockTarget.getLocation().get().getExtent().setBlockType(blockTarget.getPosition(), BlockTypes.AIR);
                }
                else if (GriefAlert.isInteractWatched(blockID)) {
                    if (!GriefAlert.getInteractAction(blockID).isDenied()) {
                        tracker.log(player, GriefAlert.getInteractAction(blockID).copy().assignBlock(blockTarget).assignGriefer(player));
                    }
                    else {
                        event.setCancelled(true);
                    }
                }
            }
        }));
    }
}
