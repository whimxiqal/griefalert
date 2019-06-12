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

import java.util.HashMap;
import java.util.UUID;

public final class AlertTracker {
    private HashMap<UUID, Pair<GriefAction, Integer>> lastGriefActionPlayerMap = new HashMap<>();
    private RecentGriefInstanceManager griefInstances;
    private final GriefAlert plugin;

    public AlertTracker(GriefAlert griefAlert) {
    	this.plugin = griefAlert;
    	griefInstances = new RecentGriefInstanceManager(plugin);
    }

    public GriefInstance get(int code) {
        return griefInstances.get(code);
    }

    public final void log(Player player, GriefInstance instance) {
        int alertId;
        
        // Log in array of recent grief instances
        if (instance.getType() != GriefAction.GriefType.DEGRIEFED) {
        	alertId = recordAlertInRecentGriefInstances(instance);
        } else {
        	// Do not put it into the recent grief instance structure
        	alertId = -1;
        }
        
        // Print to console
        if (plugin.getConfigBoolean("showAlertsInConsole")) {
        	printToConsole(player, instance, alertId);
        }
        
        // Log the instance in the grief logger
        plugin.getGriefLogger().storeAction(player, instance);
        
        // Tell staff
        if (plugin.getConfigBoolean("debugInGameAlerts")) {
        	// Just alert the staff and don't do anything else
            printToStaff(alertMessage(player, alertId, instance));
        } else {
        	alertInGame(player, alertId, instance);
        }
    }
    
    public final void alertInGame(Player player, int alertId, GriefInstance instance) {
    	Text alertMessage = alertMessage(player, alertId, instance);
        if (!player.hasPermission("griefalert.noalert")) {
        	// Player does not have the permission to mute their own grief instance alerts
        	if (!instance.isStealthyAlert()) {
        		// The Grief Instance is not in stealth mode
        		
	        	UUID playerId = player.getUniqueId();
	            
	        	Pair<GriefAction, Integer> previousGriefAction = lastGriefActionPlayerMap.get(playerId);
	        	int consecutiveGriefActionCount;
	            if (previousGriefAction != null && 
	            		previousGriefAction.getKey().equals(instance.getGriefAction()) &&
	            		previousGriefAction.getValue() < plugin.getConfigInt("maxHiddenMatchingAlerts")) {
	            	
	            	// The same grief action was repeated by the same person
        			consecutiveGriefActionCount = previousGriefAction.getValue() + 1;

	            } else {
	            	// This is the first consecutive grief action count
	            	consecutiveGriefActionCount = 1;
	            	printToStaff(alertMessage);
	            }
	            lastGriefActionPlayerMap.put(
    					playerId, 
    					Pair.of(
    							instance.getGriefAction(), 
    							consecutiveGriefActionCount));
        	}
        } else {
        	// Right now, nothing happens if a player *does* have the "griefalert.noalert" node.
        }
    }

    public final void logSign(Player player, Sign sign, SignData signData) {
        if (!player.hasPermission("griefalert.noalert")) {
            String signmsg = "Sign placed by %s at %d %d %d in %s-%s";
            printToStaff(Text.builder(String.format(signmsg, player.getName(), sign.getLocation().getBlockX(), sign.getLocation().getBlockY(),
                                                  sign.getLocation().getBlockZ(), player.getWorld().getName(),
                                                  player.getWorld().getDimension().getType().getId().replace("minecraft:", ""))).color(TextColors.GRAY).build());
            for (int index = 0; index < 4; index++) {
                Text signText = signData.lines().get(index);
                if (!signText.isEmpty()) {
                	printToStaff(Text.builder("Line " + (index + 1) + ": ").append(signText).color(TextColors.GRAY).build());
                }
            }
        }
        plugin.getGriefLogger().storeSign(player, sign, signData);
    }

    private int recordAlertInRecentGriefInstances(GriefInstance instance) {
        return griefInstances.record(instance);
    }

    public void printToStaff(Text message) {
        MessageChannel staffChannel = MessageChannel.permission("griefalert.staff");
        staffChannel.send(message);
    }

    private Text alertMessage(Player player, int alertNo, GriefInstance instance) {
        String msg = "%s %s %s (%d) in the %s.";
        return Text.builder(String.format(msg, player.getName(), instance.getType(),
                                          blockItemEntityStaff(instance), alertNo, instance.getWorld().getDimension().getType().getId().replaceAll("\\w+:", ""))).color(instance.getAlertColor()).build();
    }

    private void printToConsole(Player player, GriefInstance instance, int alertNo) {
        plugin.getLogger().info(
                player.getUniqueId().toString() + " (" + player.getName() + "):" +
                		instance.getType().name().toLowerCase() + ":" +
                        blockItemEntityConsole(instance) + ":" +
                        "x=" + instance.getX() + ":" +
                        "y=" + instance.getY() + ":" +
                        "z=" + instance.getZ() + ":" +
                        "sx=" + player.getLocation().getBlockX() + ":" +
                        "sy=" + player.getLocation().getBlockY() + ":" +
                        "sz=" + player.getLocation().getBlockZ() + ":" +
                        "w=" + instance.getWorld().getUniqueId() + ":" +
                        "d=" + instance.getWorld().getDimension().getType().getId().replaceAll("\\w+:", "") + ":" +
                        alertNo
        );
    }

    private String correctGrammar(String str) {
        return "aeiou".contains(str.substring(0, 1).toLowerCase()) ? "an " + str : "a " + str;
    }

    private String blockItemEntityStaff(GriefInstance instance) {
        if (instance.getBlock() != null) {
            if (instance.getBlock().getState().getType().getItem().isPresent()) {
                // Work around for BlockType not seeing colored blocks properly and BlockState not being translatable
                return correctGrammar(ItemStack.builder().fromBlockSnapshot(instance.getBlock()).build().getTranslation().get());
            }
            // The few blocks that have no ItemType connected (such as Fire)
            return correctGrammar(instance.getBlock().getState().getType().getTranslation().get());
        }
        else if (instance.getItem() != null) {
            return correctGrammar(instance.getItem().getTranslation().get());
        }
        else if (instance.getEntity() instanceof Painting) {
            return "a Painting of " + instance.getEntity().get(Keys.ART).get().getName();
        }
        else if (instance.getEntity() instanceof ItemFrame && instance.getEntity().get(Keys.REPRESENTED_ITEM).isPresent()) {
            return correctGrammar(instance.getEntity().get(Keys.REPRESENTED_ITEM).get().getTranslation().get() + " in an Item Frame");
        }
        else if (instance.getEntity() != null) {
            return correctGrammar(instance.getEntity().getType().getTranslation().get());
        }
        return instance.getBlockId();
    }

    private String blockItemEntityConsole(GriefInstance instance) {
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
    
    public class RecentGriefInstanceManager {
    	
    	private GriefInstance[] griefInstanceArray;
    	private int cursor = 0;

    	public RecentGriefInstanceManager(GriefAlert plugin) {
    		griefInstanceArray = new GriefInstance[plugin.getConfigInt("alertsCodeLimit")];
    	}

    	public GriefInstance get(int code) {
    		return griefInstanceArray[code];
    	}

    	/**
    	 * Adds a GriefInstance into the array.
    	 * Returns the index of the instance put in.
    	 * Moves the cursor forward.
    	 * @param instance
    	 * @return
    	 */
    	public int record(GriefInstance instance) {
    		griefInstanceArray[cursor] = instance;
    		int instanceIndex = cursor;
    		if (++cursor >= griefInstanceArray.length) {
    			cursor = 0;
    		}
    		return instanceIndex;
    	}
    }
    
}
