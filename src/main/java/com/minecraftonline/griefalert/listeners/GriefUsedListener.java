package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.GriefInstance;
import com.minecraftonline.griefalert.GriefAction.GriefType;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.DimensionType;

import javax.annotation.Nonnull;
import java.util.Optional;

public class GriefUsedListener implements EventListener<UseItemStackEvent.Start> {
    private final GriefAlert plugin;

    public GriefUsedListener(GriefAlert plugin) {
    	this.plugin = plugin;
    }

    @Override
    public void handle(@Nonnull UseItemStackEvent.Start event) {
        if (event.getCause().root() instanceof Player) {
            Optional<Player> poption = event.getCause().first(Player.class);
            if (poption.isPresent()) {
                Player player = poption.get();
                ItemStackSnapshot itemStackSnapshot = event.getItemStackInUse();
                String itemID = itemStackSnapshot.getType().getId();
                DimensionType dType = player.getLocation().getExtent().getDimension().getType();
                if (plugin.isGriefAction(GriefType.USED, itemID, dType)) {
                    if (!plugin.getGriefAction(GriefType.USED, itemID, dType).isDenied()) {
                        plugin.getTracker().processGriefInstance(new GriefInstance(plugin.getGriefAction(GriefType.USED, itemID, dType)).
                        		assignItem(itemStackSnapshot).assignGriefer(player));
                    } else {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
