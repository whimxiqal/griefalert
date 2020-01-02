package com.minecraftonline.griefalert.api.records;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.exceptions.ProfileMalformedException;
import com.minecraftonline.griefalert.util.GriefEvents;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.DimensionType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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


  public GriefEvent getGriefEvent() throws ProfileMalformedException {
    Optional<GriefEvent> eventOptional = dataContainer.get(GriefProfileDataQueries.EVENT).map((object) -> (GriefEvent) object);

    if (!eventOptional.isPresent()) {
      throw new ProfileMalformedException("No GriefEvent found in GriefProfile: \n" + printData());
    }

    return eventOptional.get();
  }


  public String getTarget() throws ProfileMalformedException {
    Optional<String> targetOptional = dataContainer.getString(GriefProfileDataQueries.TARGET);

    if (!targetOptional.isPresent()) {
      throw new ProfileMalformedException("No Target found in GriefProfile: \n" + printData());
    }

    return targetOptional.get();
  }


  public boolean isIgnoredIn(DimensionType dimensionType) {
    Optional<List<?>> dimensionListOptional = dataContainer.getList(GriefProfileDataQueries.IGNORED_DIMENSIONS);
    return dimensionListOptional.map(objects -> objects.contains(dimensionType.getId())).orElse(false);
  }

  public String printData() {
    return dataContainer.getValues(true).toString();
  }

}
