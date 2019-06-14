package com.minecraftonline.griefalert;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.minecraftonline.griefalert.tools.General;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class has the purpose of handling all occurrences of Grief Instances.
 * Here, it will be determined if and how staff need to be notified, if and
 * how the instance should be logged, how to construct messaging about Grief
 * Instances, and so forth.
 */
public final class RealtimeGriefInstanceManager {

	/**
	 * The format for a grief instance alert for staff to read.
	 * The order of input is:
	 * Username of player
	 * Grief Action Type
	 * Object griefed
	 * ID for the Grief Instance which is triggering the alert
	 * The dimension of grief
	 */
	public final static String GRIEF_INSTANCE_ALERT_FORMAT = "%s %s a %s (%d) in the %s.";
	
	/**
	 * The format for the header of a grief alert of a sign for staff to read.
	 * The order of input is:
	 * Username of player
	 * X component of location
	 * Y component of location
	 * Z component of location
	 * The world name
	 * The dimension name
	 */
	public final static String EDITED_SIGN_HEADER_ALERT_FORMAT = "Sign placed by %s at %d %d %d in %s-%s";
	
	/**
	 * The format for each line of a grief alert of a sign for staff to read.
	 * The order of input is:
	 * Line number
	 * Line text
	 */
	public final static String EDITED_SIGN_LINE_ALERT_FORMAT = "Line %d: %s";
	
	/** 
	 * Houses all data about recent grief actions of each player.
	 * The player's UUID is held in the Map, which maps to a Pairing of their most recent
	 * GriefAction and the number of consecutive calls of this Grief Action.
	 * This is used to see if an alert message needs to be printed to staff for any
	 * given GriefInstance triggered by a player.
	 * */
    private HashMap<UUID, Pair<GriefAction, Integer>> lastGriefActionPlayerMap = new HashMap<>();
    /** An array of GriefInstances to house grief alerts to which staff can respond. */
    private final RecentGriefInstanceManager griefInstances;
    /** The main plugin. */
    private final GriefAlert plugin;

    /**
     * This constructor saves the plugin and then generates
     * @param plugin
     */
    public RealtimeGriefInstanceManager(final GriefAlert plugin) {
    	this.plugin = plugin;
    	griefInstances = new RecentGriefInstanceManager(plugin);
    }

    /**
     * Gets the grief instance corresponding to the code.
     * @param code The id of queried Grief Instance
     * @return The appropriate Grief Instance
     */
    public GriefInstance get(final int code) {
        return griefInstances.get(code);
    }

    /**
     * Handles the grief instance by logging it in the Grief Logger, printing to the
     * console, notifying staff, and 
     * @param player
     * @param instance
     */
    public void processGriefInstance(final GriefInstance instance) {
    	boolean alertInGame = !instance.isStealthyAlert(); // Used to determine if this instance requires in-game alert
    	int alertId;
    	
    	// Was this a staff member degriefing grief?
        if (instance.getType() != GriefAction.GriefType.DEGRIEFED) {
        	// Log in array of recent grief instances
        	alertId = recordAlertInRecentGriefInstances(instance);
        } else {
        	// Do not put it into the recent grief instance structure
        	alertId = -1;
        	alertInGame = false;
        }
        
        // Print to console
        if (plugin.getConfigBoolean("showAlertsInConsole")) {
        	printToConsole(alertId, instance);
        }
        
        // Log the instance in the grief logger
        plugin.getGriefLogger().storeGriefInstance(instance);
        
        // Tell staff
        if (plugin.getConfigBoolean("debugInGameAlerts")) {
        	// Just alert the staff and don't do anything else.
        	// This is what it did before as well, but I'm not sure if this is the
        	// best way to debug
            printToStaff(generateAlertMessage(alertId, instance));
            return;
        }
        if (alertInGame) alert(alertId, instance);
    }
    
    /**
     * Takes the id of this specific grief instance and determines whether if staff
     * need to be notified in game. If they do, then print the message to staff.
     * @param alertId The ID of the grief instance
     * @param instance The Grief Instance to which staff should be alerted
     */
    public void alert(int alertId, GriefInstance instance) {
    	Text alertMessage = generateAlertMessage(alertId, instance);
        if (!instance.getGrieferAsPlayer().hasPermission("griefalert.noalert")) {
        	// Player does not have the permission to mute their own grief instance alerts
        	
        	UUID playerId = instance.getGrieferAsPlayer().getUniqueId();
        	Pair<GriefAction, Integer> previousGriefAction = lastGriefActionPlayerMap.get(playerId);
        	int consecutiveGriefActionCount;
            if (previousGriefAction != null && 
            		previousGriefAction.getKey().equals(instance.getGriefAction()) &&
            		previousGriefAction.getValue() < plugin.getConfigInt("maxHiddenMatchingAlerts")) {
            	
            	// The same grief action was repeated by the same person
    			consecutiveGriefActionCount = previousGriefAction.getValue() + 1;

            } else {
            	// This is the first consecutive grief action for this player
            	consecutiveGriefActionCount = 1;
            	printToStaff(alertMessage);
            }
            // Place this action as the last grief action to read next time
            lastGriefActionPlayerMap.put(
					playerId, 
					Pair.of(
							instance.getGriefAction(), 
							consecutiveGriefActionCount));
        } else {
        	// Right now, nothing happens if the griefer *does* have the "griefalert.noalert" node.
        }
    }

    /**
     * Takes information about a player and a sign they just edited and
     * determines whether staff need to be notified
     * @param player The player who edited a sign
     * @param sign The sign object corresponding to the edited sign in game
     * @param signData The data of the sign corresponding to the edited sign in game
     */
    public void alert(Player player, Sign sign, SignData signData) {
        if (!player.hasPermission("griefalert.noalert")) {
        	String toPrint = "";
        	
        	// Add header to message
            toPrint += String.format(EDITED_SIGN_HEADER_ALERT_FORMAT, player.getName(), sign.getLocation().getBlockX(), sign.getLocation().getBlockY(),
                                                  sign.getLocation().getBlockZ(), player.getWorld().getName(),
                                                  player.getWorld().getDimension().getType().getId().replace("minecraft:", ""));
            
            // Add all non-empty lines to message
            for (int index = 0; index < signData.lines().size(); index++) {
                Text signText = signData.lines().get(index);
                // Do not show empty lines
                if (!signText.isEmpty()) {
                	toPrint += String.format(EDITED_SIGN_LINE_ALERT_FORMAT, index, signText);
                }
            }
            printToStaff(Text.builder(toPrint).color(TextColors.GRAY).build());
        } else {
        	// Right now, nothing happens if the griefer *does* have the "griefalert.noalert" node.
        }
        plugin.getGriefLogger().storeSignEdit(player, sign, signData);
    }

    /**
     * Put this instance into an array to be accessed by staff in game
     * @param instance The Instance of Grief to log
     * @return The ID of the instance to be used by staff in game
     */
    private int recordAlertInRecentGriefInstances(GriefInstance instance) {
        return griefInstances.record(instance);
    }

    /**
     * Send a message directly to staff, usually about recent important grief.
     * @param message The message for staff to see
     */
    public void printToStaff(Text message) {
        MessageChannel staffChannel = MessageChannel.permission("griefalert.staff");
        staffChannel.send(message);
    }

    /**
     * A Text generation method to take information about a specific Grief Instance
     * and format it into a readable alert message.
     * @param alertId The ID of the specific grief instance
     * @param instance The specific grief instance
     * @return The readable text format of the grief instance
     */
    private Text generateAlertMessage(int alertId, GriefInstance instance) {
        return Text.builder(General.correctIndefiniteArticles(
        							String.format(GRIEF_INSTANCE_ALERT_FORMAT, 
        									instance.getGrieferAsPlayer().getName(), 
        									instance.getType(),   
        									griefedObjectForStaffToString(instance), 
        									alertId, 
        									instance.getWorld().getDimension().getType().getId().replaceAll("\\w+:", "")))).
        				color(instance.getAlertColor()).
        				build();
    }

    /**
     * Print a message directly to console about the grief instance
     * @param instance This specific grief instance to print
     * @param alertId The ID of this specific grief instance
     */
    private void printToConsole(int alertId, GriefInstance instance) {
        plugin.getLogger().info(
                instance.getGrieferAsPlayer().getUniqueId().toString() + " (" + instance.getGrieferAsPlayer().getName() + "):" +
                		instance.getType().name().toLowerCase() + ":" +
                        griefedObjectForConsoleToString(instance) + ":" +
                        "x=" + instance.getX() + ":" +
                        "y=" + instance.getY() + ":" +
                        "z=" + instance.getZ() + ":" +
                        "sx=" + instance.getGrieferAsPlayer().getLocation().getBlockX() + ":" +
                        "sy=" + instance.getGrieferAsPlayer().getLocation().getBlockY() + ":" +
                        "sz=" + instance.getGrieferAsPlayer().getLocation().getBlockZ() + ":" +
                        "w=" + instance.getWorld().getUniqueId() + ":" +
                        "d=" + instance.getWorld().getDimension().getType().getId().replaceAll("\\w+:", "") + ":" +
                        alertId);
    }

    /**
     * Returns the string representation of the griefed object for an in-game
     * audience.
     * Order of preference for printing the griefed object:
     * <li>Block Type (checks Type first as a workaround to identify color and BlockState?).
     * <li>The Item.
     * <li>A Painting
     * <li>Item Frame if it has something else in it
     * <li>Entity (which now can't be a Painting or a filled ItemFrame, so it could be, for example, an empty item frame)
     * <li>Block ID saved in the Grief Action established in configuration text file (guaranteed to exist!)
     * @param instance The grief instance housing the griefed object data
     * @return The String reprentation of the griefed object
     */
    private String griefedObjectForStaffToString(GriefInstance instance) {
        if (instance.getBlock() != null) {
            if (instance.getBlock().getState().getType().getItem().isPresent()) {
                // Work around for BlockType not seeing colored blocks properly and BlockState not being translatable
                return ItemStack.builder().fromBlockSnapshot(instance.getBlock()).build().getTranslation().get();
            }
            // The few blocks that have no ItemType connected (such as Fire)
            return instance.getBlock().getState().getType().getTranslation().get();
        }
        else if (instance.getItem() != null) {
            return instance.getItem().getTranslation().get();
        }
        else if (instance.getEntity() instanceof Painting) {
            return "a Painting of " + instance.getEntity().get(Keys.ART).get().getName();
        }
        else if (instance.getEntity() instanceof ItemFrame && instance.getEntity().get(Keys.REPRESENTED_ITEM).isPresent()) {
            return instance.getEntity().get(Keys.REPRESENTED_ITEM).get().getTranslation().get() + " in an Item Frame";
        }
        else if (instance.getEntity() != null) {
            return instance.getEntity().getType().getTranslation().get();
        }
        return instance.getBlockId();
    }

    /**
     * Returns the string representation of the griefed object for printing to the console
     * Order of preference for printing the griefed object:
     * <li>Block Type
     * <li>The Item
     * <li>A Painting
     * <li>Item Frame if it has something else in it
     * <li>Entity (which now can't be a Painting or a filled ItemFrame, so it could be, for example, an empty item frame)
     * <li>Block ID saved in the Grief Action established in configuration text file (guaranteed to exist!)
     * @param instance The grief instance housing the griefed object data
     * @return The String reprentation of the griefed object
     */
    private String griefedObjectForConsoleToString(GriefInstance instance) {
        if (instance.getBlock() != null) {
            return instance.getBlock().getState().toString().replace(':', '-');
        }
        else if (instance.getItem() != null) {
            return instance.getItem().toString().replace(':', '-');
        }
        else if (instance.getEntity() instanceof Painting) {
            return "minecraft-painting[art=" + instance.getEntity().get(Keys.ART).get().getId() + "]";
        }
        else if (instance.getEntity() instanceof ItemFrame && instance.getEntity().get(Keys.REPRESENTED_ITEM).isPresent()) {
            return "minecraft-item_frame[item_id=" + instance.getEntity().get(Keys.REPRESENTED_ITEM).get().getTranslation().get() + "]";
        }
        else if (instance.getEntity() != null) {
            return instance.getEntity().getType().getId().replace(':', '-');
        }
        return instance.getBlockId().replace(':', '-');
    }
    
    /**
     * A local class for managing the recent grief instances which are handled by in-game staff.
     * It contains an array and a cursor which marks where the next instance of grief will
     * be contained.
     *
     */
    private class RecentGriefInstanceManager {
    	
    	/** Array to house all recent Grief Instances. */
    	private GriefInstance[] griefInstanceArray;
    	/** The cursor which marks to place the next grief instance. */
    	private int cursor = 0;

    	/**
    	 * Constructor which creates the array to house all recent grief instances.
    	 * The size is dependent on the configured node loaded upon start-up
    	 * @param plugin The Main instance of the Plugin
    	 */
    	public RecentGriefInstanceManager(GriefAlert plugin) {
    		griefInstanceArray = new GriefInstance[plugin.getConfigInt("alertsCodeLimit")];
    	}

    	/**
    	 * Retrieves a specific Grief Instance from the storage array. <b>Indexes from 1</b>
    	 * @param code Code, starting at 1
    	 * @return The GriefInstance at that index
    	 */
    	public GriefInstance get(int code) {
    		return griefInstanceArray[code - 1];
    	}

    	/**
    	 * Adds a GriefInstance into the array.
    	 * Returns the index of the instance put in and moves the cursor forward.
    	 * @param instance
    	 * @return
    	 */
    	public int record(GriefInstance instance) {
    		griefInstanceArray[cursor] = instance;
    		int instanceIndex = cursor;
    		if (++cursor >= griefInstanceArray.length) {
    			cursor = 0;
    		}
    		return instanceIndex + 1;
    	}
    }
}
