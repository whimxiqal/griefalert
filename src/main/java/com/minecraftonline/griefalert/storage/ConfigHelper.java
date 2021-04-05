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

package com.minecraftonline.griefalert.storage;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.configuration.Setting;
import com.minecraftonline.griefalert.util.enums.Settings;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class ConfigHelper {

  private Collection<Setting<?>> allSettings() {
    List<Setting<?>> settings = new LinkedList<>();
    Arrays.stream(Settings.class.getDeclaredFields())
        .filter(field -> Modifier.isStatic(field.getModifiers()))
        .filter(field -> Setting.class.isAssignableFrom(field.getType()))
        .forEach(field -> {
          try {
            settings.add((Setting<?>) field.get(null));
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        });
    return settings;
  }

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
        allSettings().forEach(
            setting -> {
              root.getNode(setting.getName()).setComment(setting.getComment());
              root.getNode(setting.getName()).setValue(setting.getDefaultValue());
            });
        GriefAlert.getInstance().getConfigManager().save(root);
        GriefAlert.getInstance().getLogger().info("New configuration file created successfully");
      } catch (IOException e) {
        e.printStackTrace();
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
  public void load(CommentedConfigurationNode root) {
    try {
      GriefAlert.getInstance().getConfigManager().load();
      for (Setting<?> setting : allSettings()) {
        try {
          setting.setValueFromConfig(root);
        } catch (RuntimeException | ObjectMappingException e1) {
          root.getNode(setting.getName()).setComment(setting.getComment());
          root.getNode(setting.getName()).setValue(setting.getDefaultValue());
          GriefAlert.getInstance().getLogger().error(e1.getMessage());
        }
      }
      GriefAlert.getInstance().getLogger().info("Configuration file loaded");
    } catch (Exception e2) {
      e2.printStackTrace();
      return;
    }

    try {
      GriefAlert.getInstance().getConfigManager().save(root);
    } catch (IOException e) {
      e.printStackTrace();
      GriefAlert.getInstance().getLogger().error("Exception while saving configuration after load", e);
    }
  }

}
