package com.minecraftonline.griefalert.storage;

import com.minecraftonline.griefalert.GriefAlert;
import ninja.leaping.configurate.ConfigurationNode;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigHelper {

  private final GriefAlert plugin;
  private Path configPath;

  /**
   * The maximum number of reports before the code limit is reset. This is for ease of use by in-game staff.
   */
  private static final int DEFAULT_ALERTS_CODE_LIMIT = 9999;
  /**
   * The maximum number of identical reports to hide successively.
   */
  private static final int DEFAULT_MAX_REPEATED_HIDDEN_ALERT = 10;
  /**
   * Will the alerts be shown in the console as well as in game?
   */
  private static final boolean DEFAULT_SHOW_ALERTS_IN_CONSOLE = true;

  private static final String DEFAULT_SQL_USERNAME = "user";
  private static final String DEFAULT_SQL_PASSWORD = "PA$$word";
  private static final String DEFAULT_SQL_ADDRESS = "localhost:3306/minecraft";

  private int cachedEventLimit;
  private int hiddenRepeatedEventLimit;
  private boolean alertEventsToConsole;
  private String sqlUsername;
  private String sqlPassword;
  private String sqlDatabaseAddress;

  /**
   * Create and initialize this helper class for managing the configuration data. This constructor
   * initializes a new file if one does not exist and it loads
   * @param plugin
   * @param configPath
   * @param root
   */
  public ConfigHelper(GriefAlert plugin, Path configPath, ConfigurationNode root) {
    this.plugin = plugin;
    this.configPath = configPath;
    initialize(plugin, configPath, root);
    load(root);
  }

  /**
   * Initializes the configuration nodes with their appropriate values, designated as
   * local static variables.
   */
  private void initialize(GriefAlert plugin, Path configPath, ConfigurationNode root) {
    if (!configPath.toFile().exists()) {
      plugin.getLogger().info("No configuration file found. Generating new one...");
      try {
        plugin.setRootNode(plugin.getConfigManager().load());
        root.getNode("alertsCodeLimit").setValue(DEFAULT_ALERTS_CODE_LIMIT);
        root.getNode("maxHiddenRepeatedEvents").setValue(DEFAULT_MAX_REPEATED_HIDDEN_ALERT);
        root.getNode("showAlertsInConsole").setValue(DEFAULT_SHOW_ALERTS_IN_CONSOLE);
        root.getNode("SQLusername").setValue(DEFAULT_SQL_USERNAME);
        root.getNode("SQLpassword").setValue(DEFAULT_SQL_PASSWORD);
        root.getNode("SQLdb").setValue(DEFAULT_SQL_ADDRESS);
        plugin.getConfigManager().save(root);
        plugin.getLogger().info("New configuration file created successfully");
      } catch (IOException e) {
        plugin.getLogger().error("Exception while initializing configuration", e);
      }
    }
  }

  /**
   * Load the configuration file into the plugin to account for any deviations from the
   * default settings.
   *
   * @param root The root node for the configuration values in the file
   */
  public void load(ConfigurationNode root) {
    try {
      plugin.getConfigManager().load();
      cachedEventLimit = root.getNode("alertsCodeLimit").getInt();
      hiddenRepeatedEventLimit = root.getNode("maxHiddenRepeatedEvents").getInt();
      alertEventsToConsole = root.getNode("showAlertsInConsole").getBoolean();
      sqlUsername = root.getNode("SQLusername").getString();
      sqlPassword = root.getNode("SQLpassword").getString();
      sqlDatabaseAddress = root.getNode("SQLdb").getString();
      plugin.getLogger().info("Configuration file loaded");
    } catch (IOException e) {
      plugin.getLogger().error("Exception while loading configuration", e);
    }
  }

  public int getCachedEventLimit() {
    return cachedEventLimit;
  }

  public int getHiddenRepeatedEventLimit() {
    return hiddenRepeatedEventLimit;
  }

  public String getSqlDatabaseAddress() {
    return sqlDatabaseAddress;
  }

  public String getSqlPassword() {
    return sqlPassword;
  }

  public String getSqlUsername() {
    return sqlUsername;
  }

  public boolean isAlertEventsToConsole() {
    return alertEventsToConsole;
  }
}
