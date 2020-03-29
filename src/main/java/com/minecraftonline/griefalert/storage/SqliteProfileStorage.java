/* Created by PietElite */

package com.minecraftonline.griefalert.storage;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.storage.ProfileStorage;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import com.minecraftonline.griefalert.util.enums.GriefEvents;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.DimensionTypes;

/**
 * Implementation of persistent storage for {@link GriefProfile}s using MySQL.
 *
 * @author PietElite
 */
public class SqliteProfileStorage implements ProfileStorage {

  private static final String TABLE_NAME = "GriefAlertProfiles";
  private final String address;
  private Connection connection;

  /**
   * General constructor.
   *
   * @throws SQLException if error with SQL
   */
  public SqliteProfileStorage() throws SQLException {
    address = "jdbc:sqlite:" + GriefAlert.getInstance().getDataDirectory()
        .getPath() + "/griefalert.db";
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

    connect();
    PreparedStatement statement = getConnection().prepareStatement(command);

    statement.setString(1,
        profile.getGriefEvent().getId());
    statement.setString(2,
        profile.getTarget());
    statement.setBoolean(3,
        profile.isIgnoredIn(DimensionTypes.OVERWORLD));
    statement.setBoolean(4,
        profile.isIgnoredIn(DimensionTypes.NETHER));
    statement.setBoolean(5,
        profile.isIgnoredIn(DimensionTypes.THE_END));
    statement.setString(6,
        profile.getColored(GriefProfile.Colored.EVENT)
            .map(TextColor::getName)
            .orElse(null));
    statement.setString(7,
        profile.getColored(GriefProfile.Colored.TARGET)
            .map(TextColor::getName)
            .orElse(null));
    statement.setString(8,
        profile.getColored(GriefProfile.Colored.DIMENSION)
            .map(TextColor::getName)
            .orElse(null));

    statement.execute();
    close();
    return true;
  }

  @Override
  public boolean remove(@Nonnull GriefEvent griefEvent, @Nonnull String target)
      throws SQLException {
    if (!exists(griefEvent, target)) {
      return false;
    }

    connect();
    String command = "DELETE FROM "
        + TABLE_NAME + " WHERE "
        + GriefProfileDataQueries.EVENT + " = '" + griefEvent.getId() + "' AND "
        + GriefProfileDataQueries.TARGET + " = '" + target + "';";

    getConnection().prepareStatement(command).execute();
    close();
    return true;
  }

  @Nonnull
  @Override
  public List<GriefProfile> retrieve() throws SQLException {
    connect();
    List<GriefProfile> profiles = new LinkedList<>();

    String command = "SELECT * FROM " + TABLE_NAME + ";";
    ResultSet rs = connection.prepareStatement(command).executeQuery();

    while (rs.next()) {
      GriefEvent event = GriefEvents.REGISTRY_MODULE
          .getById(rs.getString(1))
          .orElseThrow(() -> new RuntimeException(
              "Saved GriefEvent ID in MySQL database does not match any GriefEvent."));

      final GriefProfile profile = GriefProfile.of(event, rs.getString(2));

      if (rs.getBoolean(3)) {
        profile.addIgnored(DimensionTypes.OVERWORLD);
      }
      if (rs.getBoolean(4)) {
        profile.addIgnored(DimensionTypes.NETHER);
      }
      if (rs.getBoolean(5)) {
        profile.addIgnored(DimensionTypes.THE_END);
      }

      Sponge.getRegistry()
          .getType(
              TextColor.class,
              Optional.ofNullable(rs.getString(6)).orElse(""))
          .ifPresent(color -> profile.putColored(GriefProfile.Colored.EVENT, color));

      Sponge.getRegistry()
          .getType(
              TextColor.class,
              Optional.ofNullable(rs.getString(7)).orElse(""))
          .ifPresent(color -> profile.putColored(GriefProfile.Colored.TARGET, color));

      Sponge.getRegistry()
          .getType(
              TextColor.class,
              Optional.ofNullable(rs.getString(8)).orElse(""))
          .ifPresent(color -> profile.putColored(GriefProfile.Colored.DIMENSION, color));

      profiles.add(profile);
    }
    close();
    return profiles;

  }

  private void connect() throws SQLException {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    connection = DriverManager.getConnection(address);
  }

  private void close() throws SQLException {
    getConnection().close();
  }

  private void createTable() throws SQLException {
    connect();
    String profiles = "CREATE TABLE IF NOT EXISTS "
        + TABLE_NAME + " ("
        + "event varchar(16) NOT NULL, "
        + "target varchar(255) NOT NULL, "
        + "ignore_overworld bit NOT NULL, "
        + "ignore_nether bit NOT NULL, "
        + "ignore_the_end bit NOT NULL, "
        + "event_color varchar(16), "
        + "target_color varchar(16), "
        + "dimension_color varchar(16) "
        + ");";
    getConnection().prepareStatement(profiles).execute();
    close();
  }

  private boolean exists(GriefEvent griefEvent, String target) throws SQLException {
    connect();
    String command = "SELECT * FROM "
        + TABLE_NAME + " WHERE "
        + GriefProfileDataQueries.EVENT + " = '" + griefEvent.getId() + "' AND "
        + GriefProfileDataQueries.TARGET + " = '" + target + "';";

    ResultSet rs = getConnection().prepareStatement(command).executeQuery();
    boolean hasResult = rs.next();

    close();
    return hasResult;

  }

  private Connection getConnection() {
    return connection;
  }

}
