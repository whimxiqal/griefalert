package com.minecraftonline.griefalert.alerts;

import com.helion3.prism.api.records.PrismRecord;
import com.minecraftonline.griefalert.api.profiles.GriefProfile;
import org.spongepowered.api.text.Text;

public class PlaceAlert extends PrismAlert {

  public PlaceAlert(int cacheCode, GriefProfile griefProfile, PrismRecord prismRecord) {
    super(cacheCode, griefProfile, prismRecord);
  }

  @Override
  public Text getMessageText() {
    // TODO: Write message text for PlaceAlert
    return Text.of("PlaceAlert text");
  }

}
