package com.minecraftonline.griefalert.storage;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import org.spongepowered.api.data.DataContainer;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class MySqlProfileStorage {

  private static final String TABLE_NAME = "GriefAlertProfiles";
  private Connection connection;

  /**
   * Create a connection with the SQL database.
   *
   * @throws SQLException if error through SQL
   */
  public void connect() throws SQLException {
    connect("jdbc:sqlite:" + GriefAlert.getInstance().getDataDirectory().getPath() + "/griefalert.db");
  }

  public void connect(String address) throws SQLException {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    connection = DriverManager.getConnection(address);
    createTable();
  }

  /**
   * Close the connection with the SQL database.
   *
   * @return true if the close was successful
   * @throws SQLException if error through SQL
   */
  public boolean close() throws SQLException {
    if (getConnection() == null || getConnection().isClosed()) {
      return false;
    }
    getConnection().close();
    return getConnection().isClosed();
  }

  private void createTable() throws SQLException {
    String profiles = "CREATE TABLE IF NOT EXISTS "
        + TABLE_NAME + " ("
        + GriefProfileDataQueries.EVENT + " varchar(16) NOT NULL, "
        + GriefProfileDataQueries.TARGET + " varchar(255) NOT NULL, "
        + GriefProfileDataQueries.IGNORE_OVERWORLD + " bit NOT NULL, "
        + GriefProfileDataQueries.IGNORE_NETHER + " bit NOT NULL, "
        + GriefProfileDataQueries.IGNORE_THE_END + " bit NOT NULL, "
        + GriefProfileDataQueries.EVENT_COLOR + " varchar(16), "
        + GriefProfileDataQueries.TARGET_COLOR + " varchar(16), "
        + GriefProfileDataQueries.DIMENSION_COLOR + " varchar(16) "
        + ");";
    getConnection().prepareStatement(profiles).execute();
  }

  /**
   * Write a GriefProfile into the database. Requires connection.
   *
   * @param profile The GriefProfile to add
   * @return false if a grief profile already existed with the same target and grief event
   * @throws SQLException if error through SQL
   */
  public boolean write(GriefProfile profile) throws SQLException {
    String command = String.format(
        "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) values (?, ?, ?, ?, ?, ?, ?, ?);",
        TABLE_NAME,
        GriefProfileDataQueries.EVENT,
        GriefProfileDataQueries.TARGET,
        GriefProfileDataQueries.IGNORE_OVERWORLD,
        GriefProfileDataQueries.IGNORE_NETHER,
        GriefProfileDataQueries.IGNORE_THE_END,
        GriefProfileDataQueries.EVENT_COLOR,
        GriefProfileDataQueries.TARGET_COLOR,
        GriefProfileDataQueries.DIMENSION_COLOR);

    if (exists(profile.getGriefEvent(), profile.getTarget())) {
      return false;
    }

    PreparedStatement statement = getConnection().prepareStatement(command);

    statement.setString(1, profile.getGriefEvent().getId());
    statement.setString(2, profile.getTarget());
    statement.setBoolean(3, profile.getDataContainer()
        .getBoolean(GriefProfileDataQueries.IGNORE_OVERWORLD).orElse(false));
    statement.setBoolean(4, profile.getDataContainer()
        .getBoolean(GriefProfileDataQueries.IGNORE_NETHER).orElse(false));
    statement.setBoolean(5, profile.getDataContainer()
        .getBoolean(GriefProfileDataQueries.IGNORE_THE_END).orElse(false));
    statement.setString(6, profile.getDataContainer()
        .getString(GriefProfileDataQueries.EVENT_COLOR).orElse(null));
    statement.setString(7, profile.getDataContainer()
        .getString(GriefProfileDataQueries.TARGET_COLOR).orElse(null));
    statement.setString(8, profile.getDataContainer()
        .getString(GriefProfileDataQueries.DIMENSION_COLOR).orElse(null));

    statement.execute();

    return true;
  }

  private boolean exists(GriefEvent griefEvent, String target) throws SQLException {

    String command = "SELECT * FROM "
        + TABLE_NAME + " WHERE "
        + GriefProfileDataQueries.EVENT + " = '" + griefEvent.getId() + "' AND "
        + GriefProfileDataQueries.TARGET + " = '" + target + "';";

    ResultSet rs = getConnection().prepareStatement(command).executeQuery();

    return rs.next();

  }

  /**
   * Remove the GriefProfile with the given parameters. Requires connection.
   *
   * @param griefEvent The grief event of this profile to remove
   * @param target     The target of this profile to remove
   * @return false if a grief profile was not found
   * @throws SQLException if error through SQL
   */
  public boolean remove(GriefEvent griefEvent, String target) throws SQLException {
    if (!exists(griefEvent, target)) {
      return false;
    }

    String command = "DELETE FROM "
        + TABLE_NAME + " WHERE "
        + GriefProfileDataQueries.EVENT + " = '" + griefEvent.getId() + "' AND "
        + GriefProfileDataQueries.TARGET + " = '" + target + "';";

    getConnection().prepareStatement(command).execute();

    return true;
  }

  /**
   * Get all <code>GriefProfile</code>s saved in the database. Required connection.
   *
   * @return a list of grief profiles
   * @throws SQLException if error through SQL
   */
  public List<GriefProfile> retrieve() throws SQLException {
    List<GriefProfile> profiles = new LinkedList<>();

    String command = "SELECT * FROM " + TABLE_NAME + ";";
    ResultSet rs = connection.prepareStatement(command).executeQuery();

    while (rs.next()) {
      DataContainer container = DataContainer.createNew()
          .set(GriefProfileDataQueries.EVENT, rs.getString(1))
          .set(GriefProfileDataQueries.TARGET, rs.getString(2))
          .set(GriefProfileDataQueries.IGNORE_OVERWORLD, rs.getBoolean(3))
          .set(GriefProfileDataQueries.IGNORE_NETHER, rs.getBoolean(4))
          .set(GriefProfileDataQueries.IGNORE_THE_END, rs.getBoolean(5));
      if (rs.getString(6) != null) {
        container.set(GriefProfileDataQueries.EVENT_COLOR, rs.getString(6));
      }
      if (rs.getString(7) != null) {
        container.set(GriefProfileDataQueries.TARGET_COLOR, rs.getString(7));
      }
      if (rs.getString(8) != null) {
        container.set(GriefProfileDataQueries.DIMENSION_COLOR, rs.getString(8));
      }
      profiles.add(GriefProfile.of(container));
    }

    return profiles;

  }

  private Connection getConnection() {
    return connection;
  }

}
