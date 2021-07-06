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

import com.google.inject.Inject;
import com.minecraftonline.griefalert.common.GriefAlert;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvent;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvents;
import com.minecraftonline.griefalert.common.alert.services.AlertService;
import com.minecraftonline.griefalert.common.alert.storage.InspectionStorage;
import com.minecraftonline.griefalert.common.alert.storage.ProfileStorage;
import com.minecraftonline.griefalert.common.data.records.PrismRecordPreSaveEvent;
import com.minecraftonline.griefalert.common.data.services.DataService;
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
import com.minecraftonline.griefalert.sponge.data.struct.SpongeCommonLogger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import lombok.Getter;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.data.DataRegistration;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
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

}
