package com.minecraftonline.griefalert.alerts;

import com.helion3.prism.api.records.PrismRecord;
import com.minecraftonline.griefalert.api.profiles.GriefProfile;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.text.Text;

public class PlaceAlert extends PrismAlert {

  PlaceAlert(int cacheCode, GriefProfile griefProfile, DataContainer prismDataContainer) {
    super(cacheCode, griefProfile, prismDataContainer);
  }

  @Override
  public Text getMessageText() {
    // TODO: Write message text for PlaceAlert
    return Text.of("PlaceAlert text");
  }

}
