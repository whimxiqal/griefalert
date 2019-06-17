package com.minecraftonline.griefalert.core;

import java.util.NoSuchElementException;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.Location;
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
    /** The EntitySnapshot of the griefer at the moment the grief occurred. */
    private EntitySnapshot grieferSnapshot;
    /** The Player representation of the griefer. */
    private Player griefer;
    /** The general Object for holding the grief related object in this Grief Instance. */
    private GriefTriggerObject griefObject;
	private Text alertText;
    /**
     * General constructor
     * @param griefAction The GriefAction which triggered this GriefInstance.
     */
    public GriefInstance(GriefAction griefAction, BlockSnapshot blockSnapshot, Player griefer) {
    	this.griefAction = griefAction;
    	this.griefObject = new GriefTriggerObject(blockSnapshot);
    	this.grieferSnapshot = griefer.createSnapshot();
        this.griefer = griefer;
    }

    public GriefInstance(GriefAction griefAction, Entity entity, Player griefer) {
    	this.griefAction = griefAction;
    	this.griefObject = new GriefTriggerObject(entity);
    	this.grieferSnapshot = griefer.createSnapshot();
        this.griefer = griefer;
    }

    public GriefInstance(GriefAction griefAction, ItemStackSnapshot itemStackSnapshot, Player griefer) {
    	this.griefAction = griefAction;
    	this.griefObject = new GriefTriggerObject(itemStackSnapshot);
    	this.grieferSnapshot = griefer.createSnapshot();
        this.griefer = griefer;
    }

    
    /**
     * Gets the BlockId from the GriefAction saved in this GriefInstance.
     * @return The String representation of the GriefAction BlockId
     */
    public String getBlockId() {
        return griefAction.getBlockId();
    }
    
    public Location<World> getLocation() {
    	return griefObject.getLocation();
    }

    public EntitySnapshot getGrieferSnapshot() {
        return grieferSnapshot;
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
	
	public String getGriefObjectAsString() {
		return griefObject.toString();
	}
	
	public DataContainer getGriefObjectToContainer() {
		return griefObject.toContainer();
	}
	
	@Override
	public String toString() {
		return griefAction.toString() + ", griefer: " + griefer.getName();
	}
	
	public Text getAlertText() {
		return alertText;
	}

	public void setAlertText(Text alertText) {
		this.alertText = alertText;
	}

	private static class GriefTriggerObject {
		
		private BlockSnapshot blockSnapshot;
		
		private ItemStackSnapshot itemStackSnapshot;
		
		private Entity entity;
		
		private final GriefTriggerType type;
		
		private enum GriefTriggerType {
			BLOCK,
			ITEMSTACK,
			ENTITY;
		}
		
		public GriefTriggerObject(BlockSnapshot blockSnapshot) {
			this.blockSnapshot = blockSnapshot;
			this.type = GriefTriggerType.BLOCK;
		}
		
		public GriefTriggerObject(ItemStackSnapshot itemStackSnapshot) {
			this.itemStackSnapshot = itemStackSnapshot;
			this.type = GriefTriggerType.ITEMSTACK;
		}
		
		public GriefTriggerObject(Entity entity) {
			this.entity = entity;
			this.type = GriefTriggerType.ENTITY;
		}
		
		public Location<World> getLocation() {
			try {
				switch (type) {
				case BLOCK:
					return blockSnapshot.getLocation().get();
				case ENTITY:
					return entity.getLocation();
				default:
					return null;
				}
			} catch (NullPointerException nullPointer) {
				nullPointer.printStackTrace();
			} catch (NoSuchElementException noElement) {
				noElement.printStackTrace();
			}
			return null;
		}
		
		public DataContainer toContainer() {
			switch (type) {
			case BLOCK:
				return blockSnapshot.toContainer();
			case ENTITY:
				return entity.toContainer();
			case ITEMSTACK:
				return itemStackSnapshot.toContainer();
			default:
				return null;
			}
		}
		
		@Override
		public String toString() {
			try {
				switch (type) {
				case BLOCK:
					return blockSnapshot.getState().getType().getTranslation().get();
				case ENTITY:
					// TODO Painting implementation
					// TODO ItemFrame implementation
					return entity.getType().getTranslation().get();
				case ITEMSTACK:
					return itemStackSnapshot.getType().getTranslation().get();
				}
			} catch (NullPointerException nullPointer) {
				nullPointer.printStackTrace();
			} catch (NoSuchElementException noElement) {
				noElement.printStackTrace();
			}
			return null;
		}
		
	}
    
}
