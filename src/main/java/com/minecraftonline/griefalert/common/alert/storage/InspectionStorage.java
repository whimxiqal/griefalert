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

package com.minecraftonline.griefalert.common.alert.storage;

import com.minecraftonline.griefalert.common.alert.alerts.inspections.AlertInspection;
import com.minecraftonline.griefalert.common.alert.alerts.inspections.InspectionRequest;
import com.minecraftonline.griefalert.common.alert.records.GriefProfile;
import java.util.Collection;
import javax.annotation.Nonnull;

/**
 * An interface for persistent storage of {@link GriefProfile}s.
 *
 * @author PietElite
 */
public interface InspectionStorage {

  /**
   * Write a {@link AlertInspection} into persistent storage.
   *
   * @param inspection The GriefProfile to add
   * @return false if a {@link AlertInspection} already exists too similar to the input one
   */
  boolean write(@Nonnull final AlertInspection inspection);

  /**
   * Get all {@link GriefProfile}s saved in persistent storage.
   *
   * @return a collection of {@link GriefProfile}s
   */
  @Nonnull
  Collection<AlertInspection> query(@Nonnull final InspectionRequest inspectionRequest);

}
