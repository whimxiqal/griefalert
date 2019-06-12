package com.minecraftonline.griefalert;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
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
    private static HashMap<UUID, Pair<String, Integer>> lastAction = new HashMap<>();
    private GriefInstance[] griefLocations; // add 1 to replace 0
    private static int indexInTab = 1;
    private final GriefAlert plugin;
    private final Logger gaLogger;
    private final GriefLogger gLog;

    public AlertTracker(GriefAlert griefAlert) {
    	this.plugin = griefAlert;
    	griefLocations = new GriefInstance[plugin.getConfigInt("alertsCodeLimit") + 1];
        this.gaLogger = griefAlert.getLogger();
        this.gLog = new GriefLogger(griefAlert);
    }

    public GriefInstance get(int code) {
        return griefLocations[code];
    }

    public final void log(Player player, GriefInstance instance) {
        int alertNo = 0;
        if (instance.getType() != GriefAction.GriefType.DEGRIEFED) {
            alertNo = getAlertNo(instance);
        }
        UUID playerID = player.getUniqueId();
        Text alertMessage = alertMessage(player, alertNo, instance);
        String priorAct = actionTrackForm(instance);
        if (plugin.getConfigBoolean("debugInGameAlerts")) {
            alertStaff(alertMessage);
        } else if (!player.hasPermission("griefalert.noalert") && !instance.isStealthyAlert()) {
            if (!lastAction.containsKey(playerID) || !lastAction.get(playerID).getKey().equals(priorAct) || lastAction.get(playerID).getRight() >= plugin.getConfigInt("maxHiddenMatchingAlerts")) {
                alertStaff(alertMessage);
                lastAction.put(playerID, Pair.of(instance.getType().name().charAt(0) + instance.getBlockId(), 1));
            } else {
                int last = lastAction.get(playerID).getRight();
                lastAction.put(playerID, Pair.of(instance.getType().name().charAt(0) + instance.getBlockId(), last + 1));
            }
        }
        console(player, instance, alertNo);
        gLog.storeAction(player, instance);
    }

    public final void logSign(Player player, Sign sign, SignData signData) {
        if (!player.hasPermission("griefalert.noalert")) {
            String signmsg = "Sign placed by %s at %d %d %d in %s-%s";
            alertStaff(Text.builder(String.format(signmsg, player.getName(), sign.getLocation().getBlockX(), sign.getLocation().getBlockY(),
                                                  sign.getLocation().getBlockZ(), player.getWorld().getName(),
                                                  player.getWorld().getDimension().getType().getId().replace("minecraft:", ""))).color(TextColors.GRAY).build());
            for (int index = 0; index < 4; index++) {
                Text signText = signData.lines().get(index);
                if (!signText.isEmpty()) {
                    alertStaff(Text.builder("Line " + (index + 1) + ": ").append(signText).color(TextColors.GRAY).build());
                }
            }
        }
        gLog.storeSign(player, sign, signData);
    }

    private String actionTrackForm(GriefInstance instance) {
        return instance.getType().name().charAt(0) + instance.getBlockId();
    }

    private int getAlertNo(GriefInstance instance) {
        griefLocations[indexInTab] = instance;
        int returnCode = indexInTab;
        if (++indexInTab == griefLocations.length)
            indexInTab = 1; // skipping 0 cause it annoys me that the first alert is 0
        return returnCode;
    }

    public void alertStaff(Text message) {
        MessageChannel staffChannel = MessageChannel.permission("griefalert.staff");
        staffChannel.send(message);
    }

    private Text alertMessage(Player player, int alertNo, GriefInstance instance) {
        String msg = "%s %s %s (%d) in the %s.";
        return Text.builder(String.format(msg, player.getName(), instance.getType(),
                                          blockItemEntityStaff(instance), alertNo, instance.getWorld().getDimension().getType().getId().replaceAll("\\w+:", ""))).color(instance.getAlertColor()).build();
    }

    private void console(Player player, GriefInstance instance, int alertNo) {
        if (plugin.getConfigBoolean("showAlertsInConsole")) {
            gaLogger.info(
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
}
