package com.minecraftonline.griefalert;

import org.slf4j.Logger;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.UUID;

public abstract class AlertTracker {
    private static HashMap<UUID, String> lastAction = new HashMap<>();
    private static Location[] griefLocations = new Location[GriefAlert.readConfigInt("alertsCodeLimit")];
    private static int indexInTab = 0;
    private static String msg = "%s %s %s %s (%d) in the '%s' dimension of the '%s' world.";
    private static String signmsg = "Sign placed by %s at %d %d %d in %s-%s";
    private final Logger gaLogger;

    public AlertTracker(Logger logger) {
        this.gaLogger = logger;
    }

    protected final void log(Player player, GriefAction action) {
        int alertNo = -1;
        if (action.type != GriefAction.Type.DEGRIEF) {
            alertNo = getAlertNo(player.getLocation());
        }
        UUID playerID = player.getUniqueId();
        Text alertMessage = alertMessage(player, alertNo, action);
        if ((!player.hasPermission("griefalert.noalert") && !action.stealth && action.type != GriefAction.Type.DEGRIEF) || GriefAlert.readConfigBool("debugInGameAlerts")) {
            String priorAct = actionTrackForm(action);
            if (!lastAction.containsKey(playerID) || !lastAction.get(playerID).contains(priorAct)) {
                alertStaff(alertMessage);
            }
            lastAction.put(playerID, action.type.name().charAt(0) + action.blockName);
        }
        console(player, action, alertNo);
        // TODO: Log Storage stuff
    }

    protected final void logSign(Player player, Sign sign, SignData signData) {
        if (!player.hasPermission("griefalert.noalert")) {
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

        // TODO: Log Storage Stuff
    }

    private String actionTrackForm(GriefAction action) {
        return action.type.name().charAt(0) + action.blockName;
    }

    private int getAlertNo(Location<World> worldLocation) {
        griefLocations[indexInTab] = worldLocation;
        int returnCode = indexInTab;
        if (++indexInTab == griefLocations.length)
            indexInTab = 0;
        return returnCode;
    }

    private void alertStaff(Text message) {
        MessageChannel staffChannel = MessageChannel.permission("griefalert.staff");
        staffChannel.send(message);
    }

    private Text alertMessage(Player player, int alertNo, GriefAction action) {
        return Text.builder(String.format(msg, player.getName(), action.type.name().toLowerCase(),
                                          correctGrammar(action.blockName), entityOrBlockStaff(action), alertNo, player.getWorld().getDimension().getType().getId().replaceAll("\\w+:", ""),
                                          player.getWorld().getName())).color(action.alertColor).build();
    }

    private String correctGrammar(String str) {
        return "aeiou".contains(str.substring(0, 1).toLowerCase()) ? "an" : "a";
    }

    private void console(Player player, GriefAction action, int alertNo) {
        gaLogger.info(
                player.getUniqueId().toString() + " (" + player.getName() + "):" +
                        action.type.name().toLowerCase() + ":" +
                        entityOrBlockConsole(action) + ":" +
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

    private String entityOrBlockStaff(GriefAction action) {
        if (action.block != null) {
            return action.block.getState().getId();
        }
        else if (action.entity instanceof Painting) {
            return "Painting (" + action.entity.get(Keys.ART).get().getId() + ")";
        }
        return "N/A";
    }

    private String entityOrBlockConsole(GriefAction action) {
        if (action.block != null) {
            return action.block.getState().toString().replace(':', '-');
        }
        else if (action.entity instanceof Painting) {
            Painting painting = (Painting) action.entity;
            return "Painting-" + painting.get(Keys.ART).get().getId();
        }
        return "N/A";
    }
}
