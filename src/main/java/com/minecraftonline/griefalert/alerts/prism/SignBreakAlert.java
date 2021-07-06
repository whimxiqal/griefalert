/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert.alerts.prism;

import com.helion3.prism.api.records.PrismRecord;
import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.data.SignText;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.PrismUtil;
import java.util.Optional;

/**
 * An alert caused by breaking a sign.
 */
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
