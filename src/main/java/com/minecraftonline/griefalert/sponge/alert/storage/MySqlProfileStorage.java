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

package com.minecraftonline.griefalert.sponge.alert.storage;

import com.minecraftonline.griefalert.common.alert.struct.GriefEvent;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvents;
import com.minecraftonline.griefalert.common.alert.records.GriefProfile;
import com.minecraftonline.griefalert.common.alert.storage.ProfileStorage;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Settings;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.format.TextColor;

/**
 * Deprecated ProfileStorage implementation.
 *
 * @author PietElite
 */
// TODO implement
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
    address = String.format("jdbc:mysql://%s/%s",
        Settings.STORAGE_ADDRESS.getValue(),
        Settings.STORAGE_DATABASE.getValue());
    databaseProperties = new Properties();
    databaseProperties.setProperty("user", Settings.STORAGE_USERNAME.getValue());
    databaseProperties.setProperty("password", Settings.STORAGE_PASSWORD.getValue());
    createTable();
  }

  @Override
  public boolean write(@Nonnull GriefProfile profile) throws SQLException {
    String command = String.format(
        "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s) values (?, ?, ?, ?, ?, ?, ?, ?);",
        TABLE_NAME,
        "event",
        "target",
        "ignore_overworld",
        "ignore_nether",
        "ignore_the_end",
        "event_color",
        "target_color",
        "dimension_color");

    if (exists(profile.getGriefEvent(), profile.getTarget())) {
      return false;
    }

    Connection connection = DriverManager.getConnection(address, databaseProperties);
    PreparedStatement statement = connection.prepareStatement(command);

    statement.setString(1,
        profile.getGriefEvent().getId());
    statement.setString(2,
        profile.getTarget());
    //    statement.setBoolean(3,
    //        profile.isIgnoredIn(DimensionTypes.OVERWORLD));
    //    statement.setBoolean(4,
    //        profile.isIgnoredIn(DimensionTypes.NETHER));
    //    statement.setBoolean(5,
    //        profile.isIgnoredIn(DimensionTypes.THE_END));
    statement.setString(6,
        profile.getColored(GriefProfile.Colorable.EVENT)
            .map(TextColor::getName)
            .orElse(null));
    statement.setString(7,
        profile.getColored(GriefProfile.Colorable.TARGET)
            .map(TextColor::getName)
            .orElse(null));
    statement.setString(8,
        profile.getColored(GriefProfile.Colorable.WORLD)
            .map(TextColor::getName)
            .orElse(null));

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
        + "event" + " = '" + griefEvent.getId() + "' AND "
        + "target" + " = '" + target + "';";

    connection.prepareStatement(command).execute();
    connection.close();
    return true;
  }

  @Nullable
  @Override
  public GriefProfile get(@Nonnull GriefEvent griefEvent, @Nonnull String target) throws Exception {
    // TODO complete setup
    return null;
  }

  @Nonnull
  @Override
  public List<GriefProfile> retrieve() throws SQLException {
    Connection connection = DriverManager.getConnection(address, databaseProperties);
    List<GriefProfile> profiles = new LinkedList<>();

    String command = "SELECT * FROM " + TABLE_NAME + ";";
    ResultSet rs = connection.prepareStatement(command).executeQuery();

    while (rs.next()) {
      GriefEvent event = GriefEvents.REGISTRY_MODULE
          .getById(rs.getString(1))
          .orElseThrow(() -> new RuntimeException(
              "Saved GriefEvent ID in MySQL database does not match any GriefEvent."));

      final GriefProfile.Builder profileBuilder = GriefProfile.builder(event, rs.getString(2));

      //      if (rs.getBoolean(3)) {
      //        profileBuilder.addIgnored(DimensionTypes.OVERWORLD);
      //      }
      //      if (rs.getBoolean(4)) {
      //        profileBuilder.addIgnored(DimensionTypes.NETHER);
      //      }
      //      if (rs.getBoolean(5)) {
      //        profileBuilder.addIgnored(DimensionTypes.THE_END);
      //      }

      Sponge.getRegistry()
          .getType(
              TextColor.class,
              Optional.ofNullable(rs.getString(6)).orElse(""))
          .ifPresent(color -> profileBuilder.putColored(GriefProfile.Colorable.EVENT, color));

      Sponge.getRegistry()
          .getType(
              TextColor.class,
              Optional.ofNullable(rs.getString(7)).orElse(""))
          .ifPresent(color -> profileBuilder.putColored(GriefProfile.Colorable.TARGET, color));

      Sponge.getRegistry()
          .getType(
              TextColor.class,
              Optional.ofNullable(rs.getString(8)).orElse(""))
          .ifPresent(color -> profileBuilder.putColored(GriefProfile.Colorable.WORLD, color));

      profiles.add(profileBuilder.build());
    }
    rs.close();
    connection.close();
    return profiles;

  }

  private void createTable() throws SQLException {
    Connection connection = DriverManager.getConnection(address, databaseProperties);
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
    connection.prepareStatement(profiles).execute();
    connection.close();
  }

  private boolean exists(GriefEvent griefEvent, String target) throws SQLException {
    Connection connection = DriverManager.getConnection(address, databaseProperties);
    String command = "SELECT * FROM "
        + TABLE_NAME + " WHERE "
        + "event" + " = '" + griefEvent.getId() + "' AND "
        + "target" + " = '" + target + "';";

    ResultSet rs = connection.prepareStatement(command).executeQuery();
    boolean hasResult = rs.next();
    rs.close();
    connection.close();
    return hasResult;

  }

}
