/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.prism;

import com.helion3.prism.api.records.PrismRecord;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.enums.Details;

public class BreakAlert extends PrismAlert {

  public BreakAlert(GriefProfile griefProfile, PrismRecord prismRecord) {
    super(griefProfile, prismRecord);
    addDetail(Details.blockCreator());
  }

}
