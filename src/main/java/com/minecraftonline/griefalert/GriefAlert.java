package com.minecraftonline.griefalert;

import com.google.inject.Inject;
//import com.minecraftonline.griefalert.commands.GriefAlert_Toggle_Command;
//import com.minecraftonline.griefalert.commands.GriefCheckCommand;
//import com.minecraftonline.griefalert.commands.GriefInfoCommand;
//import com.minecraftonline.griefalert.commands.GriefRecentCommand;
//import com.minecraftonline.griefalert.commands.GriefReturnCommand;
import com.minecraftonline.griefalert.commands.GriefAlertCommand;
import com.minecraftonline.griefalert.commands.GriefAlertSaveprofileCommand;
import com.minecraftonline.griefalert.griefevents.GriefEventCache;
import com.minecraftonline.griefalert.griefevents.profiles.EventWrapper;
import com.minecraftonline.griefalert.griefevents.profiles.GriefProfileMuseum;
import com.minecraftonline.griefalert.griefevents.GriefEvent;
import com.minecraftonline.griefalert.listeners.*;
import com.minecraftonline.griefalert.storage.ConfigHelper;

import com.minecraftonline.griefalert.griefevents.logging.GriefEventLogger;

import com.minecraftonline.griefalert.tools.General;
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
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import static com.minecraftonline.griefalert.GriefAlert.VERSION;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

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
 * <li><i>griefalert.command.greturn</i>: Allows the use of the /greturn command</li>
 * <li><i>griefalert.staff</i>: Shows staff messages</li>
 * <li><i>griefalert.noalert</i>: Doesn't trigger an alert</li>
 * <li><i>griefalert.degrief</i>: Allows staff to degrief blocks with the given degrief tool</li>
 */
@Plugin(id = "griefalert",
    name = "GriefAlert",
    version = VERSION,
    description = "Grief alert tool")
public class GriefAlert implements PluginContainer {
  public GriefAlert() {
  }

  // TODO try to get floating text to work above grief instance sites
  // TODO Test all grief types
  // TODO Test log_signs_content config nodes

  public static final class Permission {

    public static final Permission GRIEFALERT_COMMAND = new Permission("griefalert.command");
    public static final Permission GRIEFALERT_COMMAND_CHECK = new Permission("griefalert.command.check");
    public static final Permission GRIEFALERT_COMMAND_INFO = new Permission("griefalert.command.info");
    public static final Permission GRIEFALERT_COMMAND_RECENT = new Permission("griefalert.command.recent");
    public static final Permission GRIEFALERT_COMMAND_LOGS = new Permission("griefalert.command.logs");
    public static final Permission GRIEFALERT_COMMAND_ROLLBACK = new Permission("griefalert.command.rollback");
    public static final Permission GRIEFALERT_COMMAND_ADDPROFILE = new Permission("griefalert.command.addprofile");
    public static final Permission GRIEFALERT_MESSAGING = new Permission("griefalert.messaging");
    public static final Permission GRIEFALERT_SILENT = new Permission("griefalert.silent");
    public static final Permission GRIEFALERT_DEGRIEF = new Permission("griefalert.degrief");
    public static final Permission GRIEFALERT_COMMAND_RELOAD = new Permission("griefalert.reload");


    private final String permissionString;

    private Permission(String permissionString) {
      this.permissionString = permissionString;
    }

    public String toString() {
      return permissionString;
    }
  }

  public static final class GriefType {

    public static final GriefType DESTROY = new GriefType("destroy", "destroyed", "destroy");
    public static final GriefType INTERACT = new GriefType("interact", "interacted with", "interact with");
    public static final GriefType USE = new GriefType("use", "used", "use");
    public static final GriefType DEGRIEF = new GriefType("degrief", "degriefed", "degrief");

    private final String name;
    private final String preteriteVerb;
    private final String presentVerb;

    private GriefType(String name, String preteriteVerb, String presentVerb) {
      this.name = name;
      this.preteriteVerb = preteriteVerb;
      this.presentVerb = presentVerb;
    }

    public static GriefType from(String name) throws IllegalArgumentException {
      switch(name.toLowerCase()) {
        case "destroy":
          return DESTROY;
        case "interact":
          return INTERACT;
        case "use":
          return USE;
        case "degrief":
          return DEGRIEF;
        default:
          throw new IllegalArgumentException("Illegal GriefType name. Options are 'destroy', 'interact', 'use', or 'degrief'");
      }
    }

    public String getName() {
      return name;
    }

    public String toPreteriteVerb() {
      return preteriteVerb;
    }

    public String toPresentVerb() {
      return presentVerb;
    }

  }

  /**
   * Version of this Plugin. (Should this be final?)
   */
  public static final String VERSION = "23.0";


  /**
   * The file name of the file which holds information about which activities will be watched and logged.
   */
  public static final String GRIEF_ALERT_FILE_NAME = "grief_alert_list.txt";

  /**
   * An array list all dimensions to use when needing to place Grief Actions into all possible dimensions.
   */
  public static final String[] ALL_DIMENSIONS = new String[]{"overworld", "nether", "the_end"};

  private final HashMap<UUID, GriefAlertSaveprofileCommand.GriefProfileBuilder> profileBuilderMap = new HashMap<>();

  @Inject
  /** General logger. From Sponge API. */
  private Logger logger;

  /**
   * Grief logger.
   */
  private GriefEventLogger gLogger;

  private ConfigHelper configHelper;

  @Inject
  @DefaultConfig(sharedRoot = false)
  /** Location of the default configuration file for this plugin. From Sponge API. */
  private Path defaultConfig;

  @Inject
  @DefaultConfig(sharedRoot = false)
  /** Configuration manager of the configuration file. From Sponge API. */
  private ConfigurationLoader<CommentedConfigurationNode> configManager;
  /**
   * The root node of the configuration file, using the configuration manager.
   */
  private ConfigurationNode rootNode;

  @Inject
  @ConfigDir(sharedRoot = false)
  private File configDirectory;

  @Inject
  private PluginContainer container;

  private GriefProfileMuseum museum;
  private GriefEventCache griefEventCache;

  /**
   * Run initialization sequence before the game starts.
   * All classes that other classes depend on must be initialized here.
   *
   * @param event the event run before the game starts
   */
  @Listener
  public void initialize(GamePreInitializationEvent event) {
    General.stampConsole();


    // Load the config from the Sponge API and set the specific node values.
    this.configHelper = new ConfigHelper(this, defaultConfig);
    configHelper.initializeConfig();
    museum = new GriefProfileMuseum(this);
    griefEventCache = new GriefEventCache(this);

    // Classes which other classes depend on must be initialized here.
    this.setGriefLogger(new GriefEventLogger(this));

    // Register all the listeners with Sponge
    registerListeners();

    // Register all the commands with Sponge
    registerCommands();

  }


  /**
   * To be run when the plugin reloads
   *
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
  }


  /**
   * '
   * Registers all listeners with Sponge to appropriately read information coming from the server
   */
  private void registerListeners() {
//    Sponge.getEventManager().registerListener(this, ChangeBlockEvent.Break.class, Order.LAST, new GriefDestroyListener(this));
//    if (getConfigBoolean("logSignsContent")) {
//      Sponge.getEventManager().registerListener(this, ChangeSignEvent.class, Order.LAST, new GriefSignListener(this));
//    }
//    Sponge.getEventManager().registerListener(this, Event.class, Order.LAST, new GriefInteractListener(this));
//    Sponge.getEventManager().registerListener(this, Event.class, Order.LAST, new GriefUsedListener(this));
    Sponge.getEventManager().registerListener(this, Event.class, Order.FIRST, new GlobalListener(this));
  }

  @SuppressWarnings("deprecation")
  private void registerCommands() {
    GriefAlertCommand griefAlertCommand = new GriefAlertCommand(
        this
    );
    Sponge.getCommandManager().register(this, griefAlertCommand.buildCommandSpec(), griefAlertCommand.getAliases());
  }

  private void registerConditions() {
    // Empty for now
  }

  private void registerCompletions() {
    // Empty for now
  }

  public void handleGriefEvent(EventWrapper event) {
    if (museum.getMatchingProfile(event).isPresent()) {
      GriefEvent.throwGriefEvent(this, museum.getMatchingProfile(event).get(), event);
    }
    // Else: This was just an event which we listen for but did not match a Grief Profile
  }

  /**
   * Gets the integer associated at the specific key in the configuration.
   *
   * @param key
   * @return
   */
  public int getConfigInt(String key) {
    return rootNode.getNode(key).getInt();
  }

  /**
   * Gets the String associated at the specific key in the configuration.
   *
   * @param key
   * @return
   */
  public String getConfigString(String key) {
    return rootNode.getNode(key).getString();
  }

  /**
   * Gets the boolean associated at the specific key in the configuration.
   *
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
   *
   * @return
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * Sets the GriefLogger for this plugin.
   *
   * @param griefLogger
   */
  private void setGriefLogger(GriefEventLogger griefLogger) {
    this.gLogger = griefLogger;

  }

  public File getDataDirectory() {
    return new File(configDirectory.getParentFile().getParentFile().getPath() + "/" + "griefalert");
  }

  /**
   * Gets the GriefLogger for this plugin.
   *
   * @return This GriefLogger
   */
  public GriefEventLogger getGriefLogger() {
    return this.gLogger;
  }

  public File getGriefAlertDirectory() {
    return configDirectory;
  }

  public GriefProfileMuseum getMuseum() {
    return museum;
  }

  public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
    return configManager;
  }

  public ConfigurationNode getRootNode() {
    return rootNode;
  }

  public void setRootNode(ConfigurationNode rootNode) {
    this.rootNode = rootNode;
  }

  public GriefEventCache getGriefEventCache() {
    return griefEventCache;
  }

  public Optional<GriefAlertSaveprofileCommand.GriefProfileBuilder> putProfileBuilder(Player player, GriefAlertSaveprofileCommand.GriefProfileBuilder builder) {
    if (!player.hasPermission(Permission.GRIEFALERT_COMMAND_ADDPROFILE.toString())) {
      return Optional.empty();
    } else {
      return Optional.ofNullable(profileBuilderMap.put(player.getUniqueId(), builder));
    }
  }

  public Optional<GriefAlertSaveprofileCommand.GriefProfileBuilder> getProfileBuilder(Player player) {
    return Optional.ofNullable(profileBuilderMap.get(player.getUniqueId()));
  }

  public GriefAlertSaveprofileCommand.GriefProfileBuilder removeProfileBuilder(Player player) {
    return profileBuilderMap.remove(player.getUniqueId());
  }

  @Override
  public String getId() {
    return "griefalert";
  }
}
