package com.minecraftonline.griefalert.util;

import org.spongepowered.api.data.DataQuery;

public final class GriefProfileDataQueries {

  private GriefProfileDataQueries() {
  }

  public static final DataQuery EVENT = DataQuery.of("event");
  public static final DataQuery TARGET = DataQuery.of("target");
  public static final DataQuery IGNORE_OVERWORLD = DataQuery.of("ignore_overworld");
  public static final DataQuery IGNORE_NETHER = DataQuery.of("ignore_nether");
  public static final DataQuery IGNORE_THE_END = DataQuery.of("ignore_the_end");
  public static final DataQuery EVENT_COLOR = DataQuery.of("event_color");
  public static final DataQuery TARGET_COLOR = DataQuery.of("target_color");
  public static final DataQuery DIMENSION_COLOR = DataQuery.of("dimension_color");

}
