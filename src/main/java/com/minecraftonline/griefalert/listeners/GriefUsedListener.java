package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertTracker;
import com.minecraftonline.griefalert.GriefAlert;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.annotation.Nonnull;
import java.util.Optional;

public class GriefUsedListener extends AlertTracker implements EventListener<UseItemStackEvent.Start> {

    public GriefUsedListener(Logger logger) {
        super(logger);
    }

    @Override
    public void handle(@Nonnull UseItemStackEvent.Start event) {
        Optional<Player> poption = event.getCause().first(Player.class);
        if (poption.isPresent()) {
            Player player = poption.get();
            ItemStackSnapshot itemStackSnapshot = event.getItemStackInUse();
            String itemID = itemStackSnapshot.getType().getId();
            if (GriefAlert.isUseWatched(itemID) && !GriefAlert.getUseAction(itemID).denied) {
                log(player, GriefAlert.getUseAction(itemID).assignItem(itemStackSnapshot).assignEntity(player));
            }
        }
    }
}
