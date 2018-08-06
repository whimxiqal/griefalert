package com.minecraftonline.griefalert;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public class GriefAction {

    private final String blockName;
    private final TextColor alertColor;
    private final boolean denied;
    private final boolean stealth;
    private final Type type;
    private BlockSnapshot block;
    private ItemStackSnapshot item;
    private Entity entity;
    private EntitySnapshot griefer;
    private Vector3d rotation;

    public enum Type {
        DEGRIEFED,
        DESTORYED,
        INTERACTED {
            @Override
            public String toString() {
                return "interacted with";
            }
        },
        USED;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    GriefAction(String name, char color, boolean deny, boolean silent, Type type) {
        this.blockName = name;
        this.alertColor = convertChar(color);
        this.denied = deny;
        this.stealth = silent;
        this.type = type;
    }

    GriefAction(String name, TextColor color, boolean deny, boolean silent, Type type) {
        this.blockName = name;
        this.alertColor = color;
        this.denied = deny;
        this.stealth = silent;
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

    public String getBlockName() {
        if (blockName == null) {
            if (this.block != null) {
                return this.block.getState().getType().getId();
            }
            return "minecraft:unknown";
        }
        return blockName;
    }

    public TextColor getAlertColor() {
        return alertColor;
    }

    public boolean isDenied() {
        return denied;
    }

    public boolean isStealth() {
        return stealth;
    }

    public Type getType() {
        return type;
    }

    public BlockSnapshot getBlock() {
        return block;
    }

    public ItemStackSnapshot getItem() {
        return item;
    }

    public Entity getEntity() {
        return entity;
    }

    public EntitySnapshot getGriefer() {
        return griefer;
    }

    public Vector3d getRotation() {
        return rotation;
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

    public World getWorld() {
        return block != null ? block.getLocation().get().getExtent() : entity.getLocation().getExtent();
    }

    public GriefAction copy() {
        return new GriefAction(blockName, alertColor, denied, stealth, type);
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
