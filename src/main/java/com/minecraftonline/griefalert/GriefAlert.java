/* Created by PietElite */

package com.minecraftonline.griefalert;

import static com.minecraftonline.griefalert.GriefAlert.VERSION;

import com.google.inject.Inject;
import com.helion3.prism.api.records.PrismRecordPreSaveEvent;
import com.minecraftonline.griefalert.api.caches.AlertManager;
import com.minecraftonline.griefalert.api.caches.ProfileCache;
import com.minecraftonline.griefalert.api.commands.LegacyCommand;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.storage.ProfileStorage;
import com.minecraftonline.griefalert.commands.LegacyCommands;
import com.minecraftonline.griefalert.commands.RootCommand;
import com.minecraftonline.griefalert.holograms.HologramManager;
import com.minecraftonline.griefalert.listeners.PrismRecordListener;
import com.minecraftonline.griefalert.listeners.SpongeListeners;
import com.minecraftonline.griefalert.storage.ConfigHelper;
import com.minecraftonline.griefalert.storage.MySqlProfileStorage;
import com.minecraftonline.griefalert.storage.SqliteProfileStorage;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.Reference;
import com.minecraftonline.griefalert.util.enums.GriefEvents;
import com.minecraftonline.griefalert.util.enums.Settings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
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
    dependencies = {@Dependency(id = "prism"),
        @Dependency(id = "holograms"),
        @Dependency(id = "worldedit")})
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

  private CommentedConfigurationNode rootNode;

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
  private AlertManager alertManager;
  private ConfigHelper configHelper;
  private ProfileStorage profileStorage;
  private HologramManager hologramManager;

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
      if (Settings.STORAGE_ENGINE.getValue().equalsIgnoreCase("mysql")) {
        GriefAlert.getInstance().getLogger().debug("Using MySQL storage engine.");
        profileStorage = new MySqlProfileStorage();
      } else {
        GriefAlert.getInstance().getLogger().debug("Using SQLite storage engine.");
        profileStorage = new SqliteProfileStorage();
      }
    } catch (SQLException e) {
      GriefAlert.getInstance().getLogger().error(
          "Error while creating storage engine for profiles.");
      e.printStackTrace();
      // TODO add enable/disable feature for entire plugin
    }


    registerListeners();
  }

  @Listener
  public void onLoadComplete(GameLoadCompleteEvent event) {
    hologramManager = new HologramManager();
  }

  /**
   * Listener for {@link GameStartingServerEvent}.
   *
   * @param event the event
   */
  @Listener
  public void onStartingServer(GameStartingServerEvent event) {
    // Register all the commands with Sponge
    registerCommands();
    alertManager = new AlertManager();
    profileCache = new ProfileCache();
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

  @Listener
  public void onStoppingServer(GameStoppingServerEvent event) {
    getAlertManager().saveAlerts();
    getHologramManager().deleteAllHolograms();
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
    RootCommand rootCommand = new RootCommand();
    Sponge.getCommandManager().register(
        this,
        rootCommand.buildCommandSpec(),
        rootCommand.getAliases());
    for (LegacyCommand command : LegacyCommands.get()) {
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

  public AlertManager getAlertManager() {
    return alertManager;
  }

  public ConfigHelper getConfigHelper() {
    return configHelper;
  }

  public ProfileStorage getProfileStorage() {
    return profileStorage;
  }

  public HologramManager getHologramManager() {
    return hologramManager;
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
