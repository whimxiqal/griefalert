package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class BreakAlert extends PrismAlert {

  BreakAlert(int cacheCode, GriefProfile griefProfile, DataContainer prismDataContainer) {
    super(cacheCode, griefProfile, prismDataContainer);
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.BREAK;
  }
}
