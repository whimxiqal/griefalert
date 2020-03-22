package com.minecraftonline.griefalert.storage;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.storage.ProfileStorage;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import com.minecraftonline.griefalert.util.Settings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.annotation.Nonnull;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.DimensionTypes;

public class MySqlProfileStorage implements ProfileStorage {

  private static final String TABLE_NAME = "GriefAlertProfiles";
  private final String address;
  private final Properties databaseProperties;

  /**
   * General constructor.
   *
   * @throws SQLException if error with SQL
   */
  public MySqlProfileStorage() throws SQLException {
    address = String.format("jdbc:mysql://%s@%s/%s",
        Settings.STORAGE_USERNAME.getValue(),
        Settings.STORAGE_ADDRESS.getValue(),
        Settings.STORAGE_DATABASE.getValue());
    databaseProperties = new Properties();
    databaseProperties.setProperty("password", Settings.STORAGE_PASSWORD.getValue());
    createTable();
  }

  @Override
  public boolean write(@Nonnull GriefProfile profile) throws SQLException {
    String command = String.format(
        "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) values (?, ?, ?, ?, ?, ?, ?, ?);",
        TABLE_NAME,
        GriefProfileDataQueries.EVENT,
        GriefProfileDataQueries.TARGET,
        "ignore_overworld",
        "ignore_nether",
        "ignore_the_end",
        GriefProfileDataQueries.EVENT_COLOR,
        GriefProfileDataQueries.TARGET_COLOR,
        GriefProfileDataQueries.DIMENSION_COLOR);

    if (exists(profile.getGriefEvent(), profile.getTarget())) {
      return false;
    }

    Connection connection = DriverManager.getConnection(address, databaseProperties);
    PreparedStatement statement = connection.prepareStatement(command);

    statement.setString(1, profile.getGriefEvent().getId());
    statement.setString(2, profile.getTarget());
    statement.setBoolean(3, profile.getDataContainer()
        .getList(GriefProfileDataQueries.IGNORED)
        .map((list) -> list.contains(DimensionTypes.OVERWORLD.getId())).orElse(false));
    statement.setBoolean(4, profile.getDataContainer()
        .getList(GriefProfileDataQueries.IGNORED)
        .map((list) -> list.contains(DimensionTypes.NETHER.getId())).orElse(false));
    statement.setBoolean(5, profile.getDataContainer()
        .getList(GriefProfileDataQueries.IGNORED)
        .map((list) -> list.contains(DimensionTypes.THE_END.getId())).orElse(false));
    statement.setString(6, profile.getDataContainer()
        .getString(GriefProfileDataQueries.EVENT_COLOR).orElse(null));
    statement.setString(7, profile.getDataContainer()
        .getString(GriefProfileDataQueries.TARGET_COLOR).orElse(null));
    statement.setString(8, profile.getDataContainer()
        .getString(GriefProfileDataQueries.DIMENSION_COLOR).orElse(null));

    statement.execute();
    connection.close();
    return true;
  }

  @Override
  public boolean remove(@Nonnull GriefEvent griefEvent, @Nonnull String target)
      throws SQLException {
    if (!exists(griefEvent, target)) {
      return false;
    }

    Connection connection = DriverManager.getConnection(address, databaseProperties);
    String command = "DELETE FROM "
        + TABLE_NAME + " WHERE "
        + GriefProfileDataQueries.EVENT + " = '" + griefEvent.getId() + "' AND "
        + GriefProfileDataQueries.TARGET + " = '" + target + "';";

    connection.prepareStatement(command).execute();
    connection.close();
    return true;
  }

  @Nonnull
  @Override
  public List<GriefProfile> retrieve() throws SQLException {
    Connection connection = DriverManager.getConnection(address, databaseProperties);
    List<GriefProfile> profiles = new LinkedList<>();

    String command = "SELECT * FROM " + TABLE_NAME + ";";
    ResultSet rs = connection.prepareStatement(command).executeQuery();

    while (rs.next()) {
      final DataContainer container = DataContainer.createNew()
          .set(GriefProfileDataQueries.EVENT, rs.getString(1))
          .set(GriefProfileDataQueries.TARGET, rs.getString(2));

      List<String> ignored = new ArrayList<>();
      if (rs.getBoolean(3)) {
        ignored.add(DimensionTypes.OVERWORLD.getId());
      }
      if (rs.getBoolean(4)) {
        ignored.add(DimensionTypes.NETHER.getId());
      }
      if (rs.getBoolean(5)) {
        ignored.add(DimensionTypes.THE_END.getId());
      }
      container.set(GriefProfileDataQueries.IGNORED, ignored);
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
    rs.close();
    connection.close();
    return profiles;

  }

  private void createTable() throws SQLException {
    Connection connection = DriverManager.getConnection(address, databaseProperties);
    String profiles = "CREATE TABLE IF NOT EXISTS "
        + TABLE_NAME + " ("
        + GriefProfileDataQueries.EVENT + " varchar(16) NOT NULL, "
        + GriefProfileDataQueries.TARGET + " varchar(255) NOT NULL, "
        + "ignore_overworld bit NOT NULL, "
        + "ignore_nether bit NOT NULL, "
        + "ignore_the_end bit NOT NULL, "
        + GriefProfileDataQueries.EVENT_COLOR + " varchar(16), "
        + GriefProfileDataQueries.TARGET_COLOR + " varchar(16), "
        + GriefProfileDataQueries.DIMENSION_COLOR + " varchar(16) "
        + ");";
    connection.prepareStatement(profiles).execute();
    connection.close();
  }

  private boolean exists(GriefEvent griefEvent, String target) throws SQLException {
    Connection connection = DriverManager.getConnection(address, databaseProperties);
    String command = "SELECT * FROM "
        + TABLE_NAME + " WHERE "
        + GriefProfileDataQueries.EVENT + " = '" + griefEvent.getId() + "' AND "
        + GriefProfileDataQueries.TARGET + " = '" + target + "';";

    ResultSet rs = connection.prepareStatement(command).executeQuery();
    boolean hasResult = rs.next();

    connection.close();
    return hasResult;

  }

}
