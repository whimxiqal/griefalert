package com.minecraftonline.griefalert.storage;

import com.minecraftonline.griefalert.GriefAlert;
import ninja.leaping.configurate.ConfigurationNode;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigHelper {

  private final GriefAlert plugin;
  private Path defaultConfig;

  /** Item used by staff members to 'degrief' a grief event. This is logged but not acted on by in-game staff. */
  private static final String DEFAULT_DEGRIEF_ITEM = "minecraft:stick";
  /** The maximum number of reports before the code limit is reset. This is for ease of use by in-game staff. */
  private static final int DEFAULT_ALERTS_CODE_LIMIT = 9999;
  /** The maximum number of identical reports to hide successively. */
  private static final int DEFAULT_MAX_REPEATED_HIDDEN_ALERT = 10;
  /** Will the alerts be shown in the console as well as in game? */
  public static final boolean DEFAULT_SHOW_ALERTS_IN_CONSOLE = true;

  private static final String DEFAULT_STAFF_ALERT_MESSAGE =
      "(PLAYER) (GRIEF_VERB) a (GRIEF_OBJECT) ((GRIEF_ID)) in the (LOCATION:DIMENSION)";

  private static final String DEFAULT_STAFF_GRECENT_MESSAGE =
      "(GRIEF_VERB): (GRIEF_OBJECT) in (LOCATION:DIMENSION)";

  private static final String DEFAULT_STAFF_ALERT_MESSAGE_SIGN_HEADER =
      "Sign placed by (PLAYER) at (LOCATION:COORDINATES) in the (LOCATION:DIMENSION)";

  private static final String DEFAULT_STAFF_ALERT_MESSAGE_SIGN_LINE =
      "Line (SIGN_LINE_NUMBER): (SIGN_LINE_CONTENT)";

  private static final String DEFAULT_SQL_USERNAME = "user";
  private static final String DEFAULT_SQL_PASSWORD = "PA$$word";
  private static final String DEFAULT_SQL_ADDRESS = "localhost:3306/minecraft";


  public ConfigHelper(GriefAlert plugin, Path defaultConfig) {
    this.plugin = plugin;
    this.defaultConfig = defaultConfig;



  }

  /**
   * Initializes the configuration nodes with their appropriate values, designated as
   * local static variables.
   */
  public void initializeConfig() {
    if (!defaultConfig.toFile().exists()) {
      plugin.getLogger().info("Generating new Configuration File...");
      try {
        plugin.setRootNode(plugin.getConfigManager().load());
        plugin.getRootNode().getNode("degriefStickID").setValue(DEFAULT_DEGRIEF_ITEM);
        plugin.getRootNode().getNode("alertsCodeLimit").setValue(DEFAULT_ALERTS_CODE_LIMIT);
        plugin.getRootNode().getNode("maxHiddenMatchingAlerts").setValue(DEFAULT_MAX_REPEATED_HIDDEN_ALERT);
        plugin.getRootNode().getNode("showAlertsInConsole").setValue(DEFAULT_SHOW_ALERTS_IN_CONSOLE);
        plugin.getRootNode().getNode("SQLusername").setValue(DEFAULT_SQL_USERNAME);
        plugin.getRootNode().getNode("SQLpassword").setValue(DEFAULT_SQL_PASSWORD);
        plugin.getRootNode().getNode("SQLdb").setValue(DEFAULT_SQL_ADDRESS);
        ConfigurationNode messagingNode = plugin.getRootNode().getNode("messaging");
        messagingNode.getNode("staff_alert_message").setValue(DEFAULT_STAFF_ALERT_MESSAGE);
        messagingNode.getNode("staff_grecent_message").setValue(DEFAULT_STAFF_GRECENT_MESSAGE);
        messagingNode.getNode("staff_alert_message_sign_header").setValue(DEFAULT_STAFF_ALERT_MESSAGE_SIGN_HEADER);
        messagingNode.getNode("staff_alert_message_sign_line").setValue(DEFAULT_STAFF_ALERT_MESSAGE_SIGN_LINE);
        plugin.getConfigManager().save(plugin.getRootNode());
        plugin.getLogger().info("New Configuration File created successfully!");
      } catch (IOException e) {
        plugin.getLogger().warn("Exception while reading configuration", e);
      }
    } else {
      try {
        plugin.setRootNode(plugin.getConfigManager().load());
      } catch (IOException e) {
        plugin.getLogger().warn("Exception while reading configuration", e);
      }
    }
  }

}
