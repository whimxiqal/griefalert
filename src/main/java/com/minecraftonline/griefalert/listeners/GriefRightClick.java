package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.AlertLogger;
import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.ItemTypes;

import javax.annotation.Nonnull;
import java.util.Optional;

public class GriefRightClick extends AlertLogger implements EventListener<InteractBlockEvent.Secondary> {
    @Override
    public void handle(@Nonnull InteractBlockEvent.Secondary event) throws Exception {
        Optional<Player> poption = event.getCause().first(Player.class);
        poption.ifPresent(player -> player.getItemInHand(HandTypes.MAIN_HAND).ifPresent(item -> {
            BlockSnapshot blockTarget = event.getTargetBlock();
            String blockID = blockTarget.getState().getType().getName();
            if (item.getType() == ItemTypes.STICK && player.hasPermission("griefalert.degrief")) {
                log(player, event.getTargetBlock(), Action.DEGRIEF);
            } else if (GriefAlert.isRightClickWatched(blockID)) {
                log(player, event.getTargetBlock(), Action.RIGHTCLICK);
            }
        }));
    }
}
