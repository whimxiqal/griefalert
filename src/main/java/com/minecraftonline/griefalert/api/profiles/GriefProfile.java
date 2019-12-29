package com.minecraftonline.griefalert.api.profiles;

import org.spongepowered.api.data.DataContainer;

public class GriefProfile {

  // action: {break, place, use, interact, death}
  // target: {minecraft:cobblestone, minecraft:diamond_block, ...}
  // ignored_dimensions: {*list of* overworld, nether, the_end}
  // action_color: {blue, white, ...}
  // target_color: {blue, white, ...}
  // dimention_color: {blue, white, ...}

  private DataContainer dataContainer;

  private GriefProfile(DataContainer dataContainer) {
    this.dataContainer = dataContainer;
  }

  public static GriefProfile of(DataContainer dataContainer) {
    return new GriefProfile(dataContainer);
  }

  public DataContainer getDataContainer() {
    return dataContainer;
  }

  public boolean similarTo(GriefProfile other) {
    // TODO: implement
    return false;
  }

  public String printData() {
    return dataContainer.getValues(true).toString();
  }

}
