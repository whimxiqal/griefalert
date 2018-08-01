package com.minecraftonline.griefalert;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;

public abstract class AlertLogger {

    public enum Action {
        DEGRIEF,
        DESTORY,
        PLACE,
        RIGHTCLICK,
        USE,

    }

    protected final void log(Player player, BlockSnapshot block, Action action) {

    }
}
