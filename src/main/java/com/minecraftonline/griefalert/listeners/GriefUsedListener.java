package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertTracker;
import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.annotation.Nonnull;
import java.util.Optional;

public class GriefUsedListener implements EventListener<UseItemStackEvent.Start> {
    private final AlertTracker tracker;

    public GriefUsedListener(AlertTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void handle(@Nonnull UseItemStackEvent.Start event) {
        Optional<Player> poption = event.getCause().first(Player.class);
        if (poption.isPresent()) {
            Player player = poption.get();
            ItemStackSnapshot itemStackSnapshot = event.getItemStackInUse();
            String itemID = itemStackSnapshot.getType().getId();
            if (GriefAlert.isUseWatched(itemID)) {
                if (!GriefAlert.getUseAction(itemID).isDenied()) {
                    tracker.log(player, GriefAlert.getUseAction(itemID).assignItem(itemStackSnapshot).assignGriefer(player));
                }
                else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
