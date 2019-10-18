package com.minecraftonline.griefalert.storage;

import com.minecraftonline.griefalert.GriefAlert;

import java.io.IOException;
import java.nio.file.Path;

import ninja.leaping.configurate.ConfigurationNode;

public class ConfigHelper {

  private static final int DEFAULT_ALERTS_CODE_LIMIT = 9999;
  private static final int DEFAULT_MAX_REPEATED_HIDDEN_ALERT = 10;
  private static final boolean DEFAULT_SHOW_ALERTS_IN_CONSOLE = true;

  private int cachedEventLimit;
  private int hiddenRepeatedEventLimit;
  private boolean alertEventsToConsole;
  private String sqlUsername;
  private String sqlPassword;
  private String sqlDatabaseAddress;

  /**
   * Create and initialize this helper class for managing the configuration data. This constructor
   * initializes a new file if one does not exist and it loads
   *
   * @param configPath The path to the configuration
   * @param root       The root node of the configuration object
   */
  public ConfigHelper(Path configPath, ConfigurationNode root) {
    initialize(configPath, root);
    load(root);
  }

  /**
   * Initializes the configuration nodes with their appropriate values, designated as
   * local static variables.
   */
  private void initialize(Path configPath, ConfigurationNode root) {
    if (!configPath.toFile().exists()) {
      GriefAlert.getInstance().getLogger().info("No configuration file found. Generating new one...");
      try {
        root.getNode("alertsCodeLimit").setValue(DEFAULT_ALERTS_CODE_LIMIT);
        root.getNode("maxHiddenRepeatedEvents").setValue(DEFAULT_MAX_REPEATED_HIDDEN_ALERT);
        root.getNode("showAlertsInConsole").setValue(DEFAULT_SHOW_ALERTS_IN_CONSOLE);
        GriefAlert.getInstance().getConfigManager().save(root);
        GriefAlert.getInstance().getLogger().info("New configuration file created successfully");
      } catch (IOException e) {
        GriefAlert.getInstance().getLogger().error("Exception while initializing configuration", e);
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
      GriefAlert.getInstance().getConfigManager().load();
      cachedEventLimit = root.getNode("alertsCodeLimit").getInt();
      hiddenRepeatedEventLimit = root.getNode("maxHiddenRepeatedEvents").getInt();
      alertEventsToConsole = root.getNode("showAlertsInConsole").getBoolean();
      GriefAlert.getInstance().getLogger().info("Configuration file loaded");
    } catch (IOException e) {
      GriefAlert.getInstance().getLogger().error("Exception while loading configuration", e);
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
