package com.minecraftonline.griefalert;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

/* This is so i don't accidentally assign something to the watch list objects */
public final class ImmutableGriefAction extends GriefAction {

    public ImmutableGriefAction(String name, char color, boolean deny, boolean silent, Type type) {
        super(name, color, deny, silent, type);
    }

    @Override
    public GriefAction assignBlock(BlockSnapshot blockSnapshot) {
        throw new UnsupportedOperationException("Cannot assign Block to ImmutableGriefAction, copy the action first!");
    }

    @Override
    public GriefAction assignEntity(Entity entity) {
        throw new UnsupportedOperationException("Cannot assign Entity to ImmutableGriefAction, copy the action first!");
    }

    @Override
    public GriefAction assignGriefer(Player griefer) {
        throw new UnsupportedOperationException("Cannot assign Griefer to ImmutableGriefAction, copy the action first!");
    }

    @Override
    public GriefAction assignItem(ItemStackSnapshot itemStackSnapshot) {
        throw new UnsupportedOperationException("Cannot assign Item to ImmutableGriefAction, copy the action first!");
    }
}
