package com.minecraftonline.griefalert.core;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.type.PermissionMessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.tools.CustomizableString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * This class has the purpose of handling all occurrences of Grief Instances.
 * Here, it will be determined if and how staff need to be notified, if and
 * how the instance should be logged, how to construct messaging about Grief
 * Instances, and so forth.
 */
public final class GriefManager {
	
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
    public GriefManager(final GriefAlert plugin) {
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
    	
    	// Was this a staff member degriefing grief?
        if (instance.getType() != GriefAction.GriefType.DEGRIEFED) {
        	// Log in array of recent grief instances
        	recordAlertInRecentGriefInstances(instance);
        } else {
        	// Do not put it into the recent grief instance structure
        	alertInGame = false;
        }
        
        // Print to console
        if (plugin.getConfigBoolean("showAlertsInConsole")) {
        	printToConsole(instance);
        }
        
        // Log the instance in the grief logger
        plugin.getGriefLogger().storeGriefInstance(instance);
        
        // Tell staff
        if (alertInGame) alert(instance);
    }
    
    /**
     * Takes the id of this specific grief instance and determines whether if staff
     * need to be notified in game. If they do, then print the message to staff.
     * @param instance The Grief Instance to which staff should be alerted
     */
    public void alert(GriefInstance instance) {
    	plugin.getDebugLogger().log("Handling the most recent instance of grief in game.");
        if (!instance.getGrieferAsPlayer().hasPermission("griefalert.noalert")) {
        	// Player does not have the permission to mute their own grief instance alerts
        	Text alertMessage = generateAlertMessage(instance);
        	UUID playerId = instance.getGrieferAsPlayer().getUniqueId();
        	Pair<GriefAction, Integer> previousGriefAction = lastGriefActionPlayerMap.get(playerId);
        	int consecutiveGriefActionCount;
        	if (previousGriefAction != null) plugin.getDebugLogger().log("This player's last grief action was: " + previousGriefAction.getKey().toString());
            if (previousGriefAction != null && 
            		previousGriefAction.getKey().equals(instance.getGriefAction()) &&
            		previousGriefAction.getValue() < plugin.getConfigInt("maxHiddenMatchingAlerts")) {
            	
            	// The same grief action was repeated by the same person
            	plugin.getDebugLogger().log("The same grief action was performed by the same player. No alert sent.");
    			consecutiveGriefActionCount = previousGriefAction.getValue() + 1;

            } else {
            	// This is the first consecutive grief action for this player
            	consecutiveGriefActionCount = 1;
            	plugin.getDebugLogger().log("Alerting staff in game: " + alertMessage.toPlain());
            	printToStaff(alertMessage);
            }
            // Place this action as the last grief action to read next time
            lastGriefActionPlayerMap.put(
					playerId, 
					Pair.of(
							instance.getGriefAction(), 
							consecutiveGriefActionCount));
            plugin.getDebugLogger().log("Player '" + instance.getGrieferAsPlayer().getName() + "' has had "
            		+ consecutiveGriefActionCount + " consecutive grief actions of the same type since the last alert.");
        } else {
        	plugin.getDebugLogger().log("This player has the permission 'griefalert.noalert', so no alert sent.");
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
            toPrint += new CustomizableString(plugin.getConfigNode("messaging").getNode("staff_alert_message_sign_header").getString())
            							.replacePlayer(player)
            							.replaceLocationCoordinates(Arrays.asList(
            															String.valueOf(sign.getLocation().getBlockX()), 
            															String.valueOf(sign.getLocation().getBlockY()),
            															String.valueOf(sign.getLocation().getBlockZ())))
                                        .replaceLocationDimension(player.getWorld().getDimension().getType())
                                        .complete();
            
            // Add all non-empty lines to message
            for (int index = 0; index < signData.lines().size(); index++) {
                Text signText = signData.lines().get(index);
                // Do not show empty lines
                if (!signText.isEmpty()) {
                	toPrint += "\n" + new CustomizableString(plugin.getConfigNode("messaging").getNode("staff_alert_message_sign_line").getString())
                								.replaceSignLineNumber(index + 1)
                								.replaceSignLineContent(signText.toPlain())
                								.complete();
                }
            }
            plugin.getDebugLogger().log("Printing sign log message to staff: " + toPrint);
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
    private void recordAlertInRecentGriefInstances(GriefInstance instance) {
        griefInstances.record(instance);
    }

    /**
     * Send a message directly to staff, usually about recent important grief.
     * @param message The message for staff to see
     */
    public void printToStaff(Text message) {
        MessageChannel staffChannel = new PermissionMessageChannel("griefalert.staff");	
        staffChannel.send(Sponge.getServer().getConsole(), message);
    }

    /**
     * A Text generation method to take information about a specific Grief Instance
     * and format it into a readable alert message.
     * @param instance The specific grief instance
     * @return The readable text format of the grief instance
     */
    public Text generateAlertMessage(GriefInstance instance) {
    	String defaultAlertMessage = getConfigStaffAlertMessage();
        instance.setAlertText(Text.builder(new CustomizableString(defaultAlertMessage)
        									.replacePlayer(instance.getGrieferAsPlayer()) 
        									.replaceGriefType(instance.getType())
        									.replaceGriefObject(instance.getGriefObjectAsString())
        									.replaceGriefID(instance.getAlertID())
        									.replaceLocationDimension(instance.getLocation().getExtent().getDimension().getType())
        									.complete())
        				.color(instance.getAlertColor())
        				.onClick(TextActions.runCommand("/gcheck " + instance.getAlertID()))
        				.onHover(TextActions.showText(Text.builder("Check Grief Alert #" + instance.getAlertID()).color(TextColors.LIGHT_PURPLE).build()))
        				.build());
        return instance.getAlertText();
    }
    
	public Text generateAlertMessage(List<GriefInstance> repeatedIncidents) {
		if (repeatedIncidents.isEmpty()) {
			return Text.of(TextColors.RED, "There are no recent grief alerts logged under that player.");
		}
		List<Text> interactiveGriefAlertIDs = new LinkedList<Text>();
		for (GriefInstance griefInstance : repeatedIncidents) {
			interactiveGriefAlertIDs.add(griefInstance.getInteractiveID());
		}
    	String defaultAlertMessage = getConfigStaffAlertMessage();
        return Text.builder(new CustomizableString(defaultAlertMessage)
        									.replacePlayer(repeatedIncidents.get(0).getGrieferAsPlayer()) 
        									.replaceGriefType(repeatedIncidents.get(0).getType())
        									.replaceGriefObject(repeatedIncidents.get(0).getGriefObjectAsString())
        									.replaceGriefID(0)
        									.replaceLocationDimension(repeatedIncidents.get(0).getLocation().getExtent().getDimension().getType())
        									.complete())
        				.color(repeatedIncidents.get(0).getAlertColor())
        				.append(Text.of(TextColors.RED, "\n["))
        				.append(Text.joinWith(Text.of(TextColors.RED, ", "), interactiveGriefAlertIDs))
        				.append(Text.of(TextColors.RED, "]"))
        				.build();
	}
	
	private String getConfigStaffAlertMessage() {
		String defaultAlertMessage;
    	try {
	    	defaultAlertMessage = (String) plugin.getConfigNode("messaging").getNode("staff_alert_message").getValue();
	    	if (defaultAlertMessage == null) throw new NullPointerException();
    	} catch (ClassCastException castEx) {
    		plugin.getLogger().warn("Messaging value for node 'staff_alert_message' not a string. Sending basic alert message.");
    		defaultAlertMessage = "Alert";
    	} catch (NullPointerException nullEx) {
    		plugin.getLogger().warn("Messaging value for node 'staff_alert_message' not found. Sending basic alert message.");
    		defaultAlertMessage = "Alert";
    	}
    	return defaultAlertMessage;
	}

    /**
     * Print a message directly to console about the grief instance
     * @param instance This specific grief instance to print
     */
    private void printToConsole(GriefInstance instance) {
        plugin.getLogger().info(
                instance.getGrieferAsPlayer().getUniqueId().toString() + " (" + instance.getGrieferAsPlayer().getName() + "):" +
                		instance.getType().name().toLowerCase() + ":" +
                        instance.getGriefObjectAsString() + ":" +
                        "x=" + instance.getLocation().getBlockX() + ":" +
                        "y=" + instance.getLocation().getBlockY() + ":" +
                        "z=" + instance.getLocation().getBlockZ() + ":" +
                        "sx=" + instance.getGrieferAsPlayer().getLocation().getBlockX() + ":" +
                        "sy=" + instance.getGrieferAsPlayer().getLocation().getBlockY() + ":" +
                        "sz=" + instance.getGrieferAsPlayer().getLocation().getBlockZ() + ":" +
                        "w=" + instance.getLocation().getExtent().getUniqueId() + ":" +
                        "d=" + instance.getLocation().getExtent().getDimension().getType().getId().replaceAll("\\w+:", "") + ":" +
                        instance.getAlertID());
    }
    
    public List<GriefInstance> getRecentGriefInstances() {
    	return griefInstances.toList();
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
    	
    	private int savedInstancesCount = 0;

    	/**
    	 * Constructor which creates the array to house all recent grief instances.
    	 * The size is dependent on the configured node loaded upon start-up
    	 * @param plugin The Main instance of the Plugin
    	 */
    	public RecentGriefInstanceManager(GriefAlert plugin) {
    		griefInstanceArray = new GriefInstance[plugin.getConfigInt("alertsCodeLimit")];
    	}

    	/**
    	 * Gives the recent grief instances in List form (time-sorted)
    	 * @return A time-sorted list of all recent grief instances
    	 */
    	public List<GriefInstance> toList() {
			List<GriefInstance> output = new LinkedList<GriefInstance>();
			for (int i = cursor; i < griefInstanceArray.length; i++) {
				if (griefInstanceArray[i] != null) {
					output.add(griefInstanceArray[i]);
				}
			}
			for (int i = 0; i < cursor; i++) {
				if (griefInstanceArray[i] != null) {
					output.add(griefInstanceArray[i]);
				}
			}
			
			return output;
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
    	public void record(GriefInstance instance) {
    		// Set the ID to the GriefInstance about to be replaced to -1, signaling that it no longer has an alertID
    		if (griefInstanceArray[cursor] != null) griefInstanceArray[cursor].setAlertID(-1);
    		griefInstanceArray[cursor] = instance;
    		int instanceAlertID = cursor + 1; // return "+ 1" so it indexes from 1 instead of 0
    		if (++cursor >= griefInstanceArray.length) {
    			cursor = 0;
    		}
    		if (++savedInstancesCount >= griefInstanceArray.length) {
    			savedInstancesCount = griefInstanceArray.length;
    		}
    		plugin.getDebugLogger().log("Instance added to the Recent Grief Instance Manager. "
    				+ "Id = " + instance.getAlertID() + 1 + ", instance: " + instance.toString());
    		instance.setAlertID(instanceAlertID);
    	}
    }
    
}