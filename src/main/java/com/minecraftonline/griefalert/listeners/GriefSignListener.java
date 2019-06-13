package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;

import javax.annotation.Nonnull;
import java.util.Optional;
//TODO: PietElite: Fix
public class GriefSignListener implements EventListener<ChangeSignEvent> {
    private final GriefAlert plugin;

    public GriefSignListener(GriefAlert plugin) {
    	this.plugin = plugin;
    }

    @Override
    public void handle(@Nonnull ChangeSignEvent event) {
        if (event.getCause().root() instanceof Player) {
            if (plugin.getConfigBoolean("logSignsContent")) {
                Optional<Player> poption = event.getCause().first(Player.class);
                poption.ifPresent(player -> plugin.getRealtimeGriefInstanceManager().alert(player, event.getTargetTile(), event.getText()));
            }
        }
    }
}
