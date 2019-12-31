package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.data.DataContainer;


public class DeathAlert extends PrismAlert {

  DeathAlert(GriefProfile griefProfile, DataContainer prismDataContainer) {
    super(griefProfile, prismDataContainer);
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.DEATH;
  }

}
