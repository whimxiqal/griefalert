/* Created by PietElite */

package com.minecraftonline.griefalert.api.records;

import com.helion3.prism.api.records.PrismRecord;
import org.spongepowered.api.data.DataContainer;

public final class PrismRecordArchived {

  private final String event;
  private final Object source;
  private final DataContainer dataContainer;

  private PrismRecordArchived(PrismRecord record) {
    this.event = record.getEvent();
    this.source = record.getSource();
    this.dataContainer = record.getDataContainer().copy();
  }

  public static PrismRecordArchived of(PrismRecord record) {
    return new PrismRecordArchived(record);
  }

  public String getEvent() {
    return event;
  }

  public Object getSource() {
    return source;
  }

  public DataContainer getDataContainer() {
    return dataContainer;
  }

}
