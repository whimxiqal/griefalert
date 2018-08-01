package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertLogger;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;

import javax.annotation.Nonnull;

public class GriefSign extends AlertLogger implements EventListener<ChangeSignEvent> {
    @Override
    public void handle(@Nonnull ChangeSignEvent event) throws Exception {

    }
}
