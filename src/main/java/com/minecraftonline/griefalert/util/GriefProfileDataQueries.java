/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import org.spongepowered.api.data.DataQuery;

public final class GriefProfileDataQueries {


  private GriefProfileDataQueries() {
  }

  public static final DataQuery EVENT = DataQuery.of("event");
  public static final DataQuery TARGET = DataQuery.of("target");
  public static final DataQuery IGNORED = DataQuery.of("ignored_dimensions");
  public static final DataQuery EVENT_COLOR = DataQuery.of("event_color");
  public static final DataQuery TARGET_COLOR = DataQuery.of("target_color");
  public static final DataQuery DIMENSION_COLOR = DataQuery.of("dimension_color");

}
