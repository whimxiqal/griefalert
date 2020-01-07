/* Created by PietElite */

package com.minecraftonline.griefalert;

import static com.minecraftonline.griefalert.GriefAlert.VERSION;

import com.google.inject.Inject;
import com.helion3.prism.api.records.PrismRecordPreSaveEvent;
import com.minecraftonline.griefalert.api.caches.ProfileCache;
import com.minecraftonline.griefalert.api.caches.RotatingAlertList;
import com.minecraftonline.griefalert.api.commands.ReplacedCommand;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.storage.ProfileStorage;
import com.minecraftonline.griefalert.commands.DeprecatedCommands;
import com.minecraftonline.griefalert.commands.GriefAlertCommand;
import com.minecraftonline.griefalert.listeners.PrismRecordListener;
import com.minecraftonline.griefalert.listeners.SpongeListeners;
import com.minecraftonline.griefalert.storage.ConfigHelper;
import com.minecraftonline.griefalert.storage.MySqlProfileStorage;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.GriefEvents;
import com.minecraftonline.griefalert.util.Reference;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

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
 * Do not use this plugin without explicit approval from the administration team at MinecraftOnline.
 *
 * @author PietElite
 */
@Plugin(id = Reference.ID,
    name = Reference.NAME,
    version = VERSION,
    description = Reference.DESCRIPTION,
    dependencies = {@Dependency(id = "prism"), @Dependency(id = "worldedit")})
public final class GriefAlert {

  public static final String VERSION = Reference.VERSION;
  private static GriefAlert instance;

  // Injected features directly from Sponge

  @Inject
  @SuppressWarnings("UnusedDeclaration")
  private Logger logger;

  @Inject
  @SuppressWarnings("UnusedDeclaration")
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
  private ProfileCache profileCache;
  private RotatingAlertList rotatingAlertList;
  private ConfigHelper configHelper;
  private MySqlProfileStorage profileStorage;

  @Listener
  public void onConstruction(GameConstructionEvent event) {
    instance = this;
    registerCatalogTypes();
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

    // Load the config from the Sponge API and set the specific node values.
    try {
      rootNode = configManager.load();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Set helper manager classes
    configHelper = new ConfigHelper(defaultConfig, rootNode);
    if (getDataDirectory().mkdirs()) {
      getLogger().info("Data directory created.");
    }

    try {
      profileStorage = new MySqlProfileStorage();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    profileCache = new ProfileCache();
    rotatingAlertList = new RotatingAlertList(configHelper.getCachedEventLimit());

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

  /**
   * Reload the entire plugin and its data.
   */
  public void reload() {
    getLogger().info("Reloading plugin");
    try {
      rootNode = configManager.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    configHelper.load(rootNode);
    profileCache.reload();
  }


  private void registerCommands() {
    GriefAlertCommand griefAlertCommand = new GriefAlertCommand();
    Sponge.getCommandManager().register(
        this,
        griefAlertCommand.buildCommandSpec(),
        griefAlertCommand.getAliases());
    for (ReplacedCommand command : DeprecatedCommands.get()) {
      Sponge.getCommandManager().register(
          this,
          command.buildCommandSpec(),
          command.getAliases());
    }
  }

  private void registerListeners() {
    Sponge.getEventManager().registerListener(
        this,
        PrismRecordPreSaveEvent.class,
        new PrismRecordListener()
    );
    SpongeListeners.register(this);
  }

  private void registerCatalogTypes() {
    Sponge.getRegistry().registerModule(GriefEvent.class, GriefEvents.REGISTRY_MODULE);
  }

  public File getDataDirectory() {
    return new File(configDirectory.getParentFile().getParentFile().getPath() + "/" + "griefalert");
  }

  public ProfileCache getProfileCache() {
    return profileCache;
  }

  public ConfigurationLoader<CommentedConfigurationNode> getConfigManager() {
    return configManager;
  }

  public RotatingAlertList getRotatingAlertList() {
    return rotatingAlertList;
  }

  public ConfigHelper getConfigHelper() {
    return configHelper;
  }

  public ProfileStorage getProfileStorage() {
    return profileStorage;
  }

  public Logger getLogger() {
    return logger;
  }

  public static GriefAlert getInstance() {
    return instance;
  }

  public PluginContainer getPluginContainer() {
    return this.pluginContainer;
  }
}
