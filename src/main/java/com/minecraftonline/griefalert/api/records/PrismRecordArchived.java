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

package com.minecraftonline.griefalert.api.records;

import com.helion3.prism.api.records.PrismRecord;
import javax.annotation.Nonnull;
import org.spongepowered.api.data.DataContainer;

/**
 * A record type that stores an archived {@link PrismRecord}. All methods are
 * the same except that the {@link DataContainer} with all the data is copied
 * for the purpose of saving the data before <code>Prism</code> edits the
 * original <code>PrismRecord</code> later.
 *
 * @author PietElite
 */
public final class PrismRecordArchived {

  private final String event;
  private final Object source;
  private final DataContainer dataContainer;

  private PrismRecordArchived(@Nonnull final PrismRecord record) {
    this.event = record.getEvent();
    this.source = record.getSource();
    this.dataContainer = record.getDataContainer().copy();
  }

  /**
   * General factory.
   *
   * @param record the record to form into an archived copy
   * @return the archived copy
   */
  @Nonnull
  public static PrismRecordArchived of(@Nonnull final PrismRecord record) {
    return new PrismRecordArchived(record);
  }

  @Nonnull
  public String getEvent() {
    return event;
  }

  @Nonnull
  @SuppressWarnings("unused")
  public Object getSource() {
    return source;
  }

  @Nonnull
  public DataContainer getDataContainer() {
    return dataContainer;
  }

}
