/* Created by PietElite */

package com.minecraftonline.griefalert.api.records;

import com.helion3.prism.api.records.PrismRecord;
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

  private PrismRecordArchived(PrismRecord record) {
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
  public static PrismRecordArchived of(PrismRecord record) {
    return new PrismRecordArchived(record);
  }

  public String getEvent() {
    return event;
  }

  @SuppressWarnings("unused")
  public Object getSource() {
    return source;
  }

  public DataContainer getDataContainer() {
    return dataContainer;
  }

}
