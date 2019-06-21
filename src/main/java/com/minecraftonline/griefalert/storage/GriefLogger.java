package com.minecraftonline.griefalert.storage;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.data.persistence.DataTranslator;
import org.spongepowered.api.data.persistence.DataTranslators;
import org.spongepowered.api.entity.living.player.Player;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.core.GriefInstance;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This class handles remote logging of grief instances to a SQL database.
 */
public final class GriefLogger {
	
	private static final String GRIEF_INSTANCE_TABLE_NAME = "GriefAlert_Log";
	
	private static final String EDITED_SIGN_TABLE_NAME = "GriefAlert_Signs";
	
	/** The main plugin class object. */
	private final GriefAlert plugin;
	/** The connection object to reach the SQL database. */
    private Connection conn;
    
    /** True if there exists an error which would prevent complete functionality of this logger. */
    private boolean error;

    /**
     * The only constructor for a GriefLogger.
     * @param griefAlert The main plugin class object
     */
    public GriefLogger(GriefAlert griefAlert) {
    	this.plugin = griefAlert;
    	try {
			testConnection();
			prepareTables();
		} catch (SQLException sqlex) {
			error = true;
			plugin.getLogger().error("SQL Exception while testing connecting with SQL database.\n"
					+ "Is your information correct in the configuration file?");
		}
    }

    /**
     * Tests the connection with the SQL database
     * @throws SQLException Throws most likely if SQP configuration nods are
     * not set up with the right syntax
     */
    private void testConnection() throws SQLException {
		if (conn == null || conn.isClosed() || !conn.isValid(2)) {
			String connectionPath = "jdbc:mysql://" + plugin.getConfigString("SQLdb");
		    conn = DriverManager.getConnection(connectionPath,
		                                       plugin.getConfigString("SQLusername"),
		                                       plugin.getConfigString("SQLpassword"));
		}
    }

    /**
     * Generates non-existing tables into which logs are placed:
     * <li><b>GriefAlert_Log</b>: Contains all grief instances</li>
     * <li><b>GriefAlert_Signs</b>: Contains all information about signs edited by players</li>
     */
    private void prepareTables() {
        try {
        	// Generate the Grief Instance table if it doesn't exist
            plugin.getLogger().debug("Preparing Grief Instance Table ('" + GRIEF_INSTANCE_TABLE_NAME + "') table and creating if needed...");
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + GRIEF_INSTANCE_TABLE_NAME + " " + //
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
                                                                 "time_stamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + // Why not save with UNIX long?
                                                                 "PRIMARY KEY (id))");
            ps.execute();
            ps.close();
        } catch (SQLException sqlex) {
            plugin.getLogger().error("SQL Exception while preparing Grief Instance Table ('" + GRIEF_INSTANCE_TABLE_NAME + "') table...", sqlex);
        }
        try {
        	// Generate the Sign Edit table if it doesn't exist
            testConnection();
            plugin.getLogger().debug("Preparing Edited Sign Table ('" + EDITED_SIGN_TABLE_NAME + "') table and creating if needed...");
            PreparedStatement ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + EDITED_SIGN_TABLE_NAME + " " +
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
            plugin.getLogger().error("SQL Exception while preparing Edited Sign Table ('" + EDITED_SIGN_TABLE_NAME + "') table...", sqlex);
        }
    }

    /**
     * Save an instance of grief in the SQL database
     * @param instance The specific instance of grief to save
     */
    public final void storeGriefInstance(GriefInstance instance) {
    	if (error) {
    		plugin.getLogger().warn("Tried to store a grief instance, but there is an error with the SQL database. Instance: " + instance.toString());
    		return;
    	}
        plugin.getLogger().debug("Storing Grief Instance data...");
        PreparedStatement ps = null;
        try {
            testConnection();
            ps = conn.prepareStatement("INSERT INTO " + GRIEF_INSTANCE_TABLE_NAME + " (user,block_state,block_json,x,y,z,px,py,pz,dimension,world_id) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, instance.getGrieferAsPlayer().getUniqueId().toString());
            ps.setString(2, instance.getGriefObjectAsString());
            ps.setString(3, toJSON(generateDataContainer(instance)));
            ps.setInt(4, instance.getLocation().getBlockX());
            ps.setInt(5, instance.getLocation().getBlockY());
            ps.setInt(6, instance.getLocation().getBlockZ());
            ps.setInt(7, instance.getGrieferSnapshot().getLocation().get().getBlockX());
            ps.setInt(8, instance.getGrieferSnapshot().getLocation().get().getBlockY());
            ps.setInt(9, instance.getGrieferSnapshot().getLocation().get().getBlockZ());
            ps.setString(10, instance.getLocation().getExtent().getDimension().getType().getId());
            ps.setString(11, instance.getLocation().getExtent().getUniqueId().toString());
            ps.execute();
            plugin.getLogger().debug("Store complete!");
        } catch (SQLException sqlex) {
            plugin.getLogger().error("SQL Exception while inserting into Grief Instance ('" + GRIEF_INSTANCE_TABLE_NAME + "') table...", sqlex);
        } catch (NullPointerException nullex) {
        	plugin.getLogger().error("A field within a grief instance cannot be null when being stored", nullex);
        } finally {
            closeQuietly(ps);
        }
    }

    /**
     * The storage method for sign editing in game.
     * @param player The player who edited the sign
     * @param sign The representation of the sign
     * @param signData The data held by the new sign
     */
    public final void storeSignEdit(Player player, Sign sign, SignData signData) {
    	if (error) {
    		plugin.getLogger().warn("Tried to store a sign grief instance, but there is an error with the SQL database");
    		return;
    	}
        plugin.getLogger().debug("Storing Edited Sign data...");
        PreparedStatement ps = null;
        try {
            testConnection();
            ps = conn.prepareStatement("INSERT INTO " + EDITED_SIGN_TABLE_NAME + " (user,x,y,z,dimension,world_id,line1,line2,line3,line4) VALUES(?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, player.getUniqueId().toString());
            ps.setInt(2, sign.getLocation().getBlockX());
            ps.setInt(3, sign.getLocation().getBlockY());
            ps.setInt(4, sign.getLocation().getBlockZ());
            ps.setString(5, sign.getWorld().getDimension().getType().getId());
            ps.setString(6, sign.getWorld().getUniqueId().toString());
            ps.setString(7, signData.get(0).isPresent() ? signData.get(0).get().toPlain() : "");
            ps.setString(8, signData.get(1).isPresent() ? signData.get(1).get().toPlain() : "");
            ps.setString(9, signData.get(2).isPresent() ? signData.get(2).get().toPlain() : "");
            ps.setString(10, signData.get(3).isPresent() ? signData.get(3).get().toPlain() : "");
            ps.execute();
            plugin.getLogger().debug("Store complete!");
        } catch (SQLException sqlex) {
            plugin.getLogger().error("SQL Exception while inserting into Edited Sign ('" + EDITED_SIGN_TABLE_NAME + "') table...", sqlex);
        } finally {
            closeQuietly(ps);
        }
    }

    /**
     * A helper method to ignore any exceptions thrown when closing an AutoCloseable.
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

    /**
     * Unclear purpose.
     * @param instance
     * @return
     */
    private DataContainer generateDataContainer(GriefInstance instance) {
        return instance.getGriefObjectToContainer();
    }

    /**
     * Unclear purpose.
     * @param container
     * @return
     */
    private String toJSON(DataView container) {
        final DataTranslator<ConfigurationNode> translator = DataTranslators.CONFIGURATION_NODE;
        ConfigurationNode node = translator.translate(container);
        StringWriter writer = new StringWriter();
        try {
            GsonConfigurationLoader.builder().build().saveInternal(node, writer);
        } catch (IOException e) {
            plugin.getLogger().error("Unable to create json block string", e);
        }
        return writer.toString();
    }
    
    public boolean isError() {
    	return error;
    }
}
