package com.minecraftonline.griefalert.griefevents.logging;

import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.DIMENSION;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.GRIEFED_ID;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.GRIEFED_TRANSFORM_X;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.GRIEFED_TRANSFORM_Y;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.GRIEFED_TRANSFORM_Z;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.GRIEF_TYPE;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.MC_VERSION;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.PLAYER_ROTATION_PITCH;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.PLAYER_ROTATION_ROLL;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.PLAYER_ROTATION_YAW;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.PLAYER_TRANSFORM_X;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.PLAYER_TRANSFORM_Y;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.PLAYER_TRANSFORM_Z;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.PLAYER_UUID;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.SPECIALTY;
import static com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent.ComponentType.WORLD_ID;

import com.minecraftonline.griefalert.GriefAlert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


/**
 * This class handles remote logging of grief instances to a SQL database.
 */
public final class GriefEventLogger {

  private static final String GRIEF_TABLE_NAME = "GriefEvent_Log";

  /**
   * The main plugin class object.
   */
  private final GriefAlert plugin;
  /**
   * The connection object to reach the SQL database.
   */
  private Connection conn;

  /**
   * True if there exists an error which would prevent complete functionality of this logger.
   */
  private boolean error;

  /**
   * The only constructor for a GriefLogger.
   *
   * @param plugin The main plugin class object
   */
  public GriefEventLogger(GriefAlert plugin) {
    this.plugin = plugin;
    try {
      testConnection();
      prepareTable();
      error = false;
    } catch (SQLException e) {
      error = true;
      this.plugin.getLogger().error("SQL Exception while testing connecting with SQL database.\n"
          + "Is your information correct in the configuration file?");
    }
  }

  /**
   * Test the connection with the SQL database and prepare the table.
   */
  public void reload() {
    plugin.getLogger().info("Reloading Grief Event SQL Logger");
    try {
      testConnection();
      prepareTable();
      error = false;
    } catch (SQLException e) {
      error = true;
      this.plugin.getLogger().error("SQL Exception while testing connecting with SQL database.\n"
          + "Is your information correct in the configuration file?");
    }
  }

  /**
   * Tests the connection with the SQL database.
   *
   * @throws SQLException Throws most likely if SQP configuration nods are
   *                      not set up with the right syntax
   */
  private void testConnection() throws SQLException {
    if (conn == null || conn.isClosed() || !conn.isValid(2)) {
      String connectionPath = "jdbc:mysql://" + plugin.getConfigHelper().getSqlDatabaseAddress();
      conn = DriverManager.getConnection(connectionPath,
          plugin.getConfigHelper().getSqlUsername(),
          plugin.getConfigHelper().getSqlPassword());
    }
  }

  /**
   * Generates non-existing tables into which logs are placed.
   * <li><b>GriefAlert_Log</b>: Contains all grief instances</li>
   * <li><b>GriefAlert_Signs</b>: Contains all information about signs edited by players</li>
   */
  private void prepareTable() {
    try {
      // Generate the Grief Instance table if it doesn't exist
      plugin.getLogger().info("Preparing Grief Event Table ('"
          + GRIEF_TABLE_NAME + "') table and creating if needed...");
      PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS "
          + GRIEF_TABLE_NAME + " "
          + "(id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,"
          + "timestamp_ TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
          + GRIEF_TYPE.name().toLowerCase() + " VARCHAR(10) NOT NULL,"
          + PLAYER_UUID.name().toLowerCase() + " VARCHAR(64) NOT NULL,"
          + PLAYER_ROTATION_PITCH.name().toLowerCase() + " DOUBLE(11,3) NOT NULL,"
          + PLAYER_ROTATION_YAW.name().toLowerCase() + " DOUBLE(11,3) NOT NULL,"
          + PLAYER_ROTATION_ROLL.name().toLowerCase() + " DOUBLE(11,3) NOT NULL,"
          + PLAYER_TRANSFORM_X.name().toLowerCase() + " DOUBLE(11,3) NOT NULL,"
          + PLAYER_TRANSFORM_Y.name().toLowerCase() + " DOUBLE(11,3) NOT NULL,"
          + PLAYER_TRANSFORM_Z.name().toLowerCase() + " DOUBLE(11,3) NOT NULL,"
          + DIMENSION.name().toLowerCase() + " VARCHAR(10) NOT NULL,"
          + WORLD_ID.name().toLowerCase() + " VARCHAR(64) NOT NULL,"
          + GRIEFED_ID.name().toLowerCase() + " VARCHAR(64) NOT NULL,"
          + GRIEFED_TRANSFORM_X.name().toLowerCase() + " DOUBLE(11,3) NOT NULL,"
          + GRIEFED_TRANSFORM_Y.name().toLowerCase() + " DOUBLE(11,3) NOT NULL,"
          + GRIEFED_TRANSFORM_Z.name().toLowerCase() + " DOUBLE(11,3) NOT NULL,"
          + SPECIALTY.name().toLowerCase() + " VARCHAR(100) NOT NULL,"
          + MC_VERSION.name().toLowerCase() + " VARCHAR(8) NOT NULL,"
          + "PRIMARY KEY (id))"
      );
      ps.execute();
      ps.close();
      plugin.getLogger().info("Prepared Grief Alert Log Database");
    } catch (SQLException sqlex) {
      error = true;
      plugin.getLogger().error("SQL Exception while preparing Grief Instance Table ('"
          + GRIEF_TABLE_NAME + "') table...", sqlex);
    }
  }

  /**
   * Logs the given Grief Event to the SQL database, if there is a valid connection with it.
   *
   * @param griefEvent The Grief Event to log
   */
  public void log(LoggedGriefEvent griefEvent) {
    if (error) {
      plugin.getLogger().error("Tried to store a grief instance, but there is an error with the "
          + "connection to the SQL database. Instance: " + griefEvent.toString());
      return;
    }
    plugin.getLogger().debug("Storing Grief Instance data...");
    PreparedStatement ps = null;
    try {
      testConnection();
      List<String> columns = new LinkedList<>();
      List<String> values = new LinkedList<>();
      for (LoggedGriefEvent.ComponentType type : griefEvent.componentMap.keySet()) {
        columns.add(type.name().toLowerCase());
        Object value = griefEvent.componentMap.get(type);
        if (value instanceof String) {
          values.add("\"" + value + "\"");
        } else {
          values.add(value.toString());
        }
      }
      ps = conn.prepareStatement("INSERT INTO "
          + GRIEF_TABLE_NAME
          + " (" + String.join(",", columns) + ") VALUES"
          + " (" + String.join(",", values) + ")");
      ps.execute();
      plugin.getLogger().debug("Store complete!");
    } catch (SQLException sqlex) {
      plugin.getLogger().error("SQL Exception while inserting into Grief Instance ('"
          + GRIEF_TABLE_NAME + "') table...", sqlex);
    } catch (NullPointerException nullex) {
      plugin.getLogger().error("A field within a grief instance cannot be null when being stored",
          nullex);
    } finally {
      closeQuietly(ps);
    }
  }

  /**
   * A helper method to ignore any exceptions thrown when closing an AutoCloseable.
   *
   * @param closeable An AutoCloseable to close
   */
  private static void closeQuietly(AutoCloseable closeable) {
    try {
      if (closeable != null) {
        closeable.close();
      }
    } catch (Exception e) {
      // ignored
    }
  }

}
