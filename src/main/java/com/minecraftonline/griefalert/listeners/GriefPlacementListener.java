package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertTracker;
import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class GriefPlacementListener implements EventListener<ChangeBlockEvent.Place> {
    private final AlertTracker tracker;

    public GriefPlacementListener(AlertTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void handle(@Nonnull ChangeBlockEvent.Place event) {
        Optional<Player> poption = event.getCause().first(Player.class);
        if (poption.isPresent()) {
            Player player = poption.get();
            List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
            for (Transaction<BlockSnapshot> transaction : transactions) {
                BlockSnapshot blockSnapshot = transaction.getFinal();
                String blockID = blockSnapshot.getState().getType().getName();
                if (GriefAlert.isUseWatched(blockID)) {
                    if (!GriefAlert.getUseAction(blockID).denied) {
                        tracker.log(player, GriefAlert.getUseAction(blockID).copy().assignBlock(blockSnapshot));
                    }
                    else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
