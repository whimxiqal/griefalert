package com.minecraftonline.griefalert.util;

import org.spongepowered.api.data.DataQuery;

public final class AlertDataQueries {

  private AlertDataQueries() {
  }

  public static final DataQuery ALERT_DATA_PRISM_RECORD = DataQuery.of("PrismRecord");
  public static final DataQuery ALERT_DATA_INTERACT_SECONDARY_EVENT = DataQuery.of("InteractSecondaryEvent");

}
