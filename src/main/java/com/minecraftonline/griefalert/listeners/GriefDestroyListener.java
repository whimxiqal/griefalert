package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertTracker;
import com.minecraftonline.griefalert.GriefAlert;
import org.slf4j.Logger;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.util.List;
import java.util.Optional;

public class GriefDestroyListener extends AlertTracker implements EventListener<ChangeBlockEvent.Break> {

    public GriefDestroyListener(Logger logger) {
        super(logger);
    }

    @Override
    public void handle(ChangeBlockEvent.Break event) throws Exception {
        Optional<Player> poption = event.getCause().first(Player.class);
        if (poption.isPresent()) {
            Player player = poption.get();
            List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
            for (Transaction<BlockSnapshot> transaction : transactions) {
                BlockSnapshot blockSnapshot = transaction.getFinal();
                String blockID = blockSnapshot.getState().getType().getName();
                if (GriefAlert.isDestroyWatched(blockID)) {
                    log(player, GriefAlert.getUseAction(blockID).copy().assignBlock(blockSnapshot));
                }
            }
        }
    }
}
