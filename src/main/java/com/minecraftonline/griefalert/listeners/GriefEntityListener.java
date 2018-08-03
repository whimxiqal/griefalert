package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertTracker;
import com.minecraftonline.griefalert.GriefAction;
import com.minecraftonline.griefalert.GriefAlert;
import org.slf4j.Logger;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.InteractEntityEvent;

import javax.annotation.Nonnull;

public class GriefEntityListener extends AlertTracker implements EventListener<InteractEntityEvent> {

    public GriefEntityListener(Logger logger) {
        super(logger);
    }

    @Override
    public void handle(@Nonnull InteractEntityEvent event) {
        Entity target = event.getTargetEntity();
        if (target instanceof Hanging) {
            if (event instanceof InteractEntityEvent.Primary) {
                if (event.getCause().first(Player.class).isPresent()) {
                    Player player = event.getCause().first(Player.class).get();
                    String blockID = target instanceof Painting ? "minecraft:painting" : target instanceof ItemFrame ? "minecraft:item_frame" : "minecraft:leash_knot";

                    if (GriefAlert.isDestroyWatched(blockID)) {
                        GriefAction action = GriefAlert.getDestroyedAction(blockID).copy().assignEntity(target);
                        if (!action.denied) {
                            log(player, action);
                        } else {
                            event.setCancelled(true);
                        }
                    }
                }
            } else if (target instanceof ItemFrame) {
                if (event.getCause().first(Player.class).isPresent()) {
                    Player player = event.getCause().first(Player.class).get();
                    if (target.get(Keys.REPRESENTED_ITEM).isPresent()) {
                        System.out.println(target.get(Keys.REPRESENTED_ITEM).get().getType().getId());
                    }
                }
            }
        } else if (target instanceof ArmorStand) {
            Player player = event.getCause().first(Player.class).get();
            String blockID = "minecraft:armor_stand";
            if (event instanceof InteractEntityEvent.Primary) {
                if (event.getCause().first(Player.class).isPresent() && GriefAlert.isDestroyWatched(blockID)) {
                    GriefAction action = GriefAlert.getDestroyedAction(blockID).copy().assignEntity(target);
                    if (!action.denied) {
                        log(player, action);
                    } else {
                        event.setCancelled(true);
                    }
                }
            } else if (event.getCause().first(Player.class).isPresent() && GriefAlert.isDestroyWatched(blockID)) {
                // I have no idea right now
            }
        }
    }
}
