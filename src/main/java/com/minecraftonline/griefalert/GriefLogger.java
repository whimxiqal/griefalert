package com.minecraftonline.griefalert;

import org.slf4j.Logger;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

final class GriefLogger {
    private final Logger logger;
    private static Connection conn;

    GriefLogger(Logger logger) {
        this.logger = logger;
        //testTables();
    }

    private static void testConnection() throws SQLException {
        if (conn == null || conn.isClosed() || !conn.isValid(2)) {
            conn = DriverManager.getConnection("jdbc:mysql://" + GriefAlert.readConfigStr("SQLdb"),
                                               GriefAlert.readConfigStr("SQLusername"),
                                               GriefAlert.readConfigStr("SQLPassword"));
        }
    }

    private void testTables() {
        try {
            testConnection();
            logger.debug("Testing GriefAlert_Log table and creating if needed...");
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS GriefAlert_Log " + //
                                                                 "(TYPEID NOT NULL, " +
                                                                 "USER VARCHAR(36), " +
                                                                 "X INT(10), " +
                                                                 "Y INT(10), " +
                                                                 "Z INT(10, " +
                                                                 "PX INT(10), " +
                                                                 "PY INT(10), " +
                                                                 "PZ INT(10), " +
                                                                 "TIME_STAMP DATE");
            ps.execute();
            ps.close();
        } catch (SQLException sqlex) {
            logger.error("SQL Exception while testing Room101-Rooms tables...", sqlex);
        }
        try {
            testConnection();
            logger.debug("Testing GriefAlert_Signs table and creating if needed...");
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS GriefAlert_Signs " + //
                                                                 "(USER VARCHAR(36), " +
                                                                 "X INT(10), " +
                                                                 "Y INT(10), " +
                                                                 "Z INT(10, " +
                                                                 "LINE_1 VARCHAR(15), " +
                                                                 "LINE_2 VARCHAR(15), " +
                                                                 "LINE_3 VARCHAR(15), " +
                                                                 "LINE_4 VARCHAR(15), " +
                                                                 "TIME_STAMP DATE)");
            ps.execute();
            ps.close();
        } catch (SQLException sqlex) {
            logger.error("SQL Exception while testing Room101-Rooms tables...", sqlex);
        }
    }

    final void storeAction(Player player, GriefAction action) {

    }

    final void storeSign(Player player, Sign sign, SignData signData) {
        logger.debug("Storing Sign data...");
        PreparedStatement ps = null;
        try {
            testConnection();
            ps = conn.prepareStatement("INSERT INTO GriefAlert_Signs (USER,X,Y,Z,LINE_1,LINE_2,LINE_3,LINE_4,TIME_STAMP) VALUES(?,?,?,?,?,?,?,?,?)");
            ps.setString(1, player.getUniqueId().toString());
            ps.setInt(2, sign.getLocation().getBlockX());
            ps.setInt(3, sign.getLocation().getBlockY());
            ps.setInt(4, sign.getLocation().getBlockZ());
            ps.setString(5, signData.get(1).isPresent() ? signData.get(1).get().toPlain() : "");
            ps.setString(6, signData.get(2).isPresent() ? signData.get(2).get().toPlain() : "");
            ps.setString(7, signData.get(3).isPresent() ? signData.get(3).get().toPlain() : "");
            ps.setString(8, signData.get(4).isPresent() ? signData.get(4).get().toPlain() : "");
            //ps.setTimestamp(9, Timestamp.);
            ps.execute();
            logger.debug("Store complete!");
        } catch (SQLException sqlex) {
            logger.error("SQL Exception while inserting into GriefAlert_Signs table...", sqlex);
        } finally {
            closeQuietly(ps);
        }
    }

    private static void closeQuietly(AutoCloseable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            /* ignored */
        }
    }
}
