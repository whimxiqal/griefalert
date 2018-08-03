package com.minecraftonline.griefalert;

import com.google.inject.Inject;
import com.minecraftonline.griefalert.listeners.*;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;

import static com.minecraftonline.griefalert.GriefAlert.VERSION;

@Plugin(id = "griefalert",
        name = "GriefAlert",
        version = VERSION,
        description = "Grief alert tool")
public class GriefAlert {
    static final String VERSION = "21.0";

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;
    private static ConfigurationNode rootNode;

    private static HashMap<String, GriefAction> useWatchList = new HashMap<>();
    private static HashMap<String, GriefAction> interactWatchList = new HashMap<>();
    private static HashMap<String, GriefAction> destroyWatchList = new HashMap<>();

    @Listener
    public void initialize(GamePreInitializationEvent event) {
        ConfigurationLoader<CommentedConfigurationNode> loader =
                HoconConfigurationLoader.builder().setPath(defaultConfig).build();

        if (!defaultConfig.toFile().exists()) {
            try {
                rootNode = loader.load();
                rootNode.getNode("degriefStickID").setValue("\"minecraft:stick\"");
                rootNode.getNode("alertsCodeLimit").setValue(999);
                rootNode.getNode("logSignsContent").setValue(true);
                rootNode.getNode("displayPlacedSigns").setValue(true);
                rootNode.getNode("debugInGameAlerts").setValue(false);
                loader.save(rootNode);
                /*
                writer.write("degriefStickID=280\r\n");
            	writer.write("separatedLog=false\r\n");
            	writer.write("oldWarnBehavior=false\r\n");
            	writer.write("gcheckToCoordinates=true\r\n");
            	writer.write("alertsCodeLimit=30\r\n");
            	writer.write("logSignsContent=true\r\n");
            	writer.write("displayPlacedSigns=true\r\n");
            	writer.write("logToFile=true\r\n");
            	writer.write("# SQL configuration # \r\n");
            	writer.write("logToSQL=false\r\n");
            	writer.write("SQLdriver=com.mysql.jdbc.Driver\r\n");
            	writer.write("SQLuser=root\r\n");
            	writer.write("SQLpass=root\r\n");
            	writer.write("SQLdb=jdbc:mysql://localhost:3306/minecraft\r\n");
                 */
            } catch (IOException e) {
                logger.warn("Exception while reading configuration", e);
            }
        } else {
            try {
                rootNode = loader.load();
            } catch (IOException e) {
                logger.warn("Exception while reading configuration", e);
            }
        }
        loadGriefAlertData();
        registerListeners();
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        logger.info("Reloading GriefAlert data...");
        useWatchList.clear();
        interactWatchList.clear();
        destroyWatchList.clear();
        loadGriefAlertData();
    }

    private void registerListeners() {
        Sponge.getEventManager().registerListener(this, ChangeBlockEvent.Break.class, Order.POST, new GriefDestroyListener(logger));
        Sponge.getEventManager().registerListener(this, ChangeBlockEvent.Place.class, Order.POST, new GriefPlacementListener(logger));
        if (readConfigBool("logSignsContent")) {
            Sponge.getEventManager().registerListener(this, ChangeSignEvent.class, Order.POST, new GriefSignListener(logger));
        }
        Sponge.getEventManager().registerListener(this, InteractBlockEvent.Secondary.class, Order.LAST, new GriefInteractListener(logger));
        Sponge.getEventManager().registerListener(this, InteractEntityEvent.class, Order.POST, new GriefHangingEntityListener(logger));
    }

    public static boolean isUseWatched(String blockName) {
        return useWatchList.containsKey(blockName);
    }

    public static boolean isInteractWatched(String blockName) {
        return interactWatchList.containsKey(blockName);
    }

    public static boolean isDestroyWatched(String blockName) {
        return destroyWatchList.containsKey(blockName);
    }

    public static GriefAction getUseAction(String blockName) {
        return useWatchList.get(blockName);
    }

    public static GriefAction getInteractAction(String blockName) {
        return interactWatchList.get(blockName);
    }

    public static GriefAction getDestroyedAction(String blockName) {
        return destroyWatchList.get(blockName);
    }

    private void loadGriefAlertData() {
        File dataSource = new File("config/griefalert/watchedBlocks.txt");

        if (!dataSource.exists()) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(dataSource);
                writer.write("#Add the blocs to be watched here (without the #).\r\n");
                writer.write("#Fromat is : USE|DESTROY|INTERACT:blockName(without namespace):alertColor\r\n");
                writer.write("#Here are some examples :\r\n");
                writer.write("#USE:lava_bucket:c\r\n");
                writer.write("#DESTORY:diamond_block:3\r\n");
                writer.write("#BREAK:diamond_block:3\r\n");
                writer.write("#INTERACT:chest:3\r\n");
            } catch (Exception e) {
                logger.warn("Exception while creating watchedBlocks.txt");
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    logger.warn("Exception while closing writer for watchedBlocks.txt");
                }
            }
        }

        try {
            Scanner scanner = new Scanner(dataSource);
            String[] splitedLine;

            String blockID;
            boolean denied;
            boolean stealth;
            int onlyin;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#") || line.equals("")) {
                    continue;
                }
                splitedLine = line.split(":");
                if (splitedLine.length >= 3) {
                    // 0: type, 1: ID, 2: color, 3: stealth alarm

                    blockID = splitedLine[1].replace('-', ':');
                    if (!blockID.contains(":")) {
                        blockID = "minecraft:" + blockID;
                    }
                    char colorCode = splitedLine[2].charAt(0);
                    if ("123456789abcdef".indexOf(colorCode) == -1) {
                        logger.info("watchedBlocks.txt - invalid colorCode : " + colorCode + "  Defaulting to 'C'");
                        colorCode = 'C';
                    }
                    denied = false; //splitedLine[4].equalsIgnoreCase("deny");
                    stealth = splitedLine.length > 3 && splitedLine[3].equalsIgnoreCase("stealth");

                    onlyin = 0;
                    if (splitedLine.length > 6) {
                        if (splitedLine[6].equalsIgnoreCase("onlyinnether")) {
                            onlyin = -1;
                        }
                        if (splitedLine[6].equalsIgnoreCase("onlyinend")) {
                            onlyin = 1;
                        }
                        if (splitedLine[6].equalsIgnoreCase("onlyinnetherandend")) {
                            onlyin = -2;
                        }
                    }


                    if (splitedLine[0].equalsIgnoreCase("USE")) {
                        useWatchList.put(blockID, new GriefAction(blockID, colorCode, false, stealth, onlyin, GriefAction.Type.USE));
                    } else if (splitedLine[0].equalsIgnoreCase("DESTROY")) {
                        destroyWatchList.put(blockID, new GriefAction(blockID, colorCode, false, stealth, onlyin, GriefAction.Type.DESTORY));
                    } else if (splitedLine[0].equalsIgnoreCase("INTERACT")) {
                        interactWatchList.put(blockID, new GriefAction(blockID, colorCode, false, stealth, onlyin, GriefAction.Type.INTERACT));
                    } else {
                        logger.warn("watchedBlocks.txt - unrecognized activator : " + splitedLine[0]);
                    }
                } else {
                    logger.warn("watchedBlocks.txt - line skipped (invalid format)");
                }
            }
            scanner.close();
        } catch (Exception e) {
            logger.warn("Exception while loading", e);
        }
    }

    public static int readConfigInt(String key) {
        return rootNode.getNode(key).getInt();
    }

    public static String readConfigStr(String key) {
        return rootNode.getNode(key).getString();
    }

    public static boolean readConfigBool(String key) {
        return rootNode.getNode(key).getBoolean();
    }
}
