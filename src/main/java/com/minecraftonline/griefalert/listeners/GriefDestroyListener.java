package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertTracker;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.GriefInstance;
import com.minecraftonline.griefalert.GriefAction.GriefType;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.world.DimensionType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class GriefDestroyListener implements EventListener<ChangeBlockEvent.Break> {
    private final AlertTracker tracker;
    private final GriefAlert plugin;

    public GriefDestroyListener(GriefAlert plugin, AlertTracker tracker) {
    	this.plugin = plugin;
        this.tracker = tracker;
    }

    @Override
    public void handle(@Nonnull ChangeBlockEvent.Break event) {
        if (event.getCause().root() instanceof Player) {
            Optional<Player> poption = event.getCause().first(Player.class);
            if (poption.isPresent()) {
                Player player = poption.get();
                List<Transaction<BlockSnapshot>> transactions = event.getTransactions();
                for (Transaction<BlockSnapshot> transaction : transactions) {
                    BlockSnapshot blockSnapshot = transaction.getOriginal();
                    String blockID = blockSnapshot.getState().getType().getId();
                    DimensionType dType = blockSnapshot.getLocation().get().getExtent().getDimension().getType();
                    if (plugin.isGriefAction(GriefType.DESTROYED, blockID, dType)) {
                        if (!plugin.getGriefAction(GriefType.DESTROYED, blockID, dType).isDenied()) {
                            tracker.log(player, new GriefInstance(plugin.getGriefAction(GriefType.DESTROYED, blockID, dType)).
                            		assignBlock(blockSnapshot).assignGriefer(player));
                        } else {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
