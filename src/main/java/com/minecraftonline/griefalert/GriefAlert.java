package com.minecraftonline.griefalert;

import com.google.inject.Inject;
import com.minecraftonline.griefalert.GriefAction.GriefType;
import com.minecraftonline.griefalert.listeners.*;
import com.minecraftonline.griefalert.tools.General.IllegalColorCodeException;

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
 */
public class GriefAlert {
	
	/** Version of this Plugin. (Should this be final?) */
    static final String VERSION = "21.0";
    
    /** Item used by staff members to 'degrief' a grief event. This is logged but not acted on by in-game staff. */
    public static final String DEGRIEF_ITEM = "minecraft:stick";
    /** The maximum number of reports before the code limit is reset. This is for ease of use by in-game staff. */
    public static final int ALERTS_CODE_LIMIT = 999;
    /** The maximum number of identical reports to hide successively. */
    public static final int MAX_REPEATED_HIDDEN_ALERT = 10;
    /** Is logging whether someone changes a sign going to be logged? */
    public static final boolean LOG_SIGNS_CONTENT = true;
    /** Will there be in game alerts to help debug the plugin? */
    public static final boolean DEBUG_IN_GAME_ALERTS = false;
    /** Will the alerts be shown in the console as well as in game? */
    public static final boolean SHOW_ALERTS_IN_CONSOLE = false;

    public static final String SQL_USERNAME = "user";
    public static final String SQL_PASSWORD = "PA$$word";
    public static final String SQL_ADDRESS = "localhost:3306/minecraft";
    
    /** The file name of the file which holds information about which activities will be watched and logged. */
    public static final String GRIEF_ALERT_FILE_NAME = "watchedBlocks.txt";
    /** The file path of the grief alert file. */
    public static final String GRIEF_ALERT_FILE_PATH = "config/griefalert/" + GRIEF_ALERT_FILE_NAME;
    
    private GriefActionTableManager griefActions = new GriefActionTableManager();
    
    @Inject
    /** General logger. From Sponge API. */
    private Logger logger;
    
    /** Grief logger. */
    private GriefLogger gLogger;
    
    /** Alert Tracker. */
    private RealtimeGriefInstanceManager realtimeManager;

    @Inject
    @DefaultConfig(sharedRoot = false)
    /** Location of the default configuration file for this plugin. From Sponge API. */
    private Path defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = false)
    /** Configuration manager of the configuration file. From Sponge API. */
    private ConfigurationLoader<CommentedConfigurationNode> configManager;
    /** The root node of the configuration file, using the configuration manager. */
    private ConfigurationNode rootNode;

    

    @Listener
    /**
     * Run initialization sequence before the game starts.
     * All classes that other classes depend on must be initialized here.
     * @param event the event run before the game starts
     */
    public void initialize(GamePreInitializationEvent event) {
        logger.info("Initializing GriefAlert...");
        
        // Load the config from the Sponge API and set the specific node values.
        initializeConfig();
        
        // Classes which other classes depend on must be initialized here. 
        this.setGriefLogger(new GriefLogger(this));
        this.realtimeManager = new RealtimeGriefInstanceManager(this);
        
        // Read the grief alert file
        readGriefAlertFile(loadGriefAlertFile());
        
        // Register all the listeners with Sponge
        registerListeners(realtimeManager);
        
        // Register the command for checking grief alerts
        CommandSpec gcheckin = CommandSpec.builder().
                executor(new GriefAlertCommand(this)).
                                                  description(Text.of("Check a GriefAlert Number")).
                                                  arguments(GenericArguments.optional(GenericArguments.integer(Text.of("code")))).
                                                  permission("griefalert.check").
                                                  build();
        Sponge.getCommandManager().register(this, gcheckin, "gcheckin");
    }

    
    /**
     * To be run when the plugin reloads
     * @param event The GameReloadEvent
     */
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

    /**
     * Initializes the configuration nodes with their appropriate values, designated as
     * local static variables.
     */
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
    
    /**
     * Finds the Grief Alert File and returns it.
     * If no file exists, then generate one based on the default Grief Alert File and writes into
     * the correct location.
     * @return The Grief Alert File
     */
    private File loadGriefAlertFile() {
    	logger.info("Loading GriefAlert file: " + GRIEF_ALERT_FILE_NAME + " ...");
    	
    	// Get the file
        File griefAlertFile = new File(GRIEF_ALERT_FILE_PATH);
        if (!griefAlertFile.exists()) {
        	// Create the file because it doesn't exist yet
        	
            logger.info("Watch list file being created...");
            OutputStream outStream = null;
            try {
            	
            	// Read the data from a local resource file which has all the default information
            	InputStream initialStream = this.getClass().getResourceAsStream("defaultGriefAlertFile.txt");
	            byte[] buffer = new byte[initialStream.available()];
	            initialStream.read(buffer);
	            outStream = new FileOutputStream(griefAlertFile);
	            
	            // Write the default file to the new local file
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
    
    /**
     * Reads the Grief Alert File and inputs all its data into the Grief Action Table Manager.
     * @param griefAlertFile The file which has all Grief Actions
     */
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
                // Try to generate the griefAction
                try {
                	// Try to generate the griefAction with the appropriate color
                	try {
                		griefAction = new GriefAction(splitLine);
		            } catch (IllegalColorCodeException e) {
		            	// An invalid color code was inputed
		            	logger.info(GRIEF_ALERT_FILE_NAME + " - " + e.getMessage() + " @ Line: " + "| defaulting to: " + GriefAction.DEFAULT_ALERT_COLOR);
		            	// Default the color code
		            	splitLine[2] = String.valueOf(GriefAction.DEFAULT_ALERT_COLOR);
		            	griefAction = new GriefAction(splitLine);
		            }
                } catch (IllegalArgumentException e) {
                	logger.info(GRIEF_ALERT_FILE_NAME + " - " + e.getMessage() + " @ Line: " + line);
                	// Fatal error occurred with this line. Skipping line entirely.
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
    
    /**'
     * Registers all listeners with Sponge to appropriately read information coming from the server
     * @param manager the RealtimeGriefInstanceManager to deal with incoming Grief Instances
     */
    private void registerListeners(RealtimeGriefInstanceManager manager) {
        Sponge.getEventManager().registerListener(this, ChangeBlockEvent.Break.class, Order.LAST, new GriefDestroyListener(this));
        Sponge.getEventManager().registerListener(this, ChangeBlockEvent.Place.class, Order.LAST, new GriefPlacementListener(this));
        if (getConfigBoolean("logSignsContent")) {
            Sponge.getEventManager().registerListener(this, ChangeSignEvent.class, Order.LAST, new GriefSignListener(this));
        }
        Sponge.getEventManager().registerListener(this, InteractBlockEvent.Secondary.class, Order.LAST, new GriefInteractListener(this));
        Sponge.getEventManager().registerListener(this, InteractEntityEvent.class, Order.LAST, new GriefEntityListener(this));
        Sponge.getEventManager().registerListener(this, UseItemStackEvent.Start.class, Order.LAST, new GriefUsedListener(this));
    }
    
    /**
     * Determines whether a type of grief, a blockId (griefable object), and a specific dimension in which
     * the grief would occur is a type of grief action.
     * @param type The GriefType
     * @param blockId The id for the griefable object
     * @param dType The type of dimension
     * @return true if these together are considered a Grief Action
     */
    public boolean isGriefAction(GriefType type, String blockId, DimensionType dType) {
    	return griefActions.contains(type, blockId, dType.getId());
    }
    
    /**
     * Gets the grief action associated with this grief type, this griefable object, and this specific
     * dimension in which the grief would occur.
     * @param type The GriefType
     * @param blockId The id for the griefable object
     * @param dType The type of dimension
     * @return Returns the GriefAction in the specified dimension, the grief action designated for all dimensions
     * or null if none exists.
     */
    public GriefAction getGriefAction(GriefType type, String blockId, DimensionType dType) {
    	if (griefActions.contains(type, blockId, dType.getId())) {
    		return griefActions.get(type, blockId, dType.getId());
    	} else {
    		return griefActions.get(type, blockId, "ALL");
    	}
    }

    /**
     * Gets the integer associated at the specific key in the configuration.
     * @param key
     * @return
     */
    public int getConfigInt(String key) {
        return rootNode.getNode(key).getInt();
    }

    /**
     * Gets the String associated at the specific key in the configuration.
     * @param key
     * @return
     */
    public String getConfigString(String key) {
        return rootNode.getNode(key).getString();
    }

    /**
     * Gets the boolean associated at the specific key in the configuration.
     * @param key
     * @return
     */
    public boolean getConfigBoolean(String key) {
        return rootNode.getNode(key).getBoolean();
    }

    /**
     * Gets the generic logger associated with Sponge.
     * @return
     */
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * Gets the real-time manager of grief instances during game play
	 * @return This RealtimeGriefInstanceManager
	 */
	public RealtimeGriefInstanceManager getRealtimeGriefInstanceManager() {
		return realtimeManager;
	}
	
	/**
	 * Sets the GriefLogger for this plugin.
	 * @param griefLogger
	 */
    private void setGriefLogger(GriefLogger griefLogger) {
		this.gLogger = griefLogger;
		
	}
    
    /**
     * Gets the GriefLogger for this plugin.
     * @return This GriefLogger
     */
    public GriefLogger getGriefLogger() {
    	return this.gLogger;
    }
}
