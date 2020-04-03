/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.prism;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.enums.Details;

public class BreakAlert extends PrismAlert {

  public BreakAlert(GriefProfile griefProfile, PrismRecordArchived prismRecord) {
    super(griefProfile, prismRecord);
    addDetail(Details.BLOCK_CREATOR);
  }

}
