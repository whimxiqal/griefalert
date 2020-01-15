/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.prism;

import com.minecraftonline.griefalert.api.data.SignText;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Prism;

public class SignBreakAlert extends PrismAlert {

  /**
   * Default constructor for a sign break alert. This add all the 'extra'
   * information about the text on its lines
   *
   * @param griefProfile the grief profile for this alert
   * @param prismRecord  the prism record triggering this alert
   */
  public SignBreakAlert(GriefProfile griefProfile, PrismRecordArchived prismRecord) {
    super(griefProfile, prismRecord);

    Prism.getBrokenSignText(getPrismRecord())
        .flatMap(SignText::getText1)
        .ifPresent(text ->
            addSummaryContent("Line 1", alert -> Format.bonus(text)));

    Prism.getBrokenSignText(getPrismRecord())
        .flatMap(SignText::getText2)
        .ifPresent(text ->
            addSummaryContent("Line 2", alert -> Format.bonus(text)));

    Prism.getBrokenSignText(getPrismRecord())
        .flatMap(SignText::getText3)
        .ifPresent(text ->
            addSummaryContent("Line 3", alert -> Format.bonus(text)));

    Prism.getBrokenSignText(getPrismRecord())
        .flatMap(SignText::getText4)
        .ifPresent(text ->
            addSummaryContent("Line 4", alert -> Format.bonus(text)));

  }

}
