package com.minecraftonline.griefalert;

import com.google.inject.Inject;
import com.minecraftonline.griefalert.GriefAction.GriefType;
import com.minecraftonline.griefalert.listeners.*;
import com.minecraftonline.griefalert.tools.General.IllegalColorCodeException;
import com.minecraftonline.griefalert.tools.GriefActionTableManager;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Scanner;

import static com.minecraftonline.griefalert.GriefAlert.VERSION;

@Plugin(id = "griefalert",
        name = "GriefAlert",
        version = VERSION,
        description = "Grief alert tool")
/**
 * The main class for the plugin Grief Alert.
 * This plugin is made exclusively for MinecraftOnline.com
 * Do not use this plugin without explicit approval from an administrator of MinecraftOnline.
 *
 */
public class GriefAlert {
	
    static final String VERSION = "21.0";
    public static final String DEGRIEF_ITEM = "minecraft:stick";
    public static final int ALERTS_CODE_LIMIT = 999;
    public static final int MAX_REPEATED_HIDDEN_ALERT = 10;
    public static final boolean LOG_SIGNS_CONTENT = true;
    public static final boolean DEBUG_IN_GAME_ALERTS = false;
    public static final boolean SHOW_ALERTS_IN_CONSOLE = false;

    public static final String SQL_USERNAME = "user";
    public static final String SQL_PASSWORD = "PA$$word";
    public static final String SQL_ADDRESS = "localhost:3306/minecraft";
    
    public static final String GRIEF_ALERT_FILE_NAME = "watchedBlocks.txt";
    public static final String GRIEF_ALERT_FILE_PATH = "config/griefalert/" + GRIEF_ALERT_FILE_NAME;
    
    private GriefActionTableManager griefActions = new GriefActionTableManager();
    
    @Inject
    /** General logger. From Sponge API. */
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = false)
    /** Location of the default configuration file for this plugin. From Sponge API. */
    private Path defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = false)
    /** Configuration manager of the configuration file. From Sponge API. */
    private ConfigurationLoader<CommentedConfigurationNode> configManager;
    /** The root node of the configuration file, using the configuration manager. */
    private static ConfigurationNode rootNode;

    

    @Listener
    public void initialize(GamePreInitializationEvent event) {
        logger.info("Initializing GriefAlert...");
        
        // Load the config from the Sponge API and set the specific node values.
        initializeConfig();
        
        // Read the grief alert file
        readGriefAlertFile(loadGriefAlertFile());
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
        griefActions.clear();
        readGriefAlertFile(loadGriefAlertFile());
        logger.info("GriefAlert data reloaded!");
    }

    public void initializeConfig() {
    	if (!defaultConfig.toFile().exists()) {
            logger.info("Generating new Configuration File...");
            try {
                rootNode = configManager.load();
                rootNode.getNode("degriefStickID").setValue(DEGRIEF_ITEM);
                rootNode.getNode("alertsCodeLimit").setValue(ALERTS_CODE_LIMIT);
                rootNode.getNode("maxHiddenMatchingAlerts").setValue(MAX_REPEATED_HIDDEN_ALERT);
                rootNode.getNode("logSignsContent").setValue(LOG_SIGNS_CONTENT);
                rootNode.getNode("debugInGameAlerts").setValue(DEBUG_IN_GAME_ALERTS);
                rootNode.getNode("showAlertsInConsole").setValue(SHOW_ALERTS_IN_CONSOLE);

                rootNode.getNode("SQLusername").setValue(SQL_USERNAME);
                rootNode.getNode("SQLpassword").setValue(SQL_PASSWORD);
                rootNode.getNode("SQLdb").setValue(SQL_ADDRESS);
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
    }
    
    private File loadGriefAlertFile() {
    	logger.info("Loading GriefAlert file: " + GRIEF_ALERT_FILE_NAME + " ...");
        File griefAlertFile = new File(GRIEF_ALERT_FILE_PATH);
        if (!griefAlertFile.exists()) {
            logger.info("Watch list file being created...");
            OutputStream outStream = null;
            try {
            	InputStream initialStream = this.getClass().getResourceAsStream("defaultGriefAlertFile.txt");
	            byte[] buffer = new byte[initialStream.available()];
	            initialStream.read(buffer);
	            outStream = new FileOutputStream(griefAlertFile);
				outStream.write(buffer);
			} catch (IOException e) {
				logger.warn("Exception thrown while generating " + GRIEF_ALERT_FILE_NAME);
			} finally {
				try {
					outStream.close();
				} catch (IOException e) {
					logger.warn("Exception thrown while closing OutputStream after attempting to generate " + GRIEF_ALERT_FILE_NAME);
				}
			}
        }
        
		return griefAlertFile;
    }
    
    private void readGriefAlertFile(File griefAlertFile) {
        try {
            logger.info("Watch List file being read and loaded into plugin...");
            Scanner scanner = new Scanner(griefAlertFile);
            
            String[] splitLine;
            String line;
            while ((line = scanner.nextLine()) != null) {
                
                // Skip commented line or empty line
                if (line.startsWith("#") || line.equals("")) {
                    continue;
                }
                
                splitLine = line.split(":");
                
                GriefAction griefAction;
                try {
                	griefAction = new GriefAction(splitLine);
                } catch (IllegalColorCodeException e) {
                	logger.info(GRIEF_ALERT_FILE_NAME + " - " + e.getMessage() + " @ Line: " + "| defaulting to: " + GriefAction.DEFAULT_ALERT_COLOR);
                	splitLine[2] = String.valueOf(GriefAction.DEFAULT_ALERT_COLOR);
                	griefAction = new GriefAction(splitLine);
                } catch (IllegalArgumentException e) {
                	logger.info(GRIEF_ALERT_FILE_NAME + " - " + e.getMessage() + " @ Line: " + line);
                	// Fatal error occurred with this line. Skipping line.
                	continue;
                }
                
                // Format dimension list which this report applies to.
                String[] applicableDimensions;
                if (splitLine.length > 5) {
                	applicableDimensions = splitLine[5].split(",");
                    for (int index = 0; index < applicableDimensions.length; index++) {
                        if (applicableDimensions[index].contains("-")) {
                        	applicableDimensions[index] = applicableDimensions[index].replace('-', ':');
                        } else {
                        	applicableDimensions[index] = "minecraft:" + applicableDimensions[index];
                        }
                    }
                } else {
                	applicableDimensions = new String[]{"ALL"};
                }
                
                // Input the grief action into the tables
	            for (String dim : applicableDimensions) {
	                griefActions.put(griefAction.getType(), griefAction.getBlockId(), dim, griefAction);
	            }
            }
            scanner.close();
            logger.info("Watch List file loaded!");
        } catch (Exception e) {
            logger.warn("Exception while loading", e);
        }
    }
    
    private void registerListeners(AlertTracker tracker) {
        Sponge.getEventManager().registerListener(this, ChangeBlockEvent.Break.class, Order.LAST, new GriefDestroyListener(this, tracker));
        Sponge.getEventManager().registerListener(this, ChangeBlockEvent.Place.class, Order.LAST, new GriefPlacementListener(this, tracker));
        if (readConfigBool("logSignsContent")) {
            Sponge.getEventManager().registerListener(this, ChangeSignEvent.class, Order.LAST, new GriefSignListener(this, tracker));
        }
        Sponge.getEventManager().registerListener(this, InteractBlockEvent.Secondary.class, Order.LAST, new GriefInteractListener(this, tracker));
        Sponge.getEventManager().registerListener(this, InteractEntityEvent.class, Order.LAST, new GriefEntityListener(this, tracker));
        Sponge.getEventManager().registerListener(this, UseItemStackEvent.Start.class, Order.LAST, new GriefUsedListener(this, tracker));
    }
    
    public boolean isGriefAction(GriefType type, String blockId, DimensionType dType) {
    	return griefActions.contains(type, blockId, dType.getId());
    }
    
    public GriefAction getGriefAction(GriefType type, String blockId, DimensionType dType) {
    	if (griefActions.contains(type, blockId, dType.getId())) {
    		return griefActions.get(type, blockId, dType.getId());
    	} else {
    		return griefActions.get(type, blockId, "ALL");
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
