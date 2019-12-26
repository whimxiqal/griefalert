//package com.minecraftonline.griefalert.util;
//
//import com.helion3.prism.api.data.PrismEvent;
//import com.minecraftonline.griefalert.api.profiles.GriefProfile;
//import com.minecraftonline.griefalert.profiles.io.StorageLine;
//import org.spongepowered.api.world.DimensionTypes;
//
//public final class GriefProfiles {
//
//
//  // TODO: Move these methods into their respective classes
//  public static StorageLine toStorage(GriefProfile griefProfile) {
//    StorageLine.Builder builder = StorageLine.builder()
//        .addItem(griefProfile.getEventType().getName())
//        .addItem(griefProfile.getTarget().replaceAll("minecraft:", ""))
//        .addItem(griefProfile.getAlertColor().getName());
//    if (griefProfile.isStealthy()) {
//      builder.addItem("-s");
//    }
//    if (griefProfile.isIgnoredIn(DimensionTypes.OVERWORLD)) {
//      builder.addItem("--ignore-overworld");
//    }
//    if (griefProfile.isIgnoredIn(DimensionTypes.NETHER)) {
//      builder.addItem("--ignore-nether");
//    }
//    if (griefProfile.isIgnoredIn(DimensionTypes.THE_END)) {
//      builder.addItem("--ignore-the-end");
//    }
//    return builder.build();
//  }
//
//  public static GriefProfile fromStorage(StorageLine line) {
//    String[] tokens = line.getTokens();
//    if (tokens.length < 3) {
//      throw new IllegalArgumentException("Not enough arguments. "
//          + "Use format <TYPE> <OBJECT_ID> <COLOR> [FLAGS]");
//    }
//    PrismEvent eventType;
//    if (Prism.Registry.getPrismEvent(tokens[0]).isPresent()) {
//      eventType = Prism.Registry.getPrismEvent(tokens[0]).get();
//    } else {
//      throw new IllegalArgumentException("Invalid Event Type: " + tokens[0]);
//    }
//
//    String griefedObjectId;
//    if (tokens[1].contains("[a-zA-Z]:")) {
//      griefedObjectId = tokens[1];
//    } else {
//      griefedObjectId = "minecraft:" + tokens[1];
//    }
//
//    GriefProfile.Builder builder = GriefProfile.builder(eventType, griefedObjectId);
//
//    builder.setAlertColor(General.stringToColor(tokens[2]));
//    for (int i = 3; i < tokens.length; i++) {
//      if (tokens[i].equals("--stealth") || tokens[i].equals("-s")) {
//        builder.setStealthy(true);
//      }
//      if (tokens[i].equals("--ignore-overworld")) {
//        builder.ignoreDimension(DimensionTypes.OVERWORLD);
//      }
//      if (tokens[i].equals("--ignore-nether")) {
//        builder.ignoreDimension(DimensionTypes.NETHER);
//      }
//      if (tokens[i].equals("--ignore-the_end")) {
//        builder.ignoreDimension(DimensionTypes.THE_END);
//      }
//    }
//    return builder.build();
//  }
//
//}
