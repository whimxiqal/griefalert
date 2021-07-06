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

package com.minecraftonline.griefalert.storage.inspections;

import com.minecraftonline.griefalert.api.alerts.inspections.AlertInspection;
import com.minecraftonline.griefalert.api.alerts.inspections.Request;
import com.minecraftonline.griefalert.api.storage.InspectionStorage;
import com.minecraftonline.griefalert.util.enums.Settings;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import javax.annotation.Nonnull;

/**
 * Storage implementation for inspections in MySQL.
 */
public class MySqlInspectionStorage implements InspectionStorage {

  private static final String TABLE_NAME = "GriefAlertInspections";
  private final String address;
  private final Properties databaseProperties;

  /**
   * General constructor.
   *
   * @throws SQLException if error with SQL
   */
  public MySqlInspectionStorage() throws SQLException {
    address = String.format("jdbc:mysql://%s/%s",
        Settings.STORAGE_ADDRESS.getValue(),
        Settings.STORAGE_DATABASE.getValue());
    databaseProperties = new Properties();
    databaseProperties.setProperty("user", Settings.STORAGE_USERNAME.getValue());
    databaseProperties.setProperty("password", Settings.STORAGE_PASSWORD.getValue());
    createTable();
  }

  @Override
  public boolean write(@Nonnull AlertInspection inspection) {
    String command = String.format(
        "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s) "
            + "values (UNHEX(?), UNHEX(?), ?, ?, ?, ?, ?, UNHEX(?), ?);",
        TABLE_NAME,
        Field.officerUuid,
        Field.grieferUuid,
        Field.event,
        Field.target,
        Field.xPos,
        Field.yPos,
        Field.zPos,
        Field.worldUuid,
        Field.inspected);

    try (Connection connection = DriverManager.getConnection(address, databaseProperties);
         PreparedStatement statement = connection.prepareStatement(command)) {
      statement.setString(1, inspection.getOfficerUuid().toString().replace("-", ""));
      statement.setString(2, inspection.getGrieferUuid().toString().replace("-", ""));
      statement.setString(3, inspection.getEventId());
      statement.setString(4, inspection.getTarget());
      statement.setInt(5, inspection.getX());
      statement.setInt(6, inspection.getY());
      statement.setInt(7, inspection.getZ());
      statement.setString(8, inspection.getWorldUuid().toString().replace("-", ""));
      statement.setLong(9, inspection.getInspected().getEpochSecond());
      statement.execute();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Nonnull
  @Override
  public Collection<AlertInspection> query(@Nonnull Request request) {
    // TODO implement
    return Collections.emptyList();
  }

  private void createTable() {
    try (Connection connection = DriverManager.getConnection(address, databaseProperties)) {
      String inspections = "CREATE TABLE IF NOT EXISTS "
          + TABLE_NAME + " ("
          + "id int(10) unsigned NOT NULL AUTO_INCREMENT, "
          + Field.officerUuid + " binary(16) NOT NULL, "
          + Field.grieferUuid + " binary(16) NOT NULL, "
          + Field.event + " varchar(16) NOT NULL, "
          + Field.target + " varchar(225) NOT NULL, "
          + Field.xPos + " int(10) NOT NULL, "
          + Field.yPos + " smallint(5) NOT NULL, "
          + Field.zPos + " int(10) NOT NULL, "
          + Field.worldUuid + " binary(16) NOT NULL, "
          + Field.inspected + " int(10) unsigned NOT NULL, "
          + "PRIMARY KEY (`id`), "
          + "KEY `location` (`"
          + Field.worldUuid + "`, `"
          + Field.xPos + "`, `"
          + Field.yPos + "`, `"
          + Field.zPos + "`), "
          + "KEY `inspected` (`inspected`)"
          + ") ENGINE=InnoDB DEFAULT CHARACTER SET utf8 "
          + "  DEFAULT COLLATE utf8_general_ci;";
      connection.prepareStatement(inspections).execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private enum Field {
    officerUuid,
    grieferUuid,
    event,
    target,
    xPos,
    yPos,
    zPos,
    worldUuid,
    inspected
  }

}
