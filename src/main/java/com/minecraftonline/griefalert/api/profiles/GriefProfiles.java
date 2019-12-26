package com.minecraftonline.griefalert.api.profiles;

import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.alerts.SignBreakAlert;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.text.format.TextColors;

public final class GriefProfiles {

  // Other GriefProfiles will be stored onsite in flatfile

  public static final GriefProfile PLACE_SIGN = GriefProfile.of("minecraft:sign", GriefEvents.PLACE);
  public static final GriefProfile BREAK_SIGN = GriefProfile.of("minecraft:sign", GriefEvents.BREAK);

}
