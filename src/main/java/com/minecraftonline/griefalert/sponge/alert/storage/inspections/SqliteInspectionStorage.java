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

package com.minecraftonline.griefalert.sponge.alert.storage.inspections;

import com.minecraftonline.griefalert.SpongeGriefAlert;
import com.minecraftonline.griefalert.common.alert.alerts.inspections.AlertInspection;
import com.minecraftonline.griefalert.common.alert.alerts.inspections.InspectionRequest;
import com.minecraftonline.griefalert.common.alert.storage.InspectionStorage;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * Storage implementation for inspection storage using SQLite.
 */
public class SqliteInspectionStorage implements InspectionStorage {

  private static final String TABLE_NAME = "GriefAlertInspections";
  private final String address;

  /**
   * General constructor.
   *
   * @throws SQLException if error with SQL
   */
  public SqliteInspectionStorage() throws SQLException {
    address = "jdbc:sqlite:" + SpongeGriefAlert.getSpongeInstance().getDataDirectory()
        .getPath() + "/griefalert.db";
    createTable();
  }

  @Override
  public boolean write(@Nonnull AlertInspection inspection) {
    String command = String.format(
        "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s) values (?, ?, ?, ?, ?, ?, ?, ?, ?);",
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

    try (Connection connection = DriverManager.getConnection(address);
         PreparedStatement statement = connection.prepareStatement(command)) {
      statement.setBytes(1, toBytes(inspection.getOfficerUuid()));
      statement.setBytes(2, toBytes(inspection.getGrieferUuid()));
      statement.setString(3, inspection.getEventId());
      statement.setString(4, inspection.getTarget());
      statement.setInt(5, inspection.getX());
      statement.setInt(6, inspection.getY());
      statement.setInt(7, inspection.getZ());
      statement.setBytes(8, toBytes(inspection.getWorldUuid()));
      statement.setLong(9, inspection.getInspected().getEpochSecond());
      statement.execute();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  private byte[] toBytes(UUID uuid) throws SQLException {
    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
    bb.putLong(uuid.getMostSignificantBits());
    bb.putLong(uuid.getLeastSignificantBits());
    return bb.array();
  }

  @Nonnull
  @Override
  public Collection<AlertInspection> query(@Nonnull InspectionRequest inspectionRequest) {
    // TODO implement
    return Collections.emptyList();
  }

  private void createTable() {
    try (Connection connection = DriverManager.getConnection(address)) {
      String inspections = "CREATE TABLE IF NOT EXISTS "
          + TABLE_NAME + " ("
          + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
          + Field.officerUuid + " binary NOT NULL, "
          + Field.grieferUuid + " binary NOT NULL, "
          + Field.event + " varchar NOT NULL, "
          + Field.target + " varchar NOT NULL, "
          + Field.xPos + " integer NOT NULL, "
          + Field.yPos + " integer NOT NULL, "
          + Field.zPos + " int NOT NULL, "
          + Field.worldUuid + " binary NOT NULL, "
          + Field.inspected + " integer unsigned NOT NULL"
          + ");";
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
