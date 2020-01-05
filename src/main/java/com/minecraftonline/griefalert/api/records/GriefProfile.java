package com.minecraftonline.griefalert.api.records;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.exceptions.ProfileMalformedException;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;

import java.util.List;
import java.util.Optional;

public class GriefProfile {

  // action: {break, place, use, interact, death}
  // target: {minecraft:cobblestone, minecraft:diamond_block, ...}
  // ignored_dimensions: {*list of* overworld, nether, the_end}
  // action_color: {blue, white, ...}
  // target_color: {blue, white, ...}
  // dimension_color: {blue, white, ...}

  private final DataContainer dataContainer;

  private GriefProfile(DataContainer dataContainer) {
    this.dataContainer = dataContainer;
  }

  public static GriefProfile of(DataContainer dataContainer) {
    return new GriefProfile(dataContainer);
  }

  public DataContainer getDataContainer() {
    return dataContainer;
  }

  public boolean isValid() {
    return dataContainer.getString(GriefProfileDataQueries.EVENT).isPresent()
        && dataContainer.getString(GriefProfileDataQueries.TARGET).isPresent();
  }

  public GriefEvent getGriefEvent() {
    Optional<GriefEvent> eventOptional = dataContainer.getCatalogType(GriefProfileDataQueries.EVENT, GriefEvent.class);

    if (!eventOptional.isPresent()) {
      throw new ProfileMalformedException("No GriefEvent found in GriefProfile: \n" + printData());
    }

    return eventOptional.get();
  }


  public String getTarget() {
    Optional<String> targetOptional = dataContainer.getString(GriefProfileDataQueries.TARGET);

    if (!targetOptional.isPresent()) {
      throw new ProfileMalformedException("No Target found in GriefProfile: \n" + printData());
    }

    return targetOptional.get();
  }


  public boolean isIgnoredIn(DimensionType dimensionType) {
    return dataContainer.getCatalogTypeList(GriefProfileDataQueries.IGNORED, DimensionType.class)
        .map((list) -> list.contains(dimensionType)).orElse(false);
  }

  public String printData() {
    return dataContainer.getValues(true).toString();
  }

}
