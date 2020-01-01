package com.minecraftonline.griefalert;

import static com.minecraftonline.griefalert.GriefAlert.VERSION;

import com.google.inject.Inject;
import com.helion3.prism.api.records.PrismRecordPreSaveEvent;
import com.minecraftonline.griefalert.alerts.AlertStack;
import com.minecraftonline.griefalert.commands.GriefAlertCommand;
import com.minecraftonline.griefalert.listeners.ExtraListeners;
import com.minecraftonline.griefalert.listeners.PrismRecordListener;
import com.minecraftonline.griefalert.profiles.ProfileCabinet;
import com.minecraftonline.griefalert.storage.ConfigHelper;
import com.minecraftonline.griefalert.storage.MySQLProfileStorage;
import com.minecraftonline.griefalert.util.General;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

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

  public static final String VERSION = "24.0";
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
  private ProfileCabinet cabinet;
  private AlertStack alertQueue;
  private ConfigHelper configHelper;
  private MySQLProfileStorage profileStorage;

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
    configHelper = new ConfigHelper(defaultConfig, rootNode);
    cabinet = new ProfileCabinet();
    alertQueue = new AlertStack(configHelper.getCachedEventLimit());
    profileStorage = new MySQLProfileStorage();

    // Register all the commands with Sponge
    registerCommands();
    registerListeners();

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
    cabinet.reload();  // Must reload grief event logger after config
  }


  private void registerCommands() {
    GriefAlertCommand griefAlertCommand = new GriefAlertCommand();
    Sponge.getCommandManager().register(
        this,
        griefAlertCommand.buildCommandSpec(),
        griefAlertCommand.getAliases());
  }

  private void registerListeners() {
    Sponge.getEventManager().registerListener(
        this,
        PrismRecordPreSaveEvent.class,
        new PrismRecordListener()
    );
    ExtraListeners.register(this);
  }

  public File getDataDirectory() {
    return new File(configDirectory.getParentFile().getParentFile().getPath() + "/" + "griefalert");
  }

  public ProfileCabinet getProfileCabinet() {
    return cabinet;
  }

  public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
    return configManager;
  }

  public AlertStack getAlertQueue() {
    return alertQueue;
  }

  public ConfigHelper getConfigHelper() {
    return configHelper;
  }

  public MySQLProfileStorage getProfileStorage() {
    return profileStorage;
  }

  public Logger getLogger() {
    return logger;
  }

  public static GriefAlert getInstance() {
    return instance;
  }

}
