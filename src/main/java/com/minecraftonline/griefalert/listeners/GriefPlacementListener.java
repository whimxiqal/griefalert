package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertTracker;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.GriefInstance;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.world.DimensionType;

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
        if (event.getCause().root() instanceof Player) {
            Optional<Player> poption = event.getCause().first(Player.class);
            if (poption.isPresent()) {
                Player player = poption.get();
                List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
                for (Transaction<BlockSnapshot> transaction : transactions) {
                    BlockSnapshot blockSnapshot = transaction.getFinal();
                    String blockID = blockSnapshot.getState().getType().getName();
                    DimensionType dType = blockSnapshot.getLocation().get().getExtent().getDimension().getType();
                    if (GriefAlert.isUseWatched(blockID, dType)) {
                        if (!GriefAlert.getUseAction(blockID, dType).isDenied()) {
                            tracker.log(player, new GriefInstance(GriefAlert.getUseAction(blockID, dType)).assignBlock(blockSnapshot).assignGriefer(player));
                        } else {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
