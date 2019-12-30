package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.text.Text;

public class DeathAlert extends PrismAlert {

  DeathAlert(int cacheCode, GriefProfile griefProfile, DataContainer prismDataContainer) {
    super(cacheCode, griefProfile, prismDataContainer);
  }

  @Override
  public Text getMessageText() {
    // TODO: Write message text for PlaceAlert
    return Text.of("PlaceAlert text");
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.DEATH;
  }

}
