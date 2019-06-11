package com.minecraftonline.griefalert;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
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
import org.spongepowered.api.world.DimensionType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
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

    private static Table<String, String, GriefAction> useWatchList = HashBasedTable.create();
    private static Table<String, String, GriefAction> interactWatchList = HashBasedTable.create();
    private static Table<String, String, GriefAction> destroyWatchList = HashBasedTable.create();

    @Listener
    public void initialize(GamePreInitializationEvent event) {
        logger.info("Initializing GriefAlert...");
        if (!defaultConfig.toFile().exists()) {
            logger.info("Generating new Configuration File...");
            try {
                rootNode = configManager.load();
                rootNode.getNode("degriefStickID").setValue("minecraft:stick");
                rootNode.getNode("alertsCodeLimit").setValue(999);
                rootNode.getNode("maxHiddenMatchingAlerts").setValue(10);
                rootNode.getNode("logSignsContent").setValue(true);
                rootNode.getNode("debugInGameAlerts").setValue(false);
                rootNode.getNode("showAlertsInConsole").setValue(false);

                rootNode.getNode("SQLusername").setValue("user");
                rootNode.getNode("SQLpassword").setValue("PA$$word");
                rootNode.getNode("SQLdb").setValue("localhost:3306/minecraft");
                configManager.save(rootNode);
                logger.info("New Configuration File created successfully!");
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
        try {
            rootNode = configManager.load();
        } catch (IOException e) {
            logger.warn("Exception while reading configuration", e);
        }
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

    public static boolean isUseWatched(String blockName, DimensionType dType) {
        return useWatchList.contains(blockName, dType.getId()) || useWatchList.contains(blockName, "ALL");
    }

    public static boolean isInteractWatched(String blockName, DimensionType dType) {
        return interactWatchList.contains(blockName, dType.getId()) || interactWatchList.contains(blockName, "ALL");
    }

    public static boolean isDestroyWatched(String blockName, DimensionType dType) {
        return destroyWatchList.contains(blockName, dType.getId()) || destroyWatchList.contains(blockName, "ALL");
    }

    public static GriefAction getUseAction(String blockName, DimensionType dType) {
        return useWatchList.contains(blockName, dType.getId()) ? useWatchList.get(blockName, dType.getId())
                : useWatchList.get(blockName, "ALL");
    }

    public static GriefAction getInteractAction(String blockName, DimensionType dType) {
        return interactWatchList.contains(blockName, dType.getId()) ? interactWatchList.get(blockName, dType.getId())
                : interactWatchList.get(blockName, "ALL");
    }

    public static GriefAction getDestroyedAction(String blockName, DimensionType dType) {
        return destroyWatchList.contains(blockName, dType.getId()) ? destroyWatchList.get(blockName, dType.getId())
                : destroyWatchList.get(blockName, "ALL");
    }

    private void loadGriefAlertData() {
        logger.info("Loading GriefAlert data...");
        File dataSource = new File("config/griefalert/watchedBlocks.txt");

        if (!dataSource.exists()) {
            logger.info("Watch List file being created...");
            FileWriter writer = null;
            try {
                writer = new FileWriter(dataSource);
                writer.write("#Add the blocks to be watched here (without the #).\r\n");
                writer.write("#Format is : USE|DESTROY|INTERACT:(namespace-)blockName:alertColor(:stealth[:deny][:onlyInList])\r\n");
                writer.write("#stealth, deny, and onlyInList flags are optional");
                writer.write("#onlyInList is a comma separated list of dimensions the alert is for, replacing ':' in namespaces with '-'");
                writer.write("#Here are some examples :\r\n");
                writer.write("#USE:lava_bucket:c\r\n");
                writer.write("#DESTROY:diamond_block:3\r\n");
                writer.write("#INTERACT:chest:3\r\n");
                writer.write("#DESTROY:netherrack:c:::minecraft-overworld,minecraft-end");
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
            logger.info("Watch List file being loaded...");
            Scanner scanner = new Scanner(dataSource);
            String[] splitedLine;

            String blockID;
            boolean denied;
            boolean stealth;
            String[] onlyin;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#") || line.equals("")) {
                    continue;
                }
                splitedLine = line.split(":");
                if (splitedLine.length >= 3) {
                    // 0: type, 1: ID, 2: color, 3: stealth alarm 4: allow/deny 5: onlyIn list

                    blockID = splitedLine[1].replace('-', ':');
                    if (!blockID.contains(":")) {
                        blockID = "minecraft:" + blockID;
                    }
                    char colorCode = splitedLine[2].charAt(0);
                    if ("123456789abcdef".indexOf(colorCode) == -1) {
                        logger.info("watchedBlocks.txt - invalid colorCode : " + colorCode + " @ Line: " + line + " - Defaulting to 'C'");
                        colorCode = 'C';
                    }

                    stealth = splitedLine.length > 3 && splitedLine[3].equalsIgnoreCase("stealth");
                    denied = splitedLine.length > 4 && splitedLine[4].equalsIgnoreCase("deny");

                    onlyin = new String[]{"ALL"};
                    if (splitedLine.length > 5) {
                        onlyin = splitedLine[5].split(",");
                        for (int index = 0; index < onlyin.length; index++) {
                            if (onlyin[index].contains("-")) {
                                onlyin[index] = onlyin[index].replace('-', ':');
                            } else {
                                onlyin[index] = "minecraft:" + onlyin[index];
                            }
                        }
                    }

                    if (splitedLine[0].equalsIgnoreCase("USE")) {
                        for (String dim : onlyin) {
                            useWatchList.put(blockID, dim, new ImmutableGriefAction(blockID, colorCode, denied, stealth, GriefAction.Type.USED));
                        }
                    } else if (splitedLine[0].equalsIgnoreCase("DESTROY")) {
                        for (String dim : onlyin) {
                            destroyWatchList.put(blockID, dim, new ImmutableGriefAction(blockID, colorCode, denied, stealth, GriefAction.Type.DESTROYED));
                        }
                    } else if (splitedLine[0].equalsIgnoreCase("INTERACT")) {
                        for (String dim : onlyin) {
                            interactWatchList.put(blockID, dim, new ImmutableGriefAction(blockID, colorCode, denied, stealth, GriefAction.Type.INTERACTED));
                        }
                    } else {
                        logger.warn("watchedBlocks.txt - unrecognized activator : " + splitedLine[0] + " @ Line: " + line);
                    }
                } else {
                    logger.warn("watchedBlocks.txt - line skipped (invalid format) : " + line);
                }
            }
            scanner.close();
            logger.info("Watch List file loaded!");
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
