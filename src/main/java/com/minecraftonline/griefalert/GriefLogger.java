package com.minecraftonline.griefalert;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.hanging.Painting;
import org.spongepowered.api.entity.living.player.Player;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

final class GriefLogger {
    private final Logger logger;
    private static Connection conn;

    GriefLogger(Logger logger) {
        this.logger = logger;
        testTables();
    }

    private static void testConnection() throws SQLException {
        if (conn == null || conn.isClosed() || !conn.isValid(2)) {
            conn = DriverManager.getConnection("jdbc:mysql://" + GriefAlert.readConfigStr("SQLdb"),
                                               GriefAlert.readConfigStr("SQLusername"),
                                               GriefAlert.readConfigStr("SQLpassword"));
        }
    }

    private void testTables() {
        try {
            testConnection();
            logger.debug("Testing GriefAlert_Log table and creating if needed...");
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS GriefAlert_Log " + //
                                                                 "(id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT," +
                                                                 "user VARCHAR(36) NOT NULL," +
                                                                 "block_state TEXT NOT NULL," +
                                                                 "block_json TEXT NOT NULL, " +
                                                                 "x INT(10) NOT NULL, " +
                                                                 "y INT(10) NOT NULL, " +
                                                                 "z INT(10) NOT NULL, " +
                                                                 "px INT(10) NOT NULL, " +
                                                                 "py INT(10) NOT NULL, " +
                                                                 "pz INT(10) NOT NULL, " +
                                                                 "dimension TEXT NOT NULL, " +
                                                                 "world_id VARCHAR(36) NOT NULL, " +
                                                                 "time_stamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                                                 "PRIMARY KEY (id))");
            ps.execute();
            ps.close();
        } catch (SQLException sqlex) {
            logger.error("SQL Exception while testing GriefAlert_Log table...", sqlex);
        }
        try {
            testConnection();
            logger.debug("Testing GriefAlert_Signs table and creating if needed...");
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS GriefAlert_Signs " + //
                                                                 "(id BIGINT UNSIGNED AUTO_INCREMENT, " +
                                                                 "user VARCHAR(36) NOT NULL, " +
                                                                 "x INT(10) NOT NULL, " +
                                                                 "y INT(10) NOT NULL, " +
                                                                 "z INT(10) NOT NULL, " +
                                                                 "dimension TEXT NOT NULL, " +
                                                                 "world_id VARCHAR(36) NOT NULL, " +
                                                                 "line1 VARCHAR(50), " +
                                                                 "line2 VARCHAR(50), " +
                                                                 "line3 VARCHAR(50), " +
                                                                 "line4 VARCHAR(50), " +
                                                                 "time_stamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                                                                 "PRIMARY KEY (id))");
            ps.execute();
            ps.close();
        } catch (SQLException sqlex) {
            logger.error("SQL Exception while testing GriefAlert_Signs table...", sqlex);
        }
    }

    final void storeAction(Player player, GriefAction action) {
        logger.debug("Storing Grief Action data...");
        PreparedStatement ps = null;
        try {
            testConnection();
            ps = conn.prepareStatement("INSERT INTO GriefAlert_Log (user,block_state,block_json,x,y,z,px,py,pz,dimension,world_id) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, player.getUniqueId().toString());
            ps.setString(2, actionToString(action));
            ps.setString(3, toJSON(actionToContrainer(action)));
            ps.setInt(4, action.getX());
            ps.setInt(5, action.getY());
            ps.setInt(6, action.getZ());
            ps.setInt(7, action.getGriefer().getLocation().get().getBlockX());
            ps.setInt(8, action.getGriefer().getLocation().get().getBlockY());
            ps.setInt(9, action.getGriefer().getLocation().get().getBlockZ());
            ps.setString(10, action.getWorld().getDimension().getType().getId());
            ps.setString(11, action.getWorld().getUniqueId().toString());
            ps.execute();
            logger.debug("Store complete!");
        } catch (SQLException sqlex) {
            logger.error("SQL Exception while inserting into GriefAlert_Signs table...", sqlex);
        } finally {
            closeQuietly(ps);
        }
    }

    final void storeSign(Player player, Sign sign, SignData signData) {
        logger.debug("Storing Sign data...");
        PreparedStatement ps = null;
        try {
            testConnection();
            ps = conn.prepareStatement("INSERT INTO GriefAlert_Signs (user,x,y,z,dimension,world,line1,line2,line3,line4) VALUES(?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, player.getUniqueId().toString());
            ps.setInt(2, sign.getLocation().getBlockX());
            ps.setInt(3, sign.getLocation().getBlockY());
            ps.setInt(4, sign.getLocation().getBlockZ());
            ps.setString(5, sign.getWorld().getDimension().getType().getId());
            ps.setString(6, sign.getWorld().getUniqueId().toString());
            ps.setString(5, signData.get(0).isPresent() ? signData.get(0).get().toPlain() : "");
            ps.setString(6, signData.get(1).isPresent() ? signData.get(1).get().toPlain() : "");
            ps.setString(7, signData.get(2).isPresent() ? signData.get(2).get().toPlain() : "");
            ps.setString(8, signData.get(3).isPresent() ? signData.get(3).get().toPlain() : "");
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

    private String actionToString(GriefAction action) {
        if (action.getBlock() != null) {
            return action.getBlock().getState().toString();
        }
        else if (action.getItem() != null) {
            return action.getItem().toString();
        }
        else if (action.getEntity() instanceof Painting) {
            return "minecraft:painting[art=" + action.getEntity().get(Keys.ART).get().getId() + "]";
        }
        else if (action.getEntity() instanceof ItemFrame && action.getEntity().get(Keys.REPRESENTED_ITEM).isPresent()) {
            return "minecraft:item_frame[item_id=" + action.getEntity().get(Keys.REPRESENTED_ITEM).get().getTranslation().get() + "]";
        }
        else if (action.getEntity() != null) {
            return action.getEntity().getType().getId();
        }
        return action.getBlockName();
    }

    private DataContainer actionToContrainer(GriefAction action) {
        if (action.getBlock() != null) {
            return action.getBlock().toContainer();
        }
        if (action.getItem() != null) {
            return action.getItem().toContainer();
        }
        return action.getEntity().toContainer();
    }

    private String toJSON(DataView container) {
        final DataTranslator<ConfigurationNode> translator = DataTranslators.CONFIGURATION_NODE;
        ConfigurationNode node = translator.translate(container);
        StringWriter writer = new StringWriter();
        try {
            GsonConfigurationLoader.builder().build().saveInternal(node, writer);
        } catch (IOException e) {
            logger.error("Unable to create json block string", e);
        }
        return writer.toString();
    }
}