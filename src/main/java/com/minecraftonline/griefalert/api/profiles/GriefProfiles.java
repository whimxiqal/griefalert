package com.minecraftonline.griefalert.api.profiles;

import com.minecraftonline.griefalert.util.GriefEvents;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import org.spongepowered.api.data.DataContainer;

public final class GriefProfiles {

  // Other GriefProfiles to be stored on SQL database

  public static final GriefProfile PLACE_SIGN = GriefProfile.of(
      DataContainer.createNew()
          .set(GriefProfileDataQueries.TARGET, "minecraft:sign")
          .set(GriefProfileDataQueries.EVENT, GriefEvents.PLACE));

  public static final GriefProfile BREAK_SIGN = GriefProfile.of(
      DataContainer.createNew()
      .set(GriefProfileDataQueries.TARGET, "minecraft:sign")
      .set(GriefProfileDataQueries.EVENT, GriefEvents.BREAK));

}
