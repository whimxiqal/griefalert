/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.prism;

import com.helion3.prism.api.records.PrismRecord;
import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.data.SignText;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.PrismUtil;

import java.util.Optional;

public class SignBreakAlert extends PrismAlert {

  /**
   * Default constructor for a sign break alert. This add all the 'extra'
   * information about the text on its lines
   *
   * @param griefProfile the grief profile for this alert
   * @param prismRecord  the prism record triggering this alert
   */
  public SignBreakAlert(GriefProfile griefProfile, PrismRecord prismRecord) {
    super(griefProfile, prismRecord);

    Optional<SignText> signText = PrismUtil.getBrokenSignText(prismRecord.getDataContainer());

    signText.flatMap(SignText::getText1)
        .ifPresent(text ->
            addDetail(Detail.of(
                "Line 1",
                null,
                Format.bonus(text))));

    signText.flatMap(SignText::getText2)
        .ifPresent(text ->
            addDetail(Detail.of(
                "Line 2",
                null,
                Format.bonus(text))));

    signText.flatMap(SignText::getText3)
        .ifPresent(text ->
            addDetail(Detail.of(
                "Line 3",
                null,
                Format.bonus(text))));

    signText.flatMap(SignText::getText4)
        .ifPresent(text ->
            addDetail(Detail.of(
                "Line 4",
                null,
                Format.bonus(text))));

  }

}
