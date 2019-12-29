package com.minecraftonline.griefalert.api.profiles;

import com.minecraftonline.griefalert.util.GriefEvents;

public final class GriefProfiles {

  // Other GriefProfiles will be stored onsite in flatfile

  public static final GriefProfileOld PLACE_SIGN = GriefProfileOld.of("minecraft:sign", GriefEvents.PLACE);
  public static final GriefProfileOld BREAK_SIGN = GriefProfileOld.of("minecraft:sign", GriefEvents.BREAK);

}
