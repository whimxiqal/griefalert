package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertTracker;
import com.minecraftonline.griefalert.GriefAction;
import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.world.DimensionType;

import javax.annotation.Nonnull;

public class GriefEntityListener implements EventListener<InteractEntityEvent> {
    private final AlertTracker tracker;

    public GriefEntityListener(AlertTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    public void handle(@Nonnull InteractEntityEvent event) {
        if (event.getCause().root() instanceof Player) {
            Entity target = event.getTargetEntity();
            DimensionType dType = target.getLocation().getExtent().getDimension().getType();
            if (target instanceof Hanging) {
                if (event instanceof InteractEntityEvent.Primary) {
                    if (event.getCause().first(Player.class).isPresent()) {
                        Player player = event.getCause().first(Player.class).get();
                        String blockID = target instanceof Painting ? "minecraft:painting" : target instanceof ItemFrame ? "minecraft:item_frame" : "minecraft:leash_knot";
                        if (GriefAlert.isDestroyWatched(blockID, dType)) {
                            GriefAction action = GriefAlert.getDestroyedAction(blockID, dType).copy().assignEntity(target).assignGriefer(player);
                            if (!action.isDenied()) {
                                tracker.log(player, action);
                            } else {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            } else if (target instanceof ArmorStand) {
                Player player = event.getCause().first(Player.class).get();
                String blockID = "minecraft:armor_stand";
                if (event instanceof InteractEntityEvent.Primary) {
                    if (GriefAlert.isDestroyWatched(blockID, dType)) {
                        GriefAction action = GriefAlert.getDestroyedAction(blockID, dType).copy().assignEntity(target).assignGriefer(player);
                        if (!action.isDenied()) {
                            tracker.log(player, action);
                        } else {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
