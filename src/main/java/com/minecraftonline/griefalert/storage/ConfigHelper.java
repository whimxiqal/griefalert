/* Created by PietElite */

package com.minecraftonline.griefalert.storage;

import com.google.common.collect.ImmutableList;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.configuration.Setting;
import com.minecraftonline.griefalert.util.enums.Settings;

import java.io.IOException;
import java.nio.file.Path;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class ConfigHelper {

  private final ImmutableList<Setting<?>> settings = ImmutableList.of(
      Settings.ALERTS_CODE_LIMIT,
      Settings.MAX_HIDDEN_REPEATED_EVENTS,
      Settings.SHOW_ALERTS_IN_CONSOLE,
      Settings.CHECK_INVULNERABILITY,
      Settings.DATE_FORMAT,
      Settings.STORAGE_ENGINE,
      Settings.STORAGE_ADDRESS,
      Settings.STORAGE_DATABASE,
      Settings.STORAGE_USERNAME,
      Settings.STORAGE_PASSWORD);

  /**
   * Create and initialize this helper class for managing the configuration data. This constructor
   * initializes a new file if one does not exist and it loads
   *
   * @param configPath The path to the configuration
   * @param root       The root node of the configuration object
   */
  public ConfigHelper(Path configPath, CommentedConfigurationNode root) {
    initialize(configPath, root);
    load(root);
  }

  /**
   * Initializes the configuration nodes with their appropriate values, designated as
   * local static variables.
   */
  private void initialize(Path configPath, CommentedConfigurationNode root) {
    if (!configPath.toFile().exists()) {
      GriefAlert.getInstance().getLogger().info(
          "No configuration file found. Generating new one...");
      try {
        settings.forEach(
            setting -> {
              root.getNode(setting.getName()).setComment(setting.getComment());
              root.getNode(setting.getName()).setValue(setting.getDefaultValue());
            });
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
      for (Setting<?> setting : settings) {
        try {
          setting.setValueFromConfig(root);
        } catch (IllegalArgumentException e) {
          GriefAlert.getInstance().getLogger().error(e.getMessage());
        }
      }
      GriefAlert.getInstance().getLogger().info("Configuration file loaded");
    } catch (Exception e) {
      GriefAlert.getInstance().getLogger().error("Exception while loading configuration", e);
    }
  }

}
