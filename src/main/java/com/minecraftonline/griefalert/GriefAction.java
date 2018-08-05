package com.minecraftonline.griefalert;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class GriefAction {

    public final String blockName;
    public final TextColor alertColor;
    public final boolean denied;
    public final boolean stealth;
    public final int onlyin; //TODO: This needs redone
    public final Type type;
    public BlockSnapshot block;
    public ItemStackSnapshot item;
    public Entity entity;
    public EntitySnapshot griefer;
    public Vector3d rotation;

    public enum Type {
        DEGRIEFED,
        DESTORYED,
        INTERACTED,
        USED,
    }

    public GriefAction(String name, char color, boolean deny, boolean silent, Type type) {
        this.blockName = name;
        this.alertColor = convertChar(color);
        this.denied = deny;
        this.stealth = silent;
        this.onlyin = 0;
        this.type = type;
    }

    public GriefAction(String name, char color, boolean deny, boolean silent, int onlyin, Type type) {
        this.blockName = name;
        this.alertColor = convertChar(color);
        this.denied = deny;
        this.stealth = silent;
        this.onlyin = onlyin;
        this.type = type;
    }

    public GriefAction(String name, TextColor color, boolean deny, boolean silent, int onlyin, Type type) {
        this.blockName = name;
        this.alertColor = color;
        this.denied = deny;
        this.stealth = silent;
        this.onlyin = onlyin;
        this.type = type;
    }

    public GriefAction assignBlock(BlockSnapshot blockSnapshot) {
        this.block = blockSnapshot;
        return this;
    }

    public GriefAction assignEntity(Entity entity) {
        this.entity = entity;
        return this;
    }

    public GriefAction assignGriefer(Player player) {
        this.griefer = player.createSnapshot();
        this.rotation = player.getRotation();
        return this;
    }

    public GriefAction assignItem(ItemStackSnapshot itemStackSnapshot) {
        this.item = itemStackSnapshot;
        return this;
    }

    public int getX() {
        if (block != null) {
            return block.getLocation().get().getBlockX();
        } else {
            return entity.getLocation().getBlockX();
        }
    }

    public int getY() {
        if (block != null) {
            return block.getLocation().get().getBlockY();
        } else {
            return entity.getLocation().getBlockY();
        }
    }

    public int getZ() {
        if (block != null) {
            return block.getLocation().get().getBlockZ();
        } else {
            return entity.getLocation().getBlockZ();
        }
    }

    public GriefAction copy() {
        return new GriefAction(blockName, alertColor, denied, stealth, onlyin, type);
    }

    private TextColor convertChar(char color) {
        switch (Character.toUpperCase(color)) {
            case '0':
                return TextColors.BLACK;
            case '1':
                return TextColors.DARK_BLUE;
            case '2':
                return TextColors.DARK_GREEN;
            case '3':
                return TextColors.DARK_AQUA;
            case '4':
                return TextColors.DARK_RED;
            case '5':
                return TextColors.DARK_PURPLE;
            case '6':
                return TextColors.GOLD;
            case '7':
                return TextColors.GRAY;
            case '8':
                return TextColors.DARK_GRAY;
            case '9':
                return TextColors.BLUE;
            case 'A':
                return TextColors.GREEN;
            case 'B':
                return TextColors.AQUA;
            case 'C':
                return TextColors.RED;
            case 'D':
                return TextColors.LIGHT_PURPLE;
            case 'E':
                return TextColors.YELLOW;
            default:
                return TextColors.WHITE;
        }
    }
}
