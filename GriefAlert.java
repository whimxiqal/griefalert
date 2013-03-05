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
		minecraftLog.info("Antigrief : v17-custom for MinecraftOnline by BastetFurry loaded");
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
		GriefAlertListener listener = new GriefAlertListener();
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_RIGHTCLICKED, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_PLACE, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.ITEM_USE, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.SIGN_CHANGE, listener, this, PluginListener.Priority.MEDIUM);
	}
	
	public class GriefAlertListener extends PluginListener{

		public boolean onCommand(Player player, String[] split) {
			String playername = player.getName();
			if (player.canUseCommand("/griefalert")){
				if ( split[0].equalsIgnoreCase("/gcheckold") ){
					if( split.length==2 ){
						int code = -1;
						try{
							 code = Integer.parseInt( split[1] );
						}
						catch (NumberFormatException n){
							player.sendMessage(Colors.Rose+"Invalid code");
							return true;
						}
						
						if ( code >= 0 && code < griefLocations.length ){
							tpToCode( player, code );
						}
						else{
							player.sendMessage(Colors.Rose+"Invalid code");
						}
					}
					else if ( gcheckToCoordinates && split.length==4){
						Location target = new Location();
						try{
							target.x = Integer.parseInt( split[1] );
							target.y = Integer.parseInt( split[2] );
							target.z = Integer.parseInt( split[3] );
						}
						catch (NumberFormatException n){
							player.sendMessage(Colors.Rose+"Invalid coordinates");
							return true;
						}
						target.rotX=player.getRotation();
						target.rotY=player.getPitch();
						target.dimension = player.getLocation().dimension;
						player.teleportTo(target);	
						log.info("GriefAlert:"+player.getName()+
								":teleport:"+
								"x="+target.x+":"+
								"y="+target.y+":"+
								"z="+target.z);
					}
					else if ( gcheckToCoordinates && split.length==5){
						Location target = new Location();
						try{
							target.x = Integer.parseInt( split[1] );
							target.y = Integer.parseInt( split[2] );
							target.z = Integer.parseInt( split[3] );
							target.dimension = Integer.parseInt( split[4] );
						}
						catch (NumberFormatException n){
							player.sendMessage(Colors.Rose+"Invalid coordinates");
							return true;
						}
						target.rotX=player.getRotation();
						target.rotY=player.getPitch();
						player.teleportTo(target);	
						log.info("GriefAlert:"+player.getName()+
								":teleport:"+
								"x="+target.x+":"+
								"y="+target.y+":"+
								"z="+target.z+":"+
								"d="+target.dimension);
					}
					else if ( gcheckToCoordinates && split.length==6){
						Location target = new Location();
						try{
							target.x = Integer.parseInt( split[1] );
							target.y = Integer.parseInt( split[2] );
							target.z = Integer.parseInt( split[3] );
							target.rotX = Integer.parseInt( split[4] );
							target.rotY = Integer.parseInt( split[5] );
						}
						catch (NumberFormatException n){
							player.sendMessage(Colors.Rose+"Invalid coordinates");
							return true;
						}
						target.dimension = player.getLocation().dimension;
						player.teleportTo(target);	
						log.info("GriefAlert:"+player.getName()+
								":teleport:"+
								"x="+target.x+":"+
								"y="+target.y+":"+
								"z="+target.z+":"+
								"r="+target.rotX+":"+
								"p="+target.rotY);
					}
					else if ( gcheckToCoordinates && split.length==7){
						Location target = new Location();
						try{
							target.x = Integer.parseInt( split[1] );
							target.y = Integer.parseInt( split[2] );
							target.z = Integer.parseInt( split[3] );
							target.rotX = Integer.parseInt( split[4] );
							target.rotY = Integer.parseInt( split[5] );
							target.dimension = Integer.parseInt( split[6] );
						}
						catch (NumberFormatException n){
							player.sendMessage(Colors.Rose+"Invalid coordinates");
							return true;
						}
						player.teleportTo(target);	
						log.info("GriefAlert:"+player.getName()+
								":teleport:"+
								"x="+target.x+":"+
								"y="+target.y+":"+
								"z="+target.z+":"+
								"r="+target.rotX+":"+
								"p="+target.rotY+":"+
								"d="+target.dimension);
					}
					else if (split.length==1){
						if (indexInTab == 0){
							tpToCode( player, griefLocations.length-1 );
						}
						else{
							tpToCode( player, indexInTab-1 );
						}
					}
					else{
						player.sendMessage(Colors.Rose+"Usage : /gcheck for the last griefalert");
						player.sendMessage(Colors.Rose+"Usage : /gcheck <number> for a specific alert location");
						if (gcheckToCoordinates)
							player.sendMessage(Colors.Rose+"Usage : /gcheck X Y Z to a specific location");
					}
					return true;
				}
				else if (split[0].equalsIgnoreCase("/griefalerttoggle")){
					if(!player.canUseCommand("/griefalerttoggle"))
					{
						return false;
					}
						
					toggleAlertes=!toggleAlertes;
					for  (Player p : etc.getServer().getPlayerList() ) {
						if (p.canUseCommand("/griefalert")){
							p.sendMessage(Colors.Yellow + "("+playername+") Antigrief alerts : "
									+ (toggleAlertes ? "enabled" : "disabled"));
						}
					}
					return true;
				}
				else if (split[0].equalsIgnoreCase("/gareload")){
					loadGriefAlert();
					loadGriefAlertData();
					player.sendMessage(Colors.Green+"GrieferAlert plugin reloaded");
					return true;
				}
			}
			return false;
		}
		
		public void onBlockRightClicked(Player player, Block blockClicked, Item item) {
			if( item.getItemId() == degriefStickID && player.canUseCommand("/degriefstick") ){
				postDegriefstickToLog(player, blockClicked);
				World myWorld = player.getWorld();
				myWorld.setBlockAt(0, blockClicked.getX(), blockClicked.getY(), blockClicked.getZ());
			}
			else if ( toggleAlertes && isRightClickWatched(blockClicked.getType()) ){
				GriefAction data =  onRightClickWatchList.get(blockClicked.getType());
				
				if (logToFile)
				{
					postGriefAlertToLog(player, blockClicked, data, "rightclicked", treatCoordinates( player.getLocation() ));
/*					log.info("Antigrief alarm : "+player.getName()+" right clicked "
							+(("aeiou".contains(data.blockName.substring(0, 1).toLowerCase())) ? "an " : "a ")+data.blockName
							+" ("+treatCoordinates( player.getLocation() )+")"); */	
				}
				
/*				if (logToSQL){
					logAlertToSQL("RIGHTCLICK", player.getName(), blockClicked.getType(), player.getLocation());
				} */
			}
		}
	
		public boolean onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {
			if ( toggleAlertes ){
				int blockType = blockPlaced.getType();
				if ( isUseWatched(blockType) ){
					GriefAction data = onUseWatchList.get(blockType);
					String playerName = player.getName();
					int tcoord = treatCoordinates( player.getLocation() );
					String message = " used " +(("aeiou".contains(data.blockName.substring(0, 1).toLowerCase())) ? "an " : "a ")
						+ data.blockName + " ("+treatCoordinates( player.getLocation() )+") in the "
						+ getWorldTypeString(player.getWorld()) + " world.";
	
					if ( !data.stealth && !player.canUseCommand("/doNotTriggerAlerts")){
						if ( !lastAction.containsKey(playerName) ){
							lastAction.put(playerName, "");
						}
						if (oldWarnBehavior || !lastAction.get(playerName).contains("c"+blockType) ){
							lastAction.put(playerName, "c"+blockType);
							writeToAllGriefcheckers("§"+data.alertColor + playerName + message);
//							String coloredMessage = player.getColor()+playerName+"§"+data.alertColor + message;
/*							for  (Player p : etc.getServer().getPlayerList() ) {
								if (p.canUseCommand("/griefalert")){
									p.sendMessage(coloredMessage);
								}
							} */
						}
					}
					if (logToFile){
						postGriefAlertToLog(player, blockPlaced, data, "used", tcoord);
/*						log.info("Antigrief alarm : "+playerName+message+
								" at x="+blockPlaced.getX()+" y="+blockPlaced.getY()+" z="+blockPlaced.getZ()); */
					}
/*					if (logToSQL){
						logAlertToSQL("CREATE", playerName, blockType, player.getLocation());
					} */
					// Do we deny?
					if ( data.denied && !player.canUseCommand("/ignoreDenies") ){
						//Always deny
						if(data.onlyin == 0)
							return true;
						int dim = player.getLocation().dimension;
						//Only deny it in the End
						if(data.onlyin == 1 && dim == World.Dimension.END.getId())
						{
							return true;
						}
						//Only in the Nether
						if(data.onlyin == -1 && dim == World.Dimension.NETHER.getId())
						{
							return true;
						}
						//Nether and End but allow in normal world
						if(data.onlyin == -2 && dim != World.Dimension.NORMAL.getId())
						{
							return true;
						}
					}
				}
			}
			
			return false;
		}
			
		public boolean onItemUse(Player player, Block blockPlaced, Block blockClicked, Item item) {
			int itemId = item.getItemId();
			if ( toggleAlertes && itemId>255 && isUseWatched(itemId) ){
				String playerName = player.getName();
				GriefAction data = onUseWatchList.get(itemId);
				int tcoord = treatCoordinates( player.getLocation() );
				String message = " used "
					+(("aeiou".contains(data.blockName.substring(0, 1).toLowerCase())) ? "an " : "a ")
					+ data.blockName + " ("+treatCoordinates( player.getLocation() )+") in the "
						+ getWorldTypeString(player.getWorld()) + " world.";
				if ( !data.stealth && !player.canUseCommand("/doNotTriggerAlerts")){
					if ( !lastAction.containsKey(playerName) ){
						lastAction.put(playerName, "");
					}
					if (oldWarnBehavior || !lastAction.get(playerName).contains("u"+itemId) ){
						lastAction.put(playerName, "u"+itemId);
						writeToAllGriefcheckers("§"+data.alertColor + playerName + message);
//						String coloredMessage = player.getColor()+playerName+"§"+data.alertColor + message;
/*						for  (Player p : etc.getServer().getPlayerList() ) {
							if (p.canUseCommand("/griefalert")){
								p.sendMessage(coloredMessage);
							}
						} */
					}
					if (logToFile){
						postGriefAlertToLog(player, blockPlaced, data, "used", tcoord);
/*						log.info("Antigrief alarm : "+playerName+message+
								" at x="+blockPlaced.getX()+" y="+blockPlaced.getY()+" z="+blockPlaced.getZ()); */
					}
/*					if (logToSQL){
						logAlertToSQL("CREATE", playerName, itemId, player.getLocation());
					} */
					if ( data.denied && !player.canUseCommand("/ignoreDenies") ){
						//Always deny
						if(data.onlyin == 0)
							return true;
						int dim = player.getLocation().dimension;
						//Only deny it in the End
						if(data.onlyin == 1 && dim == World.Dimension.END.getId())
						{
							return true;
						}
						//Only in the Nether
						if(data.onlyin == -1 && dim == World.Dimension.NETHER.getId())
						{
							return true;
						}
						//Nether and End but allow in normal world
						if(data.onlyin == -2 && dim != World.Dimension.NORMAL.getId())
						{
							return true;
						}
					}
				}
			}
	        return false;
	    }
// <slowriot> 2011-05-09 10:43:54 [INFO] GriefAlert:bastetfurry:broke:glass:0:x=89:y=65:z=-25:sx=88:sy=65:sz=-22:27	
		public boolean onBlockBreak(Player player, Block block) {
//			player.sendMessage("DEBUG!" + block.getType());
			World myWorld = player.getWorld();
			int blockID = 0;

			blockID = myWorld.getBlockIdAt(block.getX(), block.getY(), block.getZ());
			
			if ( toggleAlertes && isBreakWatched(blockID) )
			{
				String playerName = player.getName();
				GriefAction data = onBreakWatchList.get(blockID);
				int tcoord = treatCoordinates( player.getLocation() );
				String message = " broke "
					+(("aeiou".contains(data.blockName.substring(0, 1).toLowerCase())) ? "an " : "a ")
					+ data.blockName + " ("+tcoord+") in the "
						+ getWorldTypeString(player.getWorld()) + " world.";
									
				if ( !data.stealth && !player.canUseCommand("/doNotTriggerAlerts") ){
					if ( !lastAction.containsKey(playerName) ){
						lastAction.put(playerName, "");
					}					
					if (oldWarnBehavior || !lastAction.get(playerName).contains("d"+blockID) ){
						lastAction.put(playerName, "d"+blockID);
						writeToAllGriefcheckers("§"+data.alertColor + playerName + message);
//						String coloredMessage = player.getColor()+playerName+"§"+data.alertColor + message;
/*						for  (Player p : etc.getServer().getPlayerList() ) {
							if (p.canUseCommand("/griefalert")){
								p.sendMessage(coloredMessage);
							}
						} */
					}
				}
				if (logToFile)
				{
					postGriefAlertToLog(player, block, data, "broke", tcoord);
//					log.info("Antigrief alarm : " + playerName + message + 
//							" at x="+block.getX()+" y="+block.getY()+" z="+block.getZ());
				}
/*				if (logToSQL){
					logAlertToSQL("DESTROY", playerName, blockID, player.getLocation());
				} */
				if ( data.denied && !player.canUseCommand("/ignoreDenies") ){
					//Always deny
					if(data.onlyin == 0)
						return true;
					int dim = player.getLocation().dimension;
					//Only deny it in the End
					if(data.onlyin == 1 && dim == World.Dimension.END.getId())
					{
						return true;
					}
					//Only in the Nether
					if(data.onlyin == -1 && dim == World.Dimension.NETHER.getId())
					{
						return true;
					}
					//Nether and End but allow in normal world
					if(data.onlyin == -2 && dim != World.Dimension.NORMAL.getId())
					{
						return true;
					}

				}
			}
			return false;
		}
		
		public boolean onSignChange(Player player, Sign sign) {
			
			World myWorld = player.getWorld();
			String worldtype = getWorldTypeString(myWorld);
			
			if ( logSignsContent ){
				if ( displayPlacedSigns ){
	     			for  (Player p : etc.getServer().getPlayerList() ) {
						if (p.canUseCommand("/griefalert")){
							p.sendMessage("Sign placed by " + player.getName() + " at " + sign.getX() + " " + sign.getY()
									+ " " + sign.getZ() + " in the "
						+ getWorldTypeString(player.getWorld()) + " world.");
							for (int i = 0; i<4; i++){
								String signText = sign.getText(i);
								if ( !signText.equals("") ){
			    					p.sendMessage("Line "+ (i+1) +" : " + sign.getText(i));
			    	           	}
							}
						}
					}
	     		}
		            
		        if ( logToFile ){
		        	log.info("Sign placed by " + player.getName() + " at " + sign.getX() + " " + sign.getY() + " " + sign.getZ() + " in world " + worldtype);
		        	for (int i = 0; i<4; i++){
		        		String signText = sign.getText(i);
		        		if ( !signText.equals("") ){
		        			log.info("Line "+ (i+1) +" : " + sign.getText(i));
		        		}
		        	}
		        }
		            
/*		        if (logToSQL){
		        	logSignToSQL(player.getName(), sign);	 
		        } */
			}
	        return false;
	    }
	
		
		public String getWorldTypeString(World world)
		{
			try
			{
				if(world.getType() == World.Dimension.NORMAL)
					return "normal";
				if(world.getType() == World.Dimension.NETHER)
					return "nether";
				if(world.getType() == World.Dimension.END)
					return "ender";
			}
			catch(Exception e)
			{
				
			}
			return "unknown";
		}
		
		public void postGriefAlertToLog(Player player, Block block, GriefAction data, String action, int gnum)
		{
			World myWorld = player.getWorld();
			int t = block.getX(); 
			String worldtype = getWorldTypeString(myWorld);
			
			log.info("GriefAlert:"+
					 player.getName()+":"+
					 action+":"+
					 data.blockName+":"+
					 myWorld.getBlockData(block.getX(), block.getY(), block.getZ())+":"+
					 "x="+t+":"+
					 "y="+block.getY()+":"+
					 "z="+block.getZ()+":"+
					 "sx="+(int)player.getX()+":"+
					 "sy="+(int)player.getY()+":"+
					 "sz="+(int)player.getZ()+":"+
					 "w="+worldtype+":"+
					 gnum);
		}
		
		public void postDegriefstickToLog(Player player, Block block)
		{
			World myWorld = player.getWorld();
			String worldtype = getWorldTypeString(myWorld);

			log.info("GriefAlert:"+
					 player.getName()+":"+
					 "griefstick:"+
					 block.getType()+":"+
					 "x="+block.getX()+":"+
					 "y="+block.getY()+":"+
					 "z="+block.getZ()+":"+
					 "sx="+(int)player.getX()+":"+
					 "sy="+(int)player.getY()+":"+
					 "sz="+(int)player.getZ()+":"+
					 "w="+worldtype);
		}
		
	}	//	GriefAlertClass
	
	public void writeToAllGriefcheckers(String msg)
	{
		for  (Player p : etc.getServer().getPlayerList() ) {
			if (p.canUseCommand("/griefalert")){
				p.sendMessage(msg);
			}
		}	
	}

	
}
