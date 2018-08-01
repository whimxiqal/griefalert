package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertLogger;
import org.spongepowered.api.entity.hanging.Hanging;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.InteractEntityEvent;

import javax.annotation.Nonnull;

public class GriefHangingEntity extends AlertLogger implements EventListener<InteractEntityEvent> {

    @Override
    public void handle(@Nonnull InteractEntityEvent event) throws Exception {
        if (event.getTargetEntity() instanceof Hanging) {
            if (event instanceof InteractEntityEvent.Primary) {
                // Destruction of Painting or ItemFrame
            } else if (event.getTargetEntity() instanceof ItemFrame) {
                // Secondary only matters to ItemFrames
            }
        }
    }
}
