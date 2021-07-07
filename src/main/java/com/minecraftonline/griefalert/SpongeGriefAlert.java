/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.minecraftonline.griefalert.common.GriefAlert;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvent;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvents;
import com.minecraftonline.griefalert.common.alert.services.AlertService;
import com.minecraftonline.griefalert.common.alert.storage.InspectionStorage;
import com.minecraftonline.griefalert.common.alert.storage.ProfileStorage;
import com.minecraftonline.griefalert.common.data.filters.FilterList;
import com.minecraftonline.griefalert.common.data.filters.FilterMode;
import com.minecraftonline.griefalert.common.data.flags.FlagClean;
import com.minecraftonline.griefalert.common.data.flags.FlagDrain;
import com.minecraftonline.griefalert.common.data.flags.FlagExtended;
import com.minecraftonline.griefalert.common.data.flags.FlagHandler;
import com.minecraftonline.griefalert.common.data.flags.FlagNoGroup;
import com.minecraftonline.griefalert.common.data.flags.FlagOrder;
import com.minecraftonline.griefalert.common.data.parameters.ParameterBlock;
import com.minecraftonline.griefalert.common.data.parameters.ParameterCause;
import com.minecraftonline.griefalert.common.data.parameters.ParameterEventName;
import com.minecraftonline.griefalert.common.data.parameters.ParameterHandler;
import com.minecraftonline.griefalert.common.data.parameters.ParameterPlayer;
import com.minecraftonline.griefalert.common.data.parameters.ParameterRadius;
import com.minecraftonline.griefalert.common.data.parameters.ParameterTime;
import com.minecraftonline.griefalert.common.data.records.ActionableResult;
import com.minecraftonline.griefalert.common.data.records.PrismRecordPreSaveEvent;
import com.minecraftonline.griefalert.common.data.services.DataService;
import com.minecraftonline.griefalert.common.data.services.DataServiceImpl;
import com.minecraftonline.griefalert.common.data.storage.StorageAdapter;
import com.minecraftonline.griefalert.common.data.struct.PrismEvent;
import com.minecraftonline.griefalert.sponge.alert.caches.AlertServiceImpl;
import com.minecraftonline.griefalert.sponge.alert.caches.ProfileCache;
import com.minecraftonline.griefalert.sponge.alert.commands.LegacyCommands;
import com.minecraftonline.griefalert.sponge.alert.commands.RootCommand;
import com.minecraftonline.griefalert.sponge.alert.commands.common.LegacyCommand;
import com.minecraftonline.griefalert.sponge.alert.holograms.HologramManager;
import com.minecraftonline.griefalert.sponge.alert.listeners.PrismRecordListener;
import com.minecraftonline.griefalert.sponge.alert.listeners.SpongeListeners;
import com.minecraftonline.griefalert.sponge.alert.storage.ConfigHelper;
import com.minecraftonline.griefalert.sponge.alert.storage.inspections.MySqlInspectionStorage;
import com.minecraftonline.griefalert.sponge.alert.storage.inspections.SqliteInspectionStorage;
import com.minecraftonline.griefalert.sponge.alert.storage.profiles.ProfileStorageJson;
import com.minecraftonline.griefalert.sponge.alert.tool.ImmutableToolGrieferManipulator;
import com.minecraftonline.griefalert.sponge.alert.tool.ImmutableToolManipulator;
import com.minecraftonline.griefalert.sponge.alert.tool.Keys;
import com.minecraftonline.griefalert.sponge.alert.tool.ToolGrieferManipulator;
import com.minecraftonline.griefalert.sponge.alert.tool.ToolHandler;
import com.minecraftonline.griefalert.sponge.alert.tool.ToolManipulator;
import com.minecraftonline.griefalert.sponge.alert.util.General;
import com.minecraftonline.griefalert.sponge.alert.util.Reference;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Settings;
import com.minecraftonline.griefalert.sponge.data.commands.PrismCommands;
import com.minecraftonline.griefalert.sponge.data.configuration.Config;
import com.minecraftonline.griefalert.sponge.data.configuration.Configuration;
import com.minecraftonline.griefalert.sponge.data.listeners.ChangeBlockListener;
import com.minecraftonline.griefalert.sponge.data.listeners.EntityListener;
import com.minecraftonline.griefalert.sponge.data.listeners.InventoryListener;
import com.minecraftonline.griefalert.sponge.data.listeners.RequiredInteractListener;
import com.minecraftonline.griefalert.sponge.data.queues.RecordingQueueManager;
import com.minecraftonline.griefalert.sponge.data.storage.h2.H2StorageAdapter;
import com.minecraftonline.griefalert.sponge.data.storage.mongodb.MongoStorageAdapter;
import com.minecraftonline.griefalert.sponge.data.storage.mysql.MySQLStorageAdapter;
import com.minecraftonline.griefalert.sponge.bridge.SpongeCommonLogger;
import com.minecraftonline.griefalert.sponge.data.util.PrismEvents;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.TypeTokens;

/**
 * The main class for the plugin Grief Alert.
 * This plugin is made for MinecraftOnline.com
 *
 * @author PietElite
 */
@Plugin(id = Reference.ID,
    name = Reference.NAME,
    description = Reference.DESCRIPTION,
    dependencies = {@Dependency(id = "holograms"), @Dependency(id = "worldedit")})
public final class SpongeGriefAlert extends GriefAlert {

  @Getter
  private static SpongeGriefAlert spongeInstance;

  // Injected features directly from Sponge

  @Inject
  @Getter
  private Logger logger;

  @Inject
  @Getter
  private PluginContainer pluginContainer;

  @Inject
  @DefaultConfig(sharedRoot = false)
  @Getter
  private ConfigurationLoader<CommentedConfigurationNode> configManager;

  /**
   * The root node of the configuration file, using the configuration manager.
   */

  private CommentedConfigurationNode rootNode;

  @Inject
  @ConfigDir(sharedRoot = false)
  @Getter private File configDirectory;

  @Inject
  @DefaultConfig(sharedRoot = false)
  @Getter private Path defaultConfig;

  @Inject
  @Getter private PluginContainer container;

  // Services
  @Getter private DataService dataService;
  @Getter private AlertService alertService;

  // Custom classes to help manage plugin
  @Getter private ProfileCache profileCache;
  @Getter private ConfigHelper configHelper;
  @Getter private ProfileStorage profileStorage;
  @Getter private HologramManager hologramManager;
  @Getter private InspectionStorage inspectionStorage;
  @Getter private ToolHandler toolHandler;

  @Listener
  public void onConstruction(GameConstructionEvent event) {
    spongeInstance = this;
    registerCatalogTypes();

    // Common GriefAlert instance information
    this.commonLogger = new SpongeCommonLogger(this.logger);
    // ...

    // OLD PRISM CODE
    configuration = new Configuration(getDefaultConfig());
    Sponge.getRegistry().registerModule(PrismEvent.class, PrismEvents.REGISTRY_MODULE);
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

    // Tool information
    Keys.GA_TOOL = Key.builder()
        .type(TypeTokens.BOOLEAN_VALUE_TOKEN)
        .id("griefalerttool")
        .name("GriefAlert staff tool")
        .query(ToolManipulator.QUERY)
        .build();

    Keys.TOOL_GRIEFER_UUID = Key.builder()
        .type(TypeTokens.UUID_VALUE_TOKEN)
        .id("griefalerttoolgriefer")
        .name("GriefAlert staff tool griefer")
        .query(ToolManipulator.QUERY)
        .build();

    DataRegistration.builder()
        .dataClass(ToolManipulator.class)
        .immutableClass(ImmutableToolManipulator.class)
        .builder(new ToolManipulator.Builder())
        .id("grief-alert-tool")
        .name("GriefAlert staff tool griefer data")
        .build();

    DataRegistration.builder()
        .dataClass(ToolGrieferManipulator.class)
        .immutableClass(ImmutableToolGrieferManipulator.class)
        .builder(new ToolGrieferManipulator.Builder())
        .id("grief-alert-tool-griefer")
        .name("GriefAlert staff tool griefer data")
        .build();

    toolHandler = new ToolHandler();
    Sponge.getEventManager().registerListeners(this, toolHandler);

    try {
      if (Settings.STORAGE_ENGINE.getValue().equalsIgnoreCase("mysql")) {
        SpongeGriefAlert.getSpongeInstance().getLogger().debug("Using MySQL storage engine.");
        inspectionStorage = new MySqlInspectionStorage();
      } else {
        SpongeGriefAlert.getSpongeInstance().getLogger().debug("Using SQLite storage engine.");
        inspectionStorage = new SqliteInspectionStorage();
      }
    } catch (SQLException e) {
      SpongeGriefAlert.getSpongeInstance().getLogger().error(
          "Error while creating storage engine for profiles.");
      e.printStackTrace();
    }


    registerListeners();
  }

  /**
   * A handler for the post initialization event.
   *
   * @param event the event
   */
  @Listener
  public void onPostInitializationEvent(GamePostInitializationEvent event) {
    Sponge.getServiceManager().setProvider(SpongeGriefAlert.getSpongeInstance(),
        AlertService.class,
        new AlertServiceImpl());
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
    try {
      profileStorage = new ProfileStorageJson();
    } catch (Exception e) {
      SpongeGriefAlert.getSpongeInstance().getLogger().error("Error while creating storage engine for profiles.");
      e.printStackTrace();
    }
    profileCache = new ProfileCache();
    alertService = Sponge.getServiceManager().provide(AlertService.class).get();
    dataService = Sponge.getServiceManager().provide(DataService.class).get();
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
   * A handler for a stopping server event.
   *
   * @param event the event
   */
  @Listener
  public void onStoppingServer(GameStoppingServerEvent event) {
    if (alertService instanceof AlertServiceImpl) {
      ((AlertServiceImpl) alertService).saveAlerts();
    }
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
    profileStorage = new ProfileStorageJson();
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

  // === OLD PRISM CODE ===

  private Configuration configuration;
  private StorageAdapter storageAdapter;
  private final Set<UUID> activeWands = Sets.newHashSet();
  private final FilterList filterList = new FilterList(FilterMode.BLACKLIST);
  private final Set<FlagHandler> flagHandlers = Sets.newHashSet();
  private final Map<UUID, List<ActionableResult>> lastActionResults = Maps.newHashMap();
  private final Set<ParameterHandler> parameterHandlers = Sets.newHashSet();
  private final Set<PrismEvent> prismEvents = Sets.newHashSet();
  private final RecordingQueueManager recordingQueueManager = new RecordingQueueManager();

  @Listener
  public void onPreInitialization(GamePreInitializationEvent event) {
    getConfiguration().loadConfiguration();
  }

  @Listener
  public void onInitialization(GameInitializationEvent event) {
    // Register FlagHandlers
    registerFlagHandler(new FlagClean());
    registerFlagHandler(new FlagDrain());
    registerFlagHandler(new FlagExtended());
    registerFlagHandler(new FlagNoGroup());
    registerFlagHandler(new FlagOrder());

    // Register ParameterHandlers
    registerParameterHandler(new ParameterBlock());
    registerParameterHandler(new ParameterCause());
    registerParameterHandler(new ParameterEventName());
    registerParameterHandler(new ParameterPlayer());
    registerParameterHandler(new ParameterRadius());
    registerParameterHandler(new ParameterTime());

    // Register Commands
    Sponge.getCommandManager().register(this, PrismCommands.getCommand(), Reference.ID, "pr");

    // Register Listeners
    Sponge.getEventManager().registerListeners(getPluginContainer(), new ChangeBlockListener());
    Sponge.getEventManager().registerListeners(getPluginContainer(), new EntityListener());
    Sponge.getEventManager().registerListeners(getPluginContainer(), new InventoryListener());

    // Events required for internal operation
    Sponge.getEventManager().registerListeners(getPluginContainer(), new RequiredInteractListener());
  }

  @Listener
  public void onPostInitialization(GamePostInitializationEvent event) {
    getConfiguration().saveConfiguration();
    Sponge.getServiceManager().setProvider(this, DataService.class, new DataServiceImpl());
  }

  @Listener
  public void onStartedServer(GameStartedServerEvent event) {
    String engine = getConfig().getStorageCategory().getEngine();
    try {
      if (StringUtils.equalsIgnoreCase(engine, "h2")) {
        storageAdapter = new H2StorageAdapter();
      } else if (StringUtils.equalsAnyIgnoreCase(engine, "mongo", "mongodb")) {
        storageAdapter = new MongoStorageAdapter();
      } else if (StringUtils.equalsIgnoreCase(engine, "mysql")) {
        storageAdapter = new MySQLStorageAdapter();
      } else {
        throw new Exception("Invalid storage engine configured.");
      }

      Preconditions.checkState(getStorageAdapter().connect());

      // Initialize the recording queue manager
      Task.builder()
          .async()
          .name("PrismRecordingQueueManager")
          .interval(1, TimeUnit.SECONDS)
          .execute(recordingQueueManager)
          .submit(getPluginContainer());
      getLogger().info("Prism started successfully. Bad guys beware.");
    } catch (Exception ex) {
      Sponge.getEventManager().unregisterPluginListeners(getPluginContainer());
      getLogger().error("Encountered an error processing {}::onStartedServer", "Prism", ex);
    }
  }

  @Listener
  public void onStoppedServer(GameStoppedServerEvent event) {
    // Cancel all scheduled tasks
    Sponge.getScheduler().getScheduledTasks(getInstance()).forEach(Task::cancel);

    if (getStorageAdapter() != null) {
      // Flush any pending records
      // If the scheduled task is still running this will block until it completes
      recordingQueueManager.run();

      // Shutdown storage
      getStorageAdapter().close();
    }
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public Config getConfig() {
    Preconditions.checkState(getConfiguration() != null, "Prism has not been initialized!");
    return getConfiguration().getConfig();
  }

  public StorageAdapter getStorageAdapter() {
    return storageAdapter;
  }

  /**
   * Returns a list of players who have active inspection wands.
   *
   * @return A list of players' UUIDs who have an active inspection wand
   */
  public Set<UUID> getActiveWands() {
    return activeWands;
  }

  /**
   * Returns the blacklist manager.
   *
   * @return Blacklist
   */
  public FilterList getFilterList() {
    return filterList;
  }

  /**
   * Returns all currently registered flag handlers.
   *
   * @return List of {@link FlagHandler}
   */
  public Set<FlagHandler> getFlagHandlers() {
    return flagHandlers;
  }

  /**
   * Returns a specific handler for a given parameter
   *
   * @param flag {@link String} flag name
   * @return The {@link FlagHandler}, or empty if unsupported
   */
  public Optional<FlagHandler> getFlagHandler(String flag) {
    for (FlagHandler flagHandler : getFlagHandlers()) {
      if (flagHandler.handles(flag)) {
        return Optional.of(flagHandler);
      }
    }

    return Optional.empty();
  }

  /**
   * Register a flag handler.
   *
   * @param flagHandler {@link FlagHandler}
   * @return True if the {@link FlagHandler} was registered
   */
  public boolean registerFlagHandler(FlagHandler flagHandler) {
    Preconditions.checkNotNull(flagHandler);
    return getFlagHandlers().add(flagHandler);
  }

  /**
   * Get a map of players and their last available actionable results.
   *
   * @return A map of players' UUIDs to a list of their {@link ActionableResult}s
   */
  public Map<UUID, List<ActionableResult>> getLastActionResults() {
    return lastActionResults;
  }

  /**
   * Returns all currently registered parameter handlers.
   *
   * @return List of {@link ParameterHandler}
   */
  public Set<ParameterHandler> getParameterHandlers() {
    return parameterHandlers;
  }

  /**
   * Returns a specific handler for a given parameter
   *
   * @param alias {@link String} parameter name
   * @return The {@link ParameterHandler}, or empty if unsupported
   */
  public Optional<ParameterHandler> getParameterHandler(String alias) {
    for (ParameterHandler parameterHandler : getParameterHandlers()) {
      if (parameterHandler.handles(alias)) {
        return Optional.of(parameterHandler);
      }
    }

    return Optional.empty();
  }

  /**
   * Register a parameter handler.
   *
   * @param parameterHandler {@link ParameterHandler}
   * @return True if the {@link ParameterHandler} was registered
   */
  public boolean registerParameterHandler(ParameterHandler parameterHandler) {
    Preconditions.checkNotNull(parameterHandler);
    return getParameterHandlers().add(parameterHandler);
  }

}
