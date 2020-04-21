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

package com.minecraftonline.griefalert.api.alerts;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * Stores the information the time someone inspected an {@link Alert}.
 *
 * @author PietElite
 */
public final class AlertInspection implements Serializable {

  private final UUID officerUuid;
  private final Date checked;

  public AlertInspection(@Nonnull final UUID officerUuid, @Nonnull final Date checked) {
    this.officerUuid = officerUuid;
    this.checked = checked;
  }

  @Nonnull
  public Date getChecked() {
    return checked;
  }

  @Nonnull
  public UUID getOfficerUuid() {
    return officerUuid;
  }

}
