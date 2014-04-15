import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GriefAlert extends Plugin{
	// Version 15.5 : 04/12 09h00 GMT+1
	// for servermod 130+
	
	public String name = "GriefAlert";
	
	static Logger minecraftLog = Logger.getLogger("Minecraft");;
	static Logger log;
	
	static boolean toggleAlertes = true;
	static boolean gcheckToCoordinates = false;
	static boolean oldWarnBehavior = false;
	static boolean separatedLog = false;
	static boolean logSignsContent = false;
	static boolean displayPlacedSigns = false;
	static int degriefStickID = 280;
		
	static boolean logToFile = true;
	static boolean logToSQL = false;
	static String SQLdriver = "com.mysql.jdbc.Driver";
	static String SQLusername = "root";
	static String SQLpassword = "root";
	static String SQLdb = "jdbc:mysql://localhost:3306/minecraft";	

	static HashMap<Integer, GriefAction> onUseWatchList;
	static HashMap<Integer, GriefAction> onRightClickWatchList;
	static HashMap<Integer, GriefAction> onBreakWatchList;
	
	static HashMap<String, String> lastAction;
	static Location[] griefLocations;
	static int indexInTab = 0;
	
	public void enable(){
		loadGriefAlert();
		loadGriefAlertData();
		minecraftLog.info("Antigrief : v19.1-custom for MinecraftOnline by BastetFurry and 14mRh4X0r loaded");
	}
	
	public void disable(){
		minecraftLog.info("Antigrief : disabled");
		log = null; 
	}

	public static void loadGriefAlert(){
		lastAction  = new HashMap<String, String>();
		
		if ( !new File("griefAlert.properties").exists() ){
			FileWriter writer = null;
            try {
            	writer = new FileWriter("griefAlert.properties");
            	writer.write("degriefStickID=280\r\n");
            	writer.write("separatedLog=false\r\n");
            	writer.write("oldWarnBehavior=false\r\n");
            	writer.write("gcheckToCoordinates=true\r\n");
            	writer.write("alertsCodeLimit=30\r\n");
            	writer.write("logSignsContent=true\r\n");
            	writer.write("displayPlacedSigns=true\r\n");
            	writer.write("logToFile=true\r\n");
            	writer.write("# SQL configuration # \r\n");
            	writer.write("logToSQL=false\r\n");
            	writer.write("SQLdriver=com.mysql.jdbc.Driver\r\n");
            	writer.write("SQLuser=root\r\n");
            	writer.write("SQLpass=root\r\n");
            	writer.write("SQLdb=jdbc:mysql://localhost:3306/minecraft\r\n");                
            }
            catch (Exception e){
            	minecraftLog.log(Level.SEVERE, "Exception while creating griefAlert.properties", e);
            }
            finally {
                try{
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                	minecraftLog.log(Level.SEVERE, "Exception while closing writer for griefAlert.properties", e);
                }
            }
		}

		PropertiesFile properties = new PropertiesFile("griefAlert.properties");
		try {
			degriefStickID = properties.getInt("degriefStickID", 280);
			gcheckToCoordinates = properties.getBoolean("gcheckToCoordinates", false);
			oldWarnBehavior = properties.getBoolean("oldWarnBehavior", false);
			int nombreAlertes = properties.getInt("alertsCodeLimit", 30);
			griefLocations = new Location[nombreAlertes];
			logToFile = properties.getBoolean("logToFile", true);
			logToSQL = properties.getBoolean("logToSQL", false);
			logSignsContent = properties.getBoolean("logSignsContent", false);
			displayPlacedSigns = properties.getBoolean("displayPlacedSigns", false);
			
			if (logToFile){
				separatedLog = properties.getBoolean("separatedLog", false);	
			}
			
			if (logToSQL){
				SQLdriver = properties.getString("SQLdriver", "com.mysql.jdbc.Driver");
				SQLusername = properties.getString("SQLuser", "root");
				SQLpassword = properties.getString("SQLpass", "root");
				SQLdb = properties.getString("SQLdb", "jdbc:mysql://localhost:3306/minecraft");
				
				try {
		            Class.forName(SQLdriver);
		        } catch (ClassNotFoundException ex) {
		        	minecraftLog.log(Level.SEVERE, "Unable to find driver class " + SQLdriver, ex);
		        }
			}
			
        } catch (Exception e) {
        	minecraftLog.log(Level.SEVERE, "Exception while reading from server.properties", e);
        }
        
        if ( separatedLog ){
			log = Logger.getLogger("GrieferAlert");
        	File logFolder = new File("griefAlertLogs");
            try {
                if (!logFolder.exists()) {
                	logFolder.mkdir();
                }
                try {
                	log.setUseParentHandlers(false);
        			log.setLevel(Level.INFO);
        			ConsoleHandler cHandeler = new ConsoleHandler();
        			cHandeler.setFormatter(new GriefAlertLogFormatter());
        			FileHandler handeler = new FileHandler("griefAlertLogs/"
        					//+new SimpleDateFormat( "MM-dd").format(new Date())
        					+((int) (System.currentTimeMillis() / 1000L))
        					+".log" );
        			handeler.setFormatter(new GriefAlertLogFormatter());
       			log.addHandler(handeler);
        			log.addHandler(cHandeler);
        		} catch (SecurityException e1) {
        			e1.printStackTrace();
        		} catch (IOException e1) {
        			e1.printStackTrace();
        		}
            }
            catch( Exception e){
            	e.printStackTrace();
            }
        }
        else{
        	log = Logger.getLogger("Minecraft");
        }	
	}
	
	public static void loadGriefAlertData(){
		
		onUseWatchList = new HashMap<Integer, GriefAction>();
		onRightClickWatchList = new HashMap<Integer, GriefAction>();
		onBreakWatchList = new HashMap<Integer, GriefAction>();
			
		File dataSource = new File("watchedBlocks.txt");
			
		if (!dataSource.exists()) {
			FileWriter writer = null;
	        try {
	        	writer = new FileWriter(dataSource);
	        	writer.write("#Add the blocs to be watched here (without the #).\r\n");
	        	writer.write("#Fromat is : onUse|onBreak|onRightClick:blocID:displayed name:color:deny|allow(:stealth)\r\n");
	        	writer.write("#Here are some examples :\r\n");
	        	writer.write("#onUse:327:lava bucket:c:allow\r\n");
	        	writer.write("#onBreak:57:diamond block:3:allow\r\n");
	        	writer.write("#onBreak:57:diamond block:3:deny\r\n");
	        	writer.write("#onRightClick:54:chest:3:allow\r\n");
	        } catch (Exception e) {
	        	minecraftLog.log(Level.SEVERE, "Exception while creating watchedBlocks.txt");
	        } finally {
	        	try {
	        		if (writer != null) {
	        			writer.close();
	        		}
	        	} catch (IOException e) {
	        		minecraftLog.log(Level.SEVERE, "Exception while closing writer for watchedBlocks.txt");
	        	}
	        }
		}
			
		try {
			Scanner scanner = new Scanner(dataSource);
			String[] splitedLine = {""};
			
			int itemId = 0;
			boolean denied = false;
			boolean stealth = false;
			int onlyin = 0;
			
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.startsWith("#") || line.equals("")) {
					continue;
				}
				splitedLine = line.split(":");
				if (splitedLine.length >= 5) {
					// 0 : type, 1 : ID, 2 : nom, 3 : color, 4 : deny, 5 : stealth alarm, 6 : world
					
					try{
						itemId = Integer.parseInt(splitedLine[1]);
					}
					catch(NumberFormatException e){
						minecraftLog.info("watchedBlocks.txt - invalid block ID : " + splitedLine[1]);
						continue;
					}
					
					char colorCode = splitedLine[3].charAt(0);
					if ( "123456789abcdef".indexOf(colorCode) == -1 ){
						minecraftLog.info("watchedBlocks.txt - invalid colorCode : " + colorCode);
						continue;
					}
						
					denied = splitedLine[4].equalsIgnoreCase("deny");
					
					stealth = false;
					if ( splitedLine.length > 5 && splitedLine[5].equalsIgnoreCase("stealth") )
						stealth = true;
					
					onlyin = 0;
					if ( splitedLine.length > 6 )
					{
						if(splitedLine[6].equalsIgnoreCase("onlyinnether"))
						{
							onlyin = -1;
						}
						if(splitedLine[6].equalsIgnoreCase("onlyinend"))
						{
							onlyin = 1;
						}
						if(splitedLine[6].equalsIgnoreCase("onlyinnetherandend"))
						{
							onlyin = -2;
						}
					}
					
					
					if ( splitedLine[0].equalsIgnoreCase("onUse") ){
						onUseWatchList.put( itemId, new GriefAction(splitedLine[2], colorCode, denied, stealth,onlyin) );
					}
					else if ( splitedLine[0].equalsIgnoreCase("onBreak") ){
						onBreakWatchList.put( itemId, new GriefAction(splitedLine[2], colorCode, denied, stealth,onlyin) );
					}
					else if (splitedLine[0].equalsIgnoreCase("onRightClick")){
						onRightClickWatchList.put( itemId, new GriefAction(splitedLine[2], colorCode, false, stealth,onlyin) );
					}
					else{
						minecraftLog.info("watchedBlocks.txt - unrecognized activator : " + splitedLine[0]);
					}
				}
				else{
					minecraftLog.info("watchedBlocks.txt - line skipped (invalid format)");
				}
			}
			scanner.close();
		} catch (Exception e) {
			minecraftLog.log(Level.SEVERE, "Antigrief plugin : exception while loading", e);
		}
	}
	
	public static boolean isUseWatched(int blockID){
		return onUseWatchList.containsKey(blockID);
	}
	
	public static boolean isRightClickWatched(int blockID){
		return onRightClickWatchList.containsKey(blockID);
	}
	
	public static boolean isBreakWatched(int blockID){
		return onBreakWatchList.containsKey(blockID);
	}
	
	///////////////////
	////	SQL    ////
	///////////////////
	
	public void logAlertToSQL(String alertType, String playerName, int blockType, Location location ){
		Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    
	    try {
	    	conn = DriverManager.getConnection(SQLdb, SQLusername, SQLpassword);
	        ps = conn.prepareStatement("INSERT INTO griefAlerts (alertType, timestamp, player, blockType, Xpos, Ypos, Zpos)" +
	        		"VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);	         
	        ps.setString(1, alertType);
	        ps.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()) );
	        ps.setString(3, playerName);
	        ps.setInt(4, blockType);
	        ps.setDouble(5, location.x);
	        ps.setDouble(6, location.y);
	        ps.setDouble(7, location.z);
	        ps.executeUpdate();
	    }
	    catch (SQLException ex) {
	    	minecraftLog.log(Level.SEVERE, "Unable to log alert into SQL", ex);
	    }
	    finally {
	        try {
	            if (ps != null)
	                ps.close();
	            if (rs != null)
	                rs.close();
	            if (conn != null)
	                conn.close();
	        } catch (SQLException ex) {}
	    }
	}
	
	public void logSignToSQL(String playerName, Sign sign ){
		Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    
	    try {
	        conn = DriverManager.getConnection(SQLdb, SQLusername, SQLpassword);
	        ps = conn.prepareStatement("INSERT INTO placedSigns (timestamp, player, Xpos, Ypos, Zpos, line1, line2, line3, line4)" +
	        		"VALUES (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
	        ps.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()) );
	        ps.setString(2, playerName);
	        ps.setInt(3, sign.getX());
	        ps.setInt(4, sign.getY());
	        ps.setInt(5, sign.getZ());
	        ps.setString(6, sign.getText(0));
	        ps.setString(7, sign.getText(1));
	        ps.setString(8, sign.getText(2));
	        ps.setString(9, sign.getText(3));
	        ps.executeUpdate();

	    } catch (SQLException ex) {
	    	minecraftLog.log(Level.SEVERE, "Unable to log sign placement into SQL", ex);
	    } finally {
	        try {
	            if (ps != null) {
	                ps.close();
	            }
	            if (rs != null) {
	                rs.close();
	            }
	            if (conn != null) {
	                conn.close();
	            }
	        }
	        catch (SQLException ex) {
	        }
	    }
	}
	
	
	public int treatCoordinates(Location position){
		/*if (logToFile){
			log.info("Antigrief incident coordinates : X="+(int)position.x+" Y="+(int)position.y+" Z="+(int)position.z);
		} */
		griefLocations[indexInTab] = position;
		int returnCode = indexInTab;
		indexInTab ++;
		if ( indexInTab == griefLocations.length )
			indexInTab = 0;
		return returnCode;
	}
	
	public void tpToCode(Player player, int code){
		Location target  = new Location();
		try{
			target = griefLocations[code];
			/*if(target.dimension != player.getLocation().dimension)
			{
				player.sendMessage(Colors.Red + "Wrong dimension, sorry!");
				return;
			}*/
			player.teleportTo( target );
			writeToAllGriefcheckers(player.getColor()+player.getName() + Colors.Yellow +
									" is checking " + Colors.White + code +
									Colors.Yellow + " for grief.");
			//<slowriot> 2011-05-09 10:43:54 [INFO] GriefAlert:bastetfurry gchecked 27
			log.info("GriefAlert:"+player.getName()+":gchecked:"+code);
		}
		catch( NullPointerException e){
			player.sendMessage(Colors.Rose+"This code isn't linked to any alert.");
		}
		catch( ArrayIndexOutOfBoundsException e ){
			player.sendMessage(Colors.Rose+"This code isn't linked to any alert.");
		}
	}
	
	public void initialize(){
		GriefAlertListener listener = new GriefAlertListener(this);
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_RIGHTCLICKED, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PLACE, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.ITEM_USE, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.SIGN_CHANGE, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.HANGING_ENTITY_DESTROYED, listener, this, PluginListener.Priority.MEDIUM);
	}
	
	public void writeToAllGriefcheckers(String msg)
	{
		for  (Player p : etc.getServer().getPlayerList() ) {
			if (p.canUseCommand("/griefalert")){
				p.sendMessage(msg);
			}
		}	
	}

	
}
