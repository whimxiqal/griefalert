package com.minecraftonline.griefalert;

import com.google.inject.Inject;
import com.minecraftonline.griefalert.listeners.*;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

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

        if (!defaultConfig.toFile().exists()) {
            try {
                rootNode = configManager.load();
                rootNode.getNode("degriefStickID").setValue("\"minecraft:stick\"");
                rootNode.getNode("alertsCodeLimit").setValue(999);
                rootNode.getNode("logSignsContent").setValue(true);
                rootNode.getNode("debugInGameAlerts").setValue(false);
                rootNode.getNode("showAlertsInConsole").setValue(false);

                rootNode.getNode("SQLusername").setValue("user");
                rootNode.getNode("SQLpassword").setValue("PA$$word");
                rootNode.getNode("SQLdb").setValue("\"localhost:3306/minecraft\"");
                configManager.save(rootNode);
            } catch (IOException e) {
                logger.warn("Exception while reading configuration", e);
            }
        } else {
            try {
                rootNode = configManager.load();
            } catch (IOException e) {
                logger.warn("Exception while reading configuration", e);
            }
        }
        loadGriefAlertData();
        AlertTracker tracker = new AlertTracker(logger);
        registerListeners(tracker);
        CommandSpec gcheckin = CommandSpec.builder().
                executor(new GriefAlertCommand(tracker)).
                                                  description(Text.of("Check a GriefAlert Number")).
                                                  arguments(GenericArguments.optional(GenericArguments.integer(Text.of("code")))).
                                                  permission("griefalert.check").
                                                  build();
        Sponge.getCommandManager().register(this, gcheckin, "gcheckin");
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        logger.info("Reloading GriefAlert data...");
        useWatchList.clear();
        interactWatchList.clear();
        destroyWatchList.clear();
        loadGriefAlertData();
        logger.info("GriefAlert data reloaded!");
    }

    private void registerListeners(AlertTracker tracker) {
        Sponge.getEventManager().registerListener(this, ChangeBlockEvent.Break.class, Order.LAST, new GriefDestroyListener(tracker));
        Sponge.getEventManager().registerListener(this, ChangeBlockEvent.Place.class, Order.LAST, new GriefPlacementListener(tracker));
        if (readConfigBool("logSignsContent")) {
            Sponge.getEventManager().registerListener(this, ChangeSignEvent.class, Order.LAST, new GriefSignListener(tracker));
        }
        Sponge.getEventManager().registerListener(this, InteractBlockEvent.Secondary.class, Order.LAST, new GriefInteractListener(tracker));
        Sponge.getEventManager().registerListener(this, InteractEntityEvent.class, Order.LAST, new GriefEntityListener(tracker));
        Sponge.getEventManager().registerListener(this, UseItemStackEvent.Start.class, Order.LAST, new GriefUsedListener(tracker));
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
                writer.write("#Fromat is : USE|DESTROY|INTERACT:(namespace-)blockName:alertColor(:stealth[:deny])\r\n");
                writer.write("#stealth and deny flags are optional");
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
                    // 0: type, 1: ID, 2: color, 3: stealth alarm 4: allow/deny

                    blockID = splitedLine[1].replace('-', ':');
                    if (!blockID.contains(":")) {
                        blockID = "minecraft:" + blockID;
                    }
                    char colorCode = splitedLine[2].charAt(0);
                    if ("123456789abcdef".indexOf(colorCode) == -1) {
                        logger.info("watchedBlocks.txt - invalid colorCode : " + colorCode + "  Defaulting to 'C'");
                        colorCode = 'C';
                    }

                    stealth = splitedLine.length > 3 && splitedLine[3].equalsIgnoreCase("stealth");
                    denied = splitedLine.length > 4 && splitedLine[4].equalsIgnoreCase("deny");

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
                        useWatchList.put(blockID, new GriefAction(blockID, colorCode, denied, stealth, onlyin, GriefAction.Type.USED));
                    } else if (splitedLine[0].equalsIgnoreCase("DESTROY")) {
                        destroyWatchList.put(blockID, new GriefAction(blockID, colorCode, denied, stealth, onlyin, GriefAction.Type.DESTORYED));
                    } else if (splitedLine[0].equalsIgnoreCase("INTERACT")) {
                        interactWatchList.put(blockID, new GriefAction(blockID, colorCode, denied, stealth, onlyin, GriefAction.Type.INTERACTED));
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
