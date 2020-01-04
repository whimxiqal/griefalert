package com.minecraftonline.griefalert.api.records;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.util.GriefEvents;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;

public final class GriefProfiles {

  // Other GriefProfiles to be stored on SQL database
  // TODO: See if we can remove this class all together and just put everything in SQL
  //  (The SQL database will have all the generic, boring ones, but maybe we can put
  //  the "special" ones like signs and armor stands in there as well for consistency

//  public static final GriefProfile PLACE_SIGN = GriefProfile.of(
//      DataContainer.createNew()
//          .set(GriefProfileDataQueries.TARGET, "minecraft:sign")
//          .set(GriefProfileDataQueries.EVENT, GriefEvents.PLACE));
//
//  public static final GriefProfile BREAK_SIGN = GriefProfile.of(
//      DataContainer.createNew()
//          .set(GriefProfileDataQueries.TARGET, "minecraft:sign")
//          .set(GriefProfileDataQueries.EVENT, GriefEvents.BREAK));
//
//  public static final GriefProfile INTERACT_ITEM_FRAME = GriefProfile.of(
//      DataContainer.createNew()
//          .set(GriefProfileDataQueries.TARGET, "minecraft:item_frame")
//          .set(GriefProfileDataQueries.EVENT, GriefEvents.INTERACT));
//
//  public static final GriefProfile INTERACT_ARMOR_STAND = GriefProfile.of(
//      DataContainer.createNew()
//          .set(GriefProfileDataQueries.TARGET, "minecraft:armor_stand")
//          .set(GriefProfileDataQueries.EVENT, GriefEvents.INTERACT));
//
//  public static final GriefProfile ATTACK_ITEM_FRAME = GriefProfile.of(
//      DataContainer.createNew()
//          .set(GriefProfileDataQueries.TARGET, "minecraft:item_frame")
//          .set(GriefProfileDataQueries.EVENT, GriefEvents.ATTACK));
//
//  public static final GriefProfile ATTACK_ARMOR_STAND = GriefProfile.of(
//      DataContainer.createNew()
//          .set(GriefProfileDataQueries.TARGET, "minecraft:armor_stand")
//          .set(GriefProfileDataQueries.EVENT, GriefEvents.ATTACK));
//
//  public static final GriefProfile BREAK_COBBLESTONE_TEST = GriefProfile.of(
//      DataContainer.createNew()
//          .set(GriefProfileDataQueries.TARGET, "minecraft:cobblestone")
//          .set(GriefProfileDataQueries.EVENT, GriefEvents.BREAK)
//          .set(GriefProfileDataQueries.IGNORE_OVERWORLD, true)
//          .set(GriefProfileDataQueries.IGNORE_THE_END, true));
//
//  public static final GriefProfile KILL_COW_TEST = GriefProfile.of(
//      DataContainer.createNew()
//          .set(GriefProfileDataQueries.TARGET, "cow")
//          .set(GriefProfileDataQueries.EVENT, GriefEvents.DEATH)
//          .set(GriefProfileDataQueries.TARGET_COLOR, TextColors.GOLD)
//          .set(GriefProfileDataQueries.DIMENSION_COLOR, TextColors.AQUA)
//          .set(GriefProfileDataQueries.EVENT_COLOR, TextColors.GREEN));
//
//  public static final GriefProfile USE_EGG_TEST = GriefProfile.of(
//      DataContainer.createNew()
//          .set(GriefProfileDataQueries.TARGET, "minecraft:egg")
//          .set(GriefProfileDataQueries.EVENT, GriefEvents.ITEM_USE));

}
