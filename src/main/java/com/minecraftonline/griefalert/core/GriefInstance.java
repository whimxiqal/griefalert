package com.minecraftonline.griefalert.core;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.World;

import com.minecraftonline.griefalert.core.GriefAction.GriefType;

/**
 * A class to represent any one candidate of an instance of grief in the game. Once a player triggers
 * a flagged action (GriefAction), a GriefInstance is generated with the specific GriefAction that was triggered.
 * The data here will be held locally in RealtimeGriefInstanceManager.java.
 * <p>
 * <i><b>Future Plans:</b> I actually don't love how this is done still. I notice that there seem to be various ways of saving the
 * griefed object, such as with an Entity or a Block. Ideally, however, I would want the GriefedObject to be 
 * a single 'Object' that can be called anywhere, but is then just cast the appropriate type. I think that would
 * bring in a lot of clarity and simplicity with this entire organization. Also, I think 'assign____' is a messy method.
 * It might bring simplicity to other classes to unify this method, because right now, it looks like the organization of which
 * Object to choose for logging is arbitrary (because not all of these objects exist in every GriefInstance).</i>
 * <p>
 * <i><b>To previous devs:</b> This used to be ImmutableGriefAction. I understood why it was done the way it was before, but it was very confusing and didn't
 * utilize subclasses properly, in my opinion. Instead of having this class be a subclass of a GriefAction that was immutable, and then
 * assign griefers and griefed objects to a up-cast GriefAction, I made the GriefAction the "immutable" version. That way, a Grief Action
 * can be saved upon loading of the plugin, and then when an instance of grief occurs, the player is saved, the griefd object is saved, and
 * the Grief Action that triggered the whole thing is saved in a new object: GriefInstance. This is the object that is referenced by the
 * logger and the RealtimeGriefInstanceManager (used to be AlertTracker).</i>
 */
public final class GriefInstance {

	/** GriefAction which triggered this GriefInstance. */
	private final GriefAction griefAction;
	/** The BlockSnapshot of the griefed block. */
    private BlockSnapshot block;
    /** The ItemStackSnapshot of the griefed item. */
    private ItemStackSnapshot item;
    /** The Entity of the griefed object. */
    private Entity entity;
    /** The EntitySnapshot of the griefer at the moment the grief occurred. */
    private EntitySnapshot grieferSnapshot;
    /** The Player representation of the griefer. */
    private Player griefer;
	
    /**
     * General constructor
     * @param griefAction The GriefAction which triggered this GriefInstance.
     */
    public GriefInstance(GriefAction griefAction) {
    	this.griefAction = griefAction;
    }

    public GriefInstance assignBlock(BlockSnapshot blockSnapshot) {
        this.block = blockSnapshot;
        return this;
    }

    public GriefInstance assignEntity(Entity entity) {
        this.entity = entity;
        return this;
    }

    public GriefInstance assignGriefer(Player griefer) {
        this.grieferSnapshot = griefer.createSnapshot();
        this.griefer = griefer;
        return this;
    }

    public GriefInstance assignItem(ItemStackSnapshot itemStackSnapshot) {
        this.item = itemStackSnapshot;
        return this;
    }
    
    /**
     * Gets the BlockId from the GriefAction saved in this GriefInstance.
     * @return The String representation of the GriefAction BlockId
     */
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

    public EntitySnapshot getGrieferSnapshot() {
        return grieferSnapshot;
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
		return griefer;
	}
	
	@Override
	public String toString() {
		return griefAction.toString() + ", griefer: " + griefer.getName();
	}
    
}
