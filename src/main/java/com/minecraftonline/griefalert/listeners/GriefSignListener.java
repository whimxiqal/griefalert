package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertTracker;
import com.minecraftonline.griefalert.GriefAlert;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;

import javax.annotation.Nonnull;
import java.util.Optional;

public class GriefSignListener extends AlertTracker implements EventListener<ChangeSignEvent> {

    public GriefSignListener(Logger logger) {
        super(logger);
    }

    @Override
    public void handle(@Nonnull ChangeSignEvent event) {
        if (GriefAlert.readConfigBool("logSignsContent")) {
            Optional<Player> poption = event.getCause().first(Player.class);
            poption.ifPresent(player -> logSign(player, event.getTargetTile(), event.getText()));
        }
    }
}
