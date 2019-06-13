package com.minecraftonline.griefalert;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;
import com.minecraftonline.griefalert.GriefAction.GriefType;
//TODO: PietElite: Fix
/* This is so i don't accidentally assign something to the watch list objects */
public final class GriefInstance {

	private GriefAction griefAction;
    private BlockSnapshot block;
    private ItemStackSnapshot item;
    private Entity entity;
    private EntitySnapshot griefer;
    private Player player;
    private Vector3d rotation;
	
    public GriefInstance(GriefAction griefAction) {
    	this.setGriefAction(griefAction);
    }

    public GriefInstance assignBlock(BlockSnapshot blockSnapshot) {
        this.block = blockSnapshot;
        return this;
    }

    public GriefInstance assignEntity(Entity entity) {
        this.entity = entity;
        return this;
    }

    public GriefInstance assignGriefer(Player player) {
        this.griefer = player.createSnapshot();
        this.rotation = player.getRotation();
        this.player = player;
        return this;
    }

    public GriefInstance assignItem(ItemStackSnapshot itemStackSnapshot) {
        this.item = itemStackSnapshot;
        return this;
    }
    
    public String getBlockId() {
        if (griefAction.getBlockId() == null) {
            if (this.block != null) {
                return this.block.getState().getType().getId();
            }
            return "minecraft:unknown";
        }
        return griefAction.getBlockId();
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

	public GriefAction getGriefAction() {
		return griefAction;
	}

	public void setGriefAction(GriefAction griefAction) {
		this.griefAction = griefAction;
	}

	public GriefType getType() {
		return griefAction.getType();
	}

	public boolean isDenied() {
		return griefAction.isDenied();
	}

	public boolean isStealthyAlert() {
		return griefAction.isStealthyAlert();
	}

	public TextColor getAlertColor() {
		return griefAction.getAlertColor();
	}
	
	public Player getGrieferAsPlayer() {
		return player;
	}
    
}
