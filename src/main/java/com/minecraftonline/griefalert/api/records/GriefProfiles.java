package com.minecraftonline.griefalert.api.records;

import com.minecraftonline.griefalert.util.GriefEvents;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import org.spongepowered.api.data.DataContainer;

public final class GriefProfiles {

  // Other GriefProfiles to be stored on SQL database
  // TODO: See if we can remove this class all together and just put everything in SQL
  //  (The SQL database will have all the generic, boring ones, but maybe we can put
  //  the "special" ones like signs and armor stands in there as well for consistency

  public static final GriefProfile PLACE_SIGN = GriefProfile.of(
      DataContainer.createNew()
          .set(GriefProfileDataQueries.TARGET, "minecraft:sign")
          .set(GriefProfileDataQueries.EVENT, GriefEvents.PLACE.getId()));

  public static final GriefProfile BREAK_SIGN = GriefProfile.of(
      DataContainer.createNew()
          .set(GriefProfileDataQueries.TARGET, "minecraft:sign")
          .set(GriefProfileDataQueries.EVENT, GriefEvents.BREAK.getId()));

  public static final GriefProfile BREAK_COBBLESTONE_TEST = GriefProfile.of(
      DataContainer.createNew()
          .set(GriefProfileDataQueries.TARGET, "minecraft:cobblestone")
          .set(GriefProfileDataQueries.EVENT, GriefEvents.BREAK.getId()));

}
