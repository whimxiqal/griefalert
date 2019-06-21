package com.minecraftonline.griefalert;

import com.google.inject.Inject;
import com.minecraftonline.griefalert.commands.GriefAlertCommand;
import com.minecraftonline.griefalert.commands.GriefAlert_Toggle_Command;
import com.minecraftonline.griefalert.commands.GriefCheckCommand;
import com.minecraftonline.griefalert.commands.GriefInfoCommand;
import com.minecraftonline.griefalert.commands.GriefRecentCommand;
import com.minecraftonline.griefalert.core.GriefAction;
import com.minecraftonline.griefalert.core.GriefAction.GriefType;
import com.minecraftonline.griefalert.core.GriefActionTableManager;
import com.minecraftonline.griefalert.core.GriefManager;
import com.minecraftonline.griefalert.listeners.*;
import com.minecraftonline.griefalert.tools.General.IllegalColorCodeException;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.SpongeCommandIssuer;
import co.aikar.commands.SpongeCommandManager;

import com.minecraftonline.griefalert.storage.GriefLogger;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.DimensionType;

import static com.minecraftonline.griefalert.GriefAlert.VERSION;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The main class for the plugin Grief Alert.
 * This plugin is made exclusively for MinecraftOnline.com
 * Do not use this plugin without explicit approval from an administrator of MinecraftOnline.
 * <p>
 * <b>Permissions</b>
 * <p>
 * <li><i>griefalert.command.gcheck</i>: Allows the use of the /gcheck command</li>
 * <li><i>griefalert.command.grecent</i>: Allows the use of the /grecent command</li>
 * <li><i>griefalert.command.toggle</i>: Allows the use of the /griefalert toggle command</li>
 * <li><i>griefalert.command.ginfo</i>: Allows the use of the /ginfo command</li>
 * <li><i>griefalert.staff</i>: Shows staff messages</li>
 * <li><i>griefalert.noalert</i>: Doesn't trigger an alert</li>
 * <li><i>griefalert.degrief</i>: Allows staff to degrief blocks with the given degrief tool</li>
 */
@Plugin(id = "griefalert",
		name = "GriefAlert",
		version = VERSION,
		description = "Grief alert tool")
public class GriefAlert implements PluginContainer {
	// TODO try to get floating text to work above grief instance sites
	// TODO Test all grief types
	// TODO Test log_signs_content config nodes
	/** Version of this Plugin. (Should this be final?) */
    static final String VERSION = "22.0";
    
    /** Item used by staff members to 'degrief' a grief event. This is logged but not acted on by in-game staff. */
    public static final String DEGRIEF_ITEM = "minecraft:stick";
    /** The maximum number of reports before the code limit is reset. This is for ease of use by in-game staff. */
    public static final int ALERTS_CODE_LIMIT = 9999;
    /** The maximum number of identical reports to hide successively. */
    public static final int MAX_REPEATED_HIDDEN_ALERT = 10;
    /** Is logging whether someone changes a sign going to be logged? */
    public static final boolean LOG_SIGNS_CONTENT = true;
    /** Will the alerts be shown in the console as well as in game? */
    public static final boolean SHOW_ALERTS_IN_CONSOLE = true;
    /** An array list all dimensions to use when needing to place Grief Actions into all possible dimensions. */
    public static final String[] ALL_DIMENSIONS = new String[] {"minecraft:overworld", "minecraft:nether", "minecraft:the_end"};
    /** The regex between each component of a Grief Alert in the Grief Alert configuration file. */
    public static final String GRIEF_ALERT_CONFIG_LINE_REGEX = ";";
    
    public static final String DEFAULT_STAFF_ALERT_MESSAGE = 
    		"(PLAYER) (GRIEF_VERB) a (GRIEF_OBJECT) ((GRIEF_ID)) in the (LOCATION:DIMENSION)";
    
    public static final String DEFAULT_STAFF_ALERT_MESSAGE_SIGN_HEADER = 
    		"Sign placed by (PLAYER) at (LOCATION:COORDINATES) in the (LOCATION:DIMENSION)";
    
    public static final String DEFAULT_STAFF_ALERT_MESSAGE_SIGN_LINE = 
    		"Line (SIGN_LINE_NUMBER): (SIGN_LINE_CONTENT)";
    
    public static final String SQL_USERNAME = "user";
    public static final String SQL_PASSWORD = "PA$$word";
    public static final String SQL_ADDRESS = "localhost:3306/minecraft";
    
    /** The file name of the file which holds information about which activities will be watched and logged. */
    public static final String GRIEF_ALERT_FILE_NAME = "grief_alert_list.txt";
    
    public boolean debugMode = false;
    
    private GriefActionTableManager griefActions = new GriefActionTableManager();
    
    @Inject
    /** General logger. From Sponge API. */
    private Logger logger;
    
    /** A logger for only sending messages for debugging the behavior of this plugin. */
    private DebugLogger dLogger;
    
    /** Grief logger. */
    private GriefLogger gLogger;
    
    /** Alert Tracker. */
    private GriefManager griefManager;
    
    private SpongeCommandManager commandManager;

    private GriefInfoCommand griefInfoCommand;
    
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
    
    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDirectory;
    
    @Inject
    private PluginContainer container;

    @Listener
    /**
     * Run initialization sequence before the game starts.
     * All classes that other classes depend on must be initialized here.
     * @param event the event run before the game starts
     */
    public void initialize(GamePreInitializationEvent event) {
        logger.info("Initializing GriefAlert...");
        
        this.dLogger = new DebugLogger(this.getLogger(), debugMode);
        
        // Load the config from the Sponge API and set the specific node values.
        initializeConfig();
        
        // Classes which other classes depend on must be initialized here. 
        this.setGriefLogger(new GriefLogger(this));
        this.griefManager = new GriefManager(this);
        
        // Read the grief alert file
        readGriefAlertFile(loadGriefAlertFile());
        
        dLogger.log("All flagged Grief Actions:");
        for (GriefAction griefAction : griefActions.values()) {
        	dLogger.log(griefAction.toString());
        }
        
        // Register all the listeners with Sponge
        registerListeners(griefManager);
        
        // Register all the commands with Sponge
        registerCommands();
       
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
                rootNode.getNode("showAlertsInConsole").setValue(SHOW_ALERTS_IN_CONSOLE);
                rootNode.getNode("debugMode").setValue(false);
                rootNode.getNode("SQLusername").setValue(SQL_USERNAME);
                rootNode.getNode("SQLpassword").setValue(SQL_PASSWORD);
                rootNode.getNode("SQLdb").setValue(SQL_ADDRESS);
                ConfigurationNode messagingNode = rootNode.getNode("messaging");
                messagingNode.getNode("staff_alert_message").setValue(DEFAULT_STAFF_ALERT_MESSAGE);
                messagingNode.getNode("staff_alert_message_sign_header").setValue(DEFAULT_STAFF_ALERT_MESSAGE_SIGN_HEADER);
                messagingNode.getNode("staff_alert_message_sign_line").setValue(DEFAULT_STAFF_ALERT_MESSAGE_SIGN_LINE);
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
    	
    	if (getGriefAlertDirectory().mkdir()) getLogger().info("Grief Alert Configuration directory created.");
    	
    	// Get the file
    	Path griefAlertFilePath = Paths.get(getGriefAlertDirectory().getPath(), GRIEF_ALERT_FILE_NAME);
        
    	getLogger().info("File path: " + griefAlertFilePath.toString());
        if (Files.notExists(griefAlertFilePath)) {
        	getLogger().info("File doesn't exist yet! Trying to create as: " + GRIEF_ALERT_FILE_NAME);
            getAsset("grief_alerts.txt").ifPresent(asset -> {
				try {
					asset.copyToFile(griefAlertFilePath, false);
					getLogger().info(GRIEF_ALERT_FILE_NAME + " created successfully.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
        }
		return griefAlertFilePath.toFile();
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
            while (scanner.hasNext()) {
                line = scanner.nextLine();
                
                // Skip commented line or empty line
                if (line.startsWith("#") || line.equals("")) {
                    continue;
                }
                
                // Get rid of commented sections
                String[] tokens = line.split("#");
                line = tokens[0];
                
                splitLine = line.split(GRIEF_ALERT_CONFIG_LINE_REGEX);
                
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
                	logger.warn(GRIEF_ALERT_FILE_NAME + " - " + e.getMessage() + " @ Line: " + line);
                	// Fatal error occurred with this line. Skipping line entirely.
                	continue;
                }
                
                // Format dimension list which this report applies to.
                String[] applicableDimensions;
                if (splitLine.length > 5) {
                	applicableDimensions = splitLine[5].split(",");
                } else {
                	applicableDimensions = ALL_DIMENSIONS;
                }
                
                // Input the grief action into the tables
	            for (String dim : applicableDimensions) {
	                griefActions.put(griefAction.getType(), griefAction.getBlockId(), dim, griefAction);
	                getDebugLogger().log("Grief Action put into Table Manager of Grief Actions. "
	                		+ "Type: " + griefAction.getType() + " "
	                		+ "Dimension: " + dim + ", "
	                		+ "BlockId: " + griefAction.getBlockId());
	            }
	            getDebugLogger().log("Grief action loaded: " + griefAction.toString() + "dimensions: " + String.join(", ", applicableDimensions));
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
    private void registerListeners(GriefManager manager) {
        Sponge.getEventManager().registerListener(this, ChangeBlockEvent.Break.class, Order.LAST, new GriefDestroyListener(this));
        if (getConfigBoolean("logSignsContent")) {
            Sponge.getEventManager().registerListener(this, ChangeSignEvent.class, Order.LAST, new GriefSignListener(this));
        }
        Sponge.getEventManager().registerListener(this, Event.class, Order.LAST, new GriefInteractListener(this));
        Sponge.getEventManager().registerListener(this, Event.class, Order.LAST, new GriefUsedListener(this));
    }
    
    @SuppressWarnings("deprecation")
	private void registerCommands() {
    	commandManager = new SpongeCommandManager(this.container);
    	commandManager.enableUnstableAPI("help");
    	commandManager.createRootCommand("gcheck");
    	commandManager.createRootCommand("grecent");
    	commandManager.createRootCommand("griefalert");
    	commandManager.createRootCommand("ginfo");
    	commandManager.registerCommand(new GriefAlertCommand(this));
    	commandManager.registerCommand(new GriefAlert_Toggle_Command(this));
    	commandManager.registerCommand(new GriefCheckCommand(this));
    	commandManager.registerCommand(new GriefRecentCommand(this));
    	commandManager.registerCommand(griefInfoCommand = new GriefInfoCommand(this));
    	registerConditions();
    	registerCompletions();
    	
    }
    
    private void registerConditions() {
    	// @Condition("player)
		commandManager.getCommandConditions().addCondition("player", (context) -> {
			SpongeCommandIssuer issuer = context.getIssuer();
			if (!(issuer.isPlayer())) {
				throw new ConditionFailedException("You must be a player to execute this command.");
			}
		});
	}
    
    private void registerCompletions() {
    	commandManager.getCommandCompletions().registerCompletion("players", c -> {
    			List<String> onlinePlayerNames = new ArrayList<String>();
    			for (Player player : Sponge.getServer().getOnlinePlayers()) {
    				onlinePlayerNames.add(player.getName());
    			}
    			return onlinePlayerNames;
    		});
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
    
    public ConfigurationNode getConfigNode(String key) {
    	return rootNode.getNode(key);
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
	public GriefManager getGriefManager() {
		return griefManager;
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
    
    public File getGriefAlertDirectory() {
    	return configDirectory;
    }
    
    public DebugLogger getDebugLogger() {
    	return dLogger;
    }
    
    public SpongeCommandManager getCommandManager() {
    	return commandManager;
    }
    
    public GriefInfoCommand getGriefInfoCommand() {
    	return this.griefInfoCommand;
    }
    
    public class DebugLogger {
    	
    	static final String DEBUG_TAG = "[DEBUG]";
    	
    	final Logger logger;
    	
    	DebugLogger(Logger logger, boolean debugMode) {
    		this.logger = logger;
    	}
    	
    	public void log(String message) {
    		if (debugMode) logger.info(DEBUG_TAG + " " + message);
    	}
    	
    }

	@Override
	public String getId() {
		return "griefalert";
	}
}
