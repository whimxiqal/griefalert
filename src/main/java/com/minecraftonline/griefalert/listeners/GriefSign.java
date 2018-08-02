package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertTracker;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;

import javax.annotation.Nonnull;
import java.util.Optional;

public class GriefSign extends AlertTracker implements EventListener<ChangeSignEvent> {

    public GriefSign(Logger logger) {
        super(logger);
    }

    @Override
    public void handle(@Nonnull ChangeSignEvent event) {
        Optional<Player> poption = event.getCause().first(Player.class);
        poption.ifPresent(player -> logSign(player, event.getTargetTile(), event.getText()));
    }
}
