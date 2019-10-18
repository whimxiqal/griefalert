package com.minecraftonline.griefalert;

import static com.minecraftonline.griefalert.GriefAlert.VERSION;

import com.google.inject.Inject;
import com.minecraftonline.griefalert.commands.GriefAlertCommand;
import com.minecraftonline.griefalert.griefevents.GriefEventCache;
import com.minecraftonline.griefalert.griefevents.profiles.GriefProfileMuseum;
import com.minecraftonline.griefalert.storage.ConfigHelper;
import com.minecraftonline.griefalert.util.General;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import com.sk89q.worldedit.WorldEdit;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * The main class for the plugin Grief Alert.
 * This plugin is made exclusively for MinecraftOnline.com
 * Do not use this plugin without explicit approval from the administration team of MinecraftOnline.
 */
@Plugin(id = "griefalert",
    name = "GriefAlert",
    version = VERSION,
    description = "Grief alert tool",
    dependencies = {@Dependency(id = "prism"), @Dependency(id = "worldedit")})
public final class GriefAlert {

  public static final String VERSION = "23.0";
  private static GriefAlert instance;
  public static final String MC_VERSION = "1.12.2";

  // Injected features directly from Sponge

  @Inject
  @SuppressWarnings("UnusedDeclaration")
  private Logger logger;

  @Inject
  private PluginContainer pluginContainer;

  @Inject
  @DefaultConfig(sharedRoot = false)
  @SuppressWarnings("UnusedDeclaration")
  private ConfigurationLoader<CommentedConfigurationNode> configManager;

  /**
   * The root node of the configuration file, using the configuration manager.
   */

  private ConfigurationNode rootNode;

  @Inject
  @ConfigDir(sharedRoot = false)
  @SuppressWarnings("UnusedDeclaration")
  private File configDirectory;

  @Inject
  @DefaultConfig(sharedRoot = false)
  @SuppressWarnings("unused")
  private Path defaultConfig;

  @Inject
  @SuppressWarnings("UnusedDeclaration")
  private PluginContainer container;


  // Custom classes to help manage plugin
  private GriefProfileMuseum museum;
  private GriefEventCache griefEventCache;
  private ConfigHelper configHelper;

  @Listener
  public void onConstruction(GameConstructionEvent event) {
    instance = this;
  }

  /**
   * Run initialization sequence before the game starts.
   * All classes that other classes depend on must be initialized here.
   *
   * @param event the event run before the game starts
   */
  @Listener
  public void initialize(GamePreInitializationEvent event) {
    General.stampConsole();
    try {
      rootNode = configManager.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // Load the config from the Sponge API and set the specific node values.
    this.configHelper = new ConfigHelper(defaultConfig, rootNode);
    museum = new GriefProfileMuseum(this);
    griefEventCache = new GriefEventCache(this);

    // Register all the commands with Sponge
    registerCommands();

  }

  /**
   * To be run when the plugin reloads.
   *
   * @param event The GameReloadEvent
   */
  @Listener
  public void onReload(GameReloadEvent event) {
    reload();
  }

  public void reload() {
    logger.info("Reloading plugin");
    try {
      rootNode = configManager.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    configHelper.load(rootNode);
    museum.reload();
    // Must reload grief event logger after config
  }


  private void registerCommands() {
    GriefAlertCommand griefAlertCommand = new GriefAlertCommand();
    Sponge.getCommandManager().register(
        this,
        griefAlertCommand.buildCommandSpec(),
        griefAlertCommand.getAliases());
  }


  public File getDataDirectory() {
    return new File(configDirectory.getParentFile().getParentFile().getPath() + "/" + "griefalert");
  }

  public GriefProfileMuseum getMuseum() {
    return museum;
  }

  public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
    return configManager;
  }

  public GriefEventCache getGriefEventCache() {
    return griefEventCache;
  }

  public ConfigHelper getConfigHelper() {
    return configHelper;
  }

  public Logger getLogger() {
    return logger;
  }

  public static GriefAlert getInstance() {
    return instance;
  }

  @SuppressWarnings("checkstyle:LineLength")
  public static final class Permission {

    public static final Permission GRIEFALERT_COMMAND = new Permission(
        "griefalert.command"
    );
    public static final Permission GRIEFALERT_COMMAND_CHECK = new Permission(
        "griefalert.command.check"
    );
    public static final Permission GRIEFALERT_COMMAND_INFO = new Permission(
        "griefalert.command.info"
    );
    public static final Permission GRIEFALERT_COMMAND_RECENT = new Permission(
        "griefalert.command.recent"
    );
    public static final Permission GRIEFALERT_COMMAND_LOGS = new Permission(
        "griefalert.command.logs"
    );
    public static final Permission GRIEFALERT_COMMAND_ROLLBACK = new Permission(
        "griefalert.command.rollback"
    );
    public static final Permission GRIEFALERT_COMMAND_BUILD = new Permission(
        "griefalert.command.build"
    );
    public static final Permission GRIEFALERT_MESSAGING = new Permission(
        "griefalert.messaging"
    );
    public static final Permission GRIEFALERT_SILENT = new Permission(
        "griefalert.silent"
    );
    public static final Permission GRIEFALERT_DEGRIEF = new Permission(
        "griefalert.degrief"
    );
    public static final Permission GRIEFALERT_COMMAND_RELOAD = new Permission(
        "griefalert.reload"
    );


    private final String permissionString;

    private Permission(String permissionString) {
      this.permissionString = permissionString;
    }

    public String toString() {
      return permissionString;
    }
  }

  public static final class GriefType {

    public static final GriefType DESTROY = new GriefType(
        "destroy",
        "destroyed",
        "destroy"
    );
    public static final GriefType INTERACT = new GriefType(
        "interact",
        "interacted with",
        "interact with");
    public static final GriefType USE = new GriefType(
        "use",
        "used",
        "use");
    public static final GriefType DEGRIEF = new GriefType(
        "degrief",
        "degriefed",
        "degrief");

    private final String name;
    private final String preteriteVerb;
    private final String presentVerb;

    private GriefType(String name, String preteriteVerb, String presentVerb) {
      this.name = name;
      this.preteriteVerb = preteriteVerb;
      this.presentVerb = presentVerb;
    }

    /**
     * Get the GriefType associated with the String version of the name.
     *
     * @param name The name of the GriefType
     * @return The static instance of a GriefType with the given name
     * @throws IllegalArgumentException if an invalid name is input to this method
     */
    public static GriefType from(String name) throws IllegalArgumentException {
      switch (name.toLowerCase()) {
        case "destroy":
          return DESTROY;
        case "interact":
          return INTERACT;
        case "use":
          return USE;
        case "degrief":
          return DEGRIEF;
        default:
          throw new IllegalArgumentException(name + ": Illegal GriefType name. Options are"
              + " 'destroy', 'interact', 'use', or 'degrief'");
      }
    }

    public String getName() {
      return name;
    }

    @Override
    public String toString() {
      return getName();
    }

    public String toPreteritVerb() {
      return preteriteVerb;
    }

    public String toPresentVerb() {
      return presentVerb;
    }

  }
}
