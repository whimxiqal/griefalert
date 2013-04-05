public class GriefAlertListener extends PluginListener {
    private final GriefAlert main;

    public GriefAlertListener(GriefAlert main) {
        this.main = main;
    }

    public boolean onCommand(Player player, String[] split) {
        String playername = player.getName();
        if (player.canUseCommand("/griefalert")) {
            if (split[0].equalsIgnoreCase("/gcheckold")) {
                if (split.length==2) {
                    int code = -1;
                    try {
                         code = Integer.parseInt(split[1]);
                    }
                    catch (NumberFormatException n) {
                        player.sendMessage(Colors.Rose+"Invalid code");
                        return true;
                    }

                    if (code >= 0 && code < GriefAlert.griefLocations.length) {
                        main.tpToCode(player, code);
                    } else {
                        player.sendMessage(Colors.Rose+"Invalid code");
                    }
                } else if (GriefAlert.gcheckToCoordinates && split.length==4) {
                    Location target = new Location();
                    try {
                        target.x = Integer.parseInt(split[1]);
                        target.y = Integer.parseInt(split[2]);
                        target.z = Integer.parseInt(split[3]);
                    }
                    catch (NumberFormatException n) {
                        player.sendMessage(Colors.Rose+"Invalid coordinates");
                        return true;
                    }
                    target.rotX=player.getRotation();
                    target.rotY=player.getPitch();
                    target.dimension = player.getLocation().dimension;
                    player.teleportTo(target);
                    GriefAlert.log.info("GriefAlert:"+player.getName()+
                            ":teleport:"+
                            "x="+target.x+":"+
                            "y="+target.y+":"+
                            "z="+target.z);
                } else if (GriefAlert.gcheckToCoordinates && split.length==5) {
                    Location target = new Location();
                    try {
                        target.x = Integer.parseInt(split[1]);
                        target.y = Integer.parseInt(split[2]);
                        target.z = Integer.parseInt(split[3]);
                        target.dimension = Integer.parseInt(split[4]);
                    }
                    catch (NumberFormatException n) {
                        player.sendMessage(Colors.Rose+"Invalid coordinates");
                        return true;
                    }
                    target.rotX=player.getRotation();
                    target.rotY=player.getPitch();
                    player.teleportTo(target);
                    GriefAlert.log.info("GriefAlert:"+player.getName()+
                            ":teleport:"+
                            "x="+target.x+":"+
                            "y="+target.y+":"+
                            "z="+target.z+":"+
                            "d="+target.dimension);
                } else if (GriefAlert.gcheckToCoordinates && split.length==6) {
                    Location target = new Location();
                    try {
                        target.x = Integer.parseInt(split[1]);
                        target.y = Integer.parseInt(split[2]);
                        target.z = Integer.parseInt(split[3]);
                        target.rotX = Integer.parseInt(split[4]);
                        target.rotY = Integer.parseInt(split[5]);
                    }
                    catch (NumberFormatException n) {
                        player.sendMessage(Colors.Rose+"Invalid coordinates");
                        return true;
                    }
                    target.dimension = player.getLocation().dimension;
                    player.teleportTo(target);
                    GriefAlert.log.info("GriefAlert:"+player.getName()+
                            ":teleport:"+
                            "x="+target.x+":"+
                            "y="+target.y+":"+
                            "z="+target.z+":"+
                            "r="+target.rotX+":"+
                            "p="+target.rotY);
                } else if (GriefAlert.gcheckToCoordinates && split.length==7) {
                    Location target = new Location();
                    try {
                        target.x = Integer.parseInt(split[1]);
                        target.y = Integer.parseInt(split[2]);
                        target.z = Integer.parseInt(split[3]);
                        target.rotX = Integer.parseInt(split[4]);
                        target.rotY = Integer.parseInt(split[5]);
                        target.dimension = Integer.parseInt(split[6]);
                    }
                    catch (NumberFormatException n) {
                        player.sendMessage(Colors.Rose+"Invalid coordinates");
                        return true;
                    }
                    player.teleportTo(target);
                    GriefAlert.log.info("GriefAlert:"+player.getName()+
                            ":teleport:"+
                            "x="+target.x+":"+
                            "y="+target.y+":"+
                            "z="+target.z+":"+
                            "r="+target.rotX+":"+
                            "p="+target.rotY+":"+
                            "d="+target.dimension);
                } else if (split.length==1) {
                    if (GriefAlert.indexInTab == 0) {
                        main.tpToCode(player, GriefAlert.griefLocations.length-1);
                    } else {
                        main.tpToCode(player, GriefAlert.indexInTab-1);
                    }
                } else {
                    player.sendMessage(Colors.Rose+"Usage : /gcheck for the last griefalert");
                    player.sendMessage(Colors.Rose+"Usage : /gcheck <number> for a specific alert location");
                    if (GriefAlert.gcheckToCoordinates)
                        player.sendMessage(Colors.Rose+"Usage : /gcheck X Y Z to a specific location");
                }
                return true;
            } else if (split[0].equalsIgnoreCase("/griefalerttoggle")) {
                if (!player.canUseCommand("/griefalerttoggle")) {
                    return false;
                }

                GriefAlert.toggleAlertes=!GriefAlert.toggleAlertes;
                for (Player p : etc.getServer().getPlayerList()) {
                    if (p.canUseCommand("/griefalert")) {
                        p.sendMessage(Colors.Yellow + "("+playername+") Antigrief alerts : "
                                + (GriefAlert.toggleAlertes ? "enabled" : "disabled"));
                    }
                }
                return true;
            } else if (split[0].equalsIgnoreCase("/gareload")) {
                GriefAlert.loadGriefAlert();
                GriefAlert.loadGriefAlertData();
                player.sendMessage(Colors.Green+"GrieferAlert plugin reloaded");
                return true;
            }
        }
        return false;
    }

    public void onBlockRightClicked(Player player, Block blockClicked, Item item) {
        if (item.getItemId() == GriefAlert.degriefStickID && player.canUseCommand("/degriefstick")) {
            postDegriefstickToLog(player, blockClicked);
            World myWorld = player.getWorld();
            myWorld.setBlockAt(0, blockClicked.getX(), blockClicked.getY(), blockClicked.getZ());
        } else if (GriefAlert.toggleAlertes && GriefAlert.isRightClickWatched(blockClicked.getType())) {
            GriefAction data = GriefAlert.onRightClickWatchList.get(blockClicked.getType());

            if (GriefAlert.logToFile) {
                postGriefAlertToLog(player, blockClicked, data, "rightclicked", main.treatCoordinates(player.getLocation()));
/*                  log.info("Antigrief alarm : "+player.getName()+" right clicked "
                        +(("aeiou".contains(data.blockName.substring(0, 1).toLowerCase())) ? "an " : "a ")+data.blockName
                        +" ("+treatCoordinates(player.getLocation())+")"); */
            }

/*              if (logToSQL) {
                logAlertToSQL("RIGHTCLICK", player.getName(), blockClicked.getType(), player.getLocation());
            } */
        }
    }

    public boolean onBlockPlace(Player player, Block blockPlaced, Block blockClicked, Item itemInHand) {
        if (GriefAlert.toggleAlertes) {
            int blockType = blockPlaced.getType();
            if (GriefAlert.isUseWatched(blockType)) {
                GriefAction data = GriefAlert.onUseWatchList.get(blockType);
                String playerName = player.getName();
                int tcoord = main.treatCoordinates(player.getLocation());
                String message = " used " +(("aeiou".contains(data.blockName.substring(0, 1).toLowerCase())) ? "an " : "a ")
                    + data.blockName + " ("+main.treatCoordinates(player.getLocation())+") in the "
                    + getWorldTypeString(player.getWorld()) + " world.";

                if (!data.stealth && !player.canUseCommand("/doNotTriggerAlerts")) {
                    if (!GriefAlert.lastAction.containsKey(playerName)) {
                        GriefAlert.lastAction.put(playerName, "");
                    }
                    if (GriefAlert.oldWarnBehavior || !GriefAlert.lastAction.get(playerName).contains("c"+blockType)) {
                        GriefAlert.lastAction.put(playerName, "c"+blockType);
                        main.writeToAllGriefcheckers("§"+data.alertColor + playerName + message);
//                          String coloredMessage = player.getColor()+playerName+"§"+data.alertColor + message;
/*                          for  (Player p : etc.getServer().getPlayerList()) {
                            if (p.canUseCommand("/griefalert")) {
                                p.sendMessage(coloredMessage);
                            }
                        } */
                    }
                }
                if (GriefAlert.logToFile) {
                    postGriefAlertToLog(player, blockPlaced, data, "used", tcoord);
/*                      log.info("Antigrief alarm : "+playerName+message+
                            " at x="+blockPlaced.getX()+" y="+blockPlaced.getY()+" z="+blockPlaced.getZ()); */
                }
/*                  if (logToSQL) {
                    logAlertToSQL("CREATE", playerName, blockType, player.getLocation());
                } */
                // Do we deny?
                if (data.denied && !player.canUseCommand("/ignoreDenies")) {
                    //Always deny
                    if (data.onlyin == 0)
                        return true;
                    int dim = player.getLocation().dimension;
                    //Only deny it in the End
                    if (data.onlyin == 1 && dim == World.Dimension.END.getId()) {
                        return true;
                    }
                    //Only in the Nether
                    if (data.onlyin == -1 && dim == World.Dimension.NETHER.getId()) {
                        return true;
                    }
                    //Nether and End but allow in normal world
                    if (data.onlyin == -2 && dim != World.Dimension.NORMAL.getId()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean onItemUse(Player player, Block blockPlaced, Block blockClicked, Item item) {
        int itemId = item.getItemId();
        if (GriefAlert.toggleAlertes && itemId>255 && GriefAlert.isUseWatched(itemId)) {
            String playerName = player.getName();
            GriefAction data = GriefAlert.onUseWatchList.get(itemId);
            int tcoord = main.treatCoordinates(player.getLocation());
            String message = " used "
                +(("aeiou".contains(data.blockName.substring(0, 1).toLowerCase())) ? "an " : "a ")
                + data.blockName + " ("+main.treatCoordinates(player.getLocation())+") in the "
                    + getWorldTypeString(player.getWorld()) + " world.";
            if (!data.stealth && !player.canUseCommand("/doNotTriggerAlerts")) {
                if (!GriefAlert.lastAction.containsKey(playerName)) {
                    GriefAlert.lastAction.put(playerName, "");
                }
                if (GriefAlert.oldWarnBehavior || !GriefAlert.lastAction.get(playerName).contains("u"+itemId)) {
                    GriefAlert.lastAction.put(playerName, "u"+itemId);
                    main.writeToAllGriefcheckers("§"+data.alertColor + playerName + message);
//                      String coloredMessage = player.getColor()+playerName+"§"+data.alertColor + message;
/*                      for  (Player p : etc.getServer().getPlayerList()) {
                        if (p.canUseCommand("/griefalert")) {
                            p.sendMessage(coloredMessage);
                        }
                    } */
                }
                if (GriefAlert.logToFile) {
                    postGriefAlertToLog(player, blockPlaced, data, "used", tcoord);
/*                      log.info("Antigrief alarm : "+playerName+message+
                            " at x="+blockPlaced.getX()+" y="+blockPlaced.getY()+" z="+blockPlaced.getZ()); */
                }
/*                  if (logToSQL) {
                    logAlertToSQL("CREATE", playerName, itemId, player.getLocation());
                } */
                if (data.denied && !player.canUseCommand("/ignoreDenies")) {
                    //Always deny
                    if (data.onlyin == 0)
                        return true;
                    int dim = player.getLocation().dimension;
                    //Only deny it in the End
                    if (data.onlyin == 1 && dim == World.Dimension.END.getId()) {
                        return true;
                    }
                    //Only in the Nether
                    if (data.onlyin == -1 && dim == World.Dimension.NETHER.getId()) {
                        return true;
                    }
                    //Nether and End but allow in normal world
                    if (data.onlyin == -2 && dim != World.Dimension.NORMAL.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
// <slowriot> 2011-05-09 10:43:54 [INFO] GriefAlert:bastetfurry:broke:glass:0:x=89:y=65:z=-25:sx=88:sy=65:sz=-22:27
    public boolean onBlockBreak(Player player, Block block) {
//          player.sendMessage("DEBUG!" + block.getType());
        World myWorld = player.getWorld();
        int blockID = 0;

        blockID = myWorld.getBlockIdAt(block.getX(), block.getY(), block.getZ());

        if (GriefAlert.toggleAlertes && GriefAlert.isBreakWatched(blockID)) {
            String playerName = player.getName();
            GriefAction data = GriefAlert.onBreakWatchList.get(blockID);
            int tcoord = main.treatCoordinates(player.getLocation());
            String message = " broke "
                +(("aeiou".contains(data.blockName.substring(0, 1).toLowerCase())) ? "an " : "a ")
                + data.blockName + " ("+tcoord+") in the "
                    + getWorldTypeString(player.getWorld()) + " world.";

            if (!data.stealth && !player.canUseCommand("/doNotTriggerAlerts")) {
                if (!GriefAlert.lastAction.containsKey(playerName)) {
                    GriefAlert.lastAction.put(playerName, "");
                }
                if (GriefAlert.oldWarnBehavior || !GriefAlert.lastAction.get(playerName).contains("d"+blockID)) {
                    GriefAlert.lastAction.put(playerName, "d"+blockID);
                    main.writeToAllGriefcheckers("§"+data.alertColor + playerName + message);
//                      String coloredMessage = player.getColor()+playerName+"§"+data.alertColor + message;
/*                      for  (Player p : etc.getServer().getPlayerList()) {
                        if (p.canUseCommand("/griefalert")) {
                            p.sendMessage(coloredMessage);
                        }
                    } */
                }
            }
            if (GriefAlert.logToFile) {
                postGriefAlertToLog(player, block, data, "broke", tcoord);
//                  log.info("Antigrief alarm : " + playerName + message +
//                          " at x="+block.getX()+" y="+block.getY()+" z="+block.getZ());
            }
/*              if (logToSQL) {
                logAlertToSQL("DESTROY", playerName, blockID, player.getLocation());
            } */
            if (data.denied && !player.canUseCommand("/ignoreDenies")) {
                //Always deny
                if (data.onlyin == 0)
                    return true;
                int dim = player.getLocation().dimension;
                //Only deny it in the End
                if (data.onlyin == 1 && dim == World.Dimension.END.getId()) {
                    return true;
                }
                //Only in the Nether
                if (data.onlyin == -1 && dim == World.Dimension.NETHER.getId()) {
                    return true;
                }
                //Nether and End but allow in normal world
                if (data.onlyin == -2 && dim != World.Dimension.NORMAL.getId()) {
                    return true;
                }

            }
        }
        return false;
    }

    public boolean onSignChange(Player player, Sign sign) {

        World myWorld = player.getWorld();
        String worldtype = getWorldTypeString(myWorld);

        if (GriefAlert.logSignsContent) {
            if (GriefAlert.displayPlacedSigns) {
                for (Player p : etc.getServer().getPlayerList()) {
                    if (p.canUseCommand("/griefalert")) {
                        p.sendMessage("Sign placed by " + player.getName() + " at " + sign.getX() + " " + sign.getY()
                                + " " + sign.getZ() + " in the " + getWorldTypeString(player.getWorld()) + " world.");
                        for (int i = 0; i<4; i++) {
                            String signText = sign.getText(i);
                            if (!signText.equals("")) {
                                p.sendMessage("Line "+ (i+1) +" : " + sign.getText(i));
                            }
                        }
                    }
                }
            }

            if (GriefAlert.logToFile) {
                GriefAlert.log.info("Sign placed by " + player.getName() + " at " + sign.getX() + " " + sign.getY() + " " + sign.getZ() + " in world " + worldtype);
                for (int i = 0; i<4; i++) {
                    String signText = sign.getText(i);
                    if (!signText.equals("")) {
                        GriefAlert.log.info("Line "+ (i+1) +" : " + sign.getText(i));
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onHangingEntityDestroyed(HangingEntity entity, DamageSource damageSource) {
        if (damageSource == null || !damageSource.getSourceEntity().isPlayer()) {
            return false;
        }

        Player player = (Player) damageSource.getSourceEntity();
        boolean indirect = damageSource.isIndirectDamageSource();
        int blockID = entity.getEntity() instanceof OEntityPainting ? 321 : 389; // Painting or item frame

        if (GriefAlert.toggleAlertes && GriefAlert.isBreakWatched(blockID)) {
            String playerName = player.getName();
            GriefAction data = GriefAlert.onBreakWatchList.get(blockID);
            int tcoord = main.treatCoordinates(player.getLocation());
            String message = String.format(" %s %s %s (%s) in the %s world.",
                indirect ? "indirectly broke" : "broke",
                (("aeiou".contains(data.blockName.substring(0, 1).toLowerCase())) ? "an" : "a"),
                data.blockName, tcoord, getWorldTypeString(player.getWorld()));

            if (!data.stealth && !player.canUseCommand("/doNotTriggerAlerts")) {
                if (!GriefAlert.lastAction.containsKey(playerName)) {
                    GriefAlert.lastAction.put(playerName, "");
                }
                if (GriefAlert.oldWarnBehavior || !GriefAlert.lastAction.get(playerName).contains("d"+blockID)) {
                    GriefAlert.lastAction.put(playerName, "d"+blockID);
                    main.writeToAllGriefcheckers("\u00a7"+data.alertColor + playerName + message);
                }
            }

            if (GriefAlert.logToFile) {
                postGriefAlertToLog(player,
                        new Block(0, etc.floor(entity.getX()), etc.floor(entity.getY()), etc.floor(entity.getZ())),
                        data, indirect ? "remote" : "broke", tcoord);
            }

            if (data.denied && !player.canUseCommand("/ignoreDenies")) {
                //Always deny
                if (data.onlyin == 0)
                    return true;
                int dim = player.getLocation().dimension;
                //Only deny it in the End
                if (data.onlyin == 1 && dim == World.Dimension.END.getId()) {
                    return true;
                }
                //Only in the Nether
                if (data.onlyin == -1 && dim == World.Dimension.NETHER.getId()) {
                    return true;
                }
                //Nether and End but allow in normal world
                if (data.onlyin == -2 && dim != World.Dimension.NORMAL.getId()) {
                    return true;
                }

            }
        }

        return false;
    }

    public String getWorldTypeString(World world) {
        try {
            if (world.getType() == World.Dimension.NORMAL)
                return "normal";
            if (world.getType() == World.Dimension.NETHER)
                return "nether";
            if (world.getType() == World.Dimension.END)
                return "ender";
        } catch (Exception e) {

        }
        return "unknown";
    }

    public void postGriefAlertToLog(Player player, Block block, GriefAction data, String action, int gnum) {
        World myWorld = player.getWorld();
        int t = block.getX();
        String worldtype = getWorldTypeString(myWorld);

        GriefAlert.log.info("GriefAlert:"+
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

    public void postDegriefstickToLog(Player player, Block block) {
        World myWorld = player.getWorld();
        String worldtype = getWorldTypeString(myWorld);

        GriefAlert.log.info("GriefAlert:"+
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

}
