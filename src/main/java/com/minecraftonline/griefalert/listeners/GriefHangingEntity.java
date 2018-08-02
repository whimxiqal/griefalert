package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertTracker;
import com.minecraftonline.griefalert.GriefAction;
import com.minecraftonline.griefalert.GriefAlert;
import org.slf4j.Logger;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.InteractEntityEvent;

import javax.annotation.Nonnull;

public class GriefHangingEntity extends AlertTracker implements EventListener<InteractEntityEvent> {

    public GriefHangingEntity(Logger logger) {
        super(logger);
    }

    @Override
    public void handle(@Nonnull InteractEntityEvent event) {
        if (event.getTargetEntity() instanceof Hanging) {
            if (event instanceof InteractEntityEvent.Primary) {
                if (event.getCause().first(Player.class).isPresent()) {
                    Player player = event.getCause().first(Player.class).get();
                    Entity entity = event.getTargetEntity();
                    String blockID = entity instanceof Painting ? "minecraft:painting" : entity instanceof ItemFrame ? "minecraft:item_frame" : "minecraft:lead";

                    if (GriefAlert.isDestroyWatched(blockID)) {
                        GriefAction action = GriefAlert.getDestroyedAction(blockID).copy().assignEntity(entity);
                        if (!action.denied) {
                            log(player, action);
                        } else {
                            event.setCancelled(true);
                        }
                    }
                }
            } else if (event.getTargetEntity() instanceof ItemFrame) {
                // Secondary only matters to ItemFrames
            }
        }
    }
}
