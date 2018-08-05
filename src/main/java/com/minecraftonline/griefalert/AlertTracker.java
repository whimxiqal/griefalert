package com.minecraftonline.griefalert;

import org.slf4j.Logger;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.hanging.LeashHitch;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import java.util.HashMap;
import java.util.UUID;

public final class AlertTracker {
    private static HashMap<UUID, String> lastAction = new HashMap<>();
    private static GriefAction[] griefLocations = new GriefAction[GriefAlert.readConfigInt("alertsCodeLimit") + 1]; // add 1 to replace 0
    private static int indexInTab = 1;
    private final Logger gaLogger;
    private final GriefLogger gLog;

    public AlertTracker(Logger logger) {
        this.gaLogger = logger;
        this.gLog = new GriefLogger(logger);
    }

    public GriefAction get(int code) {
        return griefLocations[code];
    }

    public final void log(Player player, GriefAction action) {
        int alertNo = 0;
        if (action.type != GriefAction.Type.DEGRIEFED) {
            alertNo = getAlertNo(action);
        }
        UUID playerID = player.getUniqueId();
        Text alertMessage = alertMessage(player, alertNo, action);
        if ((!player.hasPermission("griefalert.noalert") && !action.stealth && action.type != GriefAction.Type.DEGRIEFED) || GriefAlert.readConfigBool("debugInGameAlerts")) {
            String priorAct = actionTrackForm(action);
            if ((!lastAction.containsKey(playerID) || !lastAction.get(playerID).contains(priorAct)) || GriefAlert.readConfigBool("debugInGameAlerts")) {
                alertStaff(alertMessage);
            }
            lastAction.put(playerID, action.type.name().charAt(0) + action.blockName);
        }
        console(player, action, alertNo);
        //gLog.storeAction(player, action);
    }

    public final void logSign(Player player, Sign sign, SignData signData) {
        if (!player.hasPermission("griefalert.noalert")) {
            String signmsg = "Sign placed by %s at %d %d %d in %s-%s";
            alertStaff(Text.builder(String.format(signmsg, player.getName(), sign.getLocation().getBlockX(), sign.getLocation().getBlockY(),
                    sign.getLocation().getBlockZ(), player.getWorld().getName(),
                    player.getWorld().getDimension().getType().getId().replace("minecraft:", ""))).build());
            for (int index = 0; index < 4; index++) {
                Text signText = signData.lines().get(index);
                if (!signText.isEmpty()) {
                    alertStaff(Text.builder("Line " + (index + 1) + ": ").append(signText).build());
                }
            }
        }
        //gLog.storeSign(player, sign, signData);
    }

    private String actionTrackForm(GriefAction action) {
        return action.type.name().charAt(0) + action.blockName;
    }

    private int getAlertNo(GriefAction action) {
        griefLocations[indexInTab] = action;
        int returnCode = indexInTab;
        if (++indexInTab == griefLocations.length)
            indexInTab = 1; // skipping 0 cause it annoys me that the first alert is 0
        return returnCode;
    }

    public void alertStaff(Text message) {
        MessageChannel staffChannel = MessageChannel.permission("griefalert.staff");
        staffChannel.send(message);
    }

    private Text alertMessage(Player player, int alertNo, GriefAction action) {
        String msg = "%s %s %s (%d) in the %s of %s.";
        return Text.builder(String.format(msg, player.getName(), action.type.name().toLowerCase(),
                blockItemEntityStaff(action), alertNo, player.getWorld().getDimension().getType().getId().replaceAll("\\w+:", ""),
                player.getWorld().getName())).color(action.alertColor).build();
    }

    private void console(Player player, GriefAction action, int alertNo) {
        gaLogger.info(
                player.getUniqueId().toString() + " (" + player.getName() + "):" +
                        action.type.name().toLowerCase() + ":" +
                        blockItemEntityConsole(action) + ":" +
                        "x=" + action.getX() + ":" +
                        "y=" + action.getY() + ":" +
                        "z=" + action.getZ() + ":" +
                        "sx=" + player.getLocation().getBlockX() + ":" +
                        "sy=" + player.getLocation().getBlockY() + ":" +
                        "sz=" + player.getLocation().getBlockZ() + ":" +
                        "w=" + player.getWorld().getName() + ":" +
                        "d=" + player.getWorld().getDimension().getType().getId().replaceAll("\\w+:", "") + ":" +
                        alertNo
        );
    }

    private String correctGrammar(String str) {
        return "aeiou".contains(str.substring(0, 1).toLowerCase()) ? "an " + str : "a " + str;
    }

    private String blockItemEntityStaff(GriefAction action) {
        if (action.block != null) {
            return correctGrammar(action.block.getState().getType().getTranslation().get());
        } else if (action.item != null) {
            return correctGrammar(action.item.getTranslation().get());
        }
        else if (action.entity instanceof Painting) {
            return "a Painting of " + action.entity.get(Keys.ART).get().getId();
        } else if (action.entity instanceof ItemFrame) {
            if (action.entity.get(Keys.REPRESENTED_ITEM).isPresent()) {
                return correctGrammar(action.entity.get(Keys.REPRESENTED_ITEM).get().getTranslation().get() + " in an Item Frame");
            } else {
                return "an Item Frame";
            }
        } else if (action.entity instanceof ArmorStand) {
            if (action.type == GriefAction.Type.DESTORYED) {
                return "an Armor Stand";
            }
        }
        return action.blockName.replace(':', '-');
    }

    private String blockItemEntityConsole(GriefAction action) {
        if (action.block != null) {
            return action.block.getState().toString().replace(':', '-');
        } else if (action.item != null) {
            return action.item.toString().replace(':', '-');
        } else if (action.entity instanceof LeashHitch) {
            return "minecraft-leash_knot";
        } else if (action.entity instanceof Painting) {
            return action.entity.get(Keys.ART).get().getId() + " painting";
        } else if (action.entity instanceof ItemFrame) {
            if (action.entity.get(Keys.REPRESENTED_ITEM).isPresent()) {
                return "framed " + action.entity.get(Keys.REPRESENTED_ITEM).get().getTranslation().get();
            } else {
                return "minecraft-item_frame";
            }
        } else if (action.entity instanceof ArmorStand) {
            if (action.type == GriefAction.Type.DESTORYED) {
                return "minecraft-armor_stand";
            }
        }
        return action.blockName.replace(':', '-');
    }
}
