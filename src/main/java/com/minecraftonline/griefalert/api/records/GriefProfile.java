package com.minecraftonline.griefalert.api.records;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.exceptions.ProfileMalformedException;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import com.minecraftonline.griefalert.util.Registry;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;

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

  public boolean isValid() {
    return dataContainer.getString(GriefProfileDataQueries.EVENT).isPresent()
        && dataContainer.getString(GriefProfileDataQueries.TARGET).isPresent();
  }

  public GriefEvent getGriefEvent() throws ProfileMalformedException {
    Optional<String> eventIdOptional = dataContainer.getString(GriefProfileDataQueries.EVENT);

    if (!eventIdOptional.isPresent()) {
      throw new ProfileMalformedException("No GriefEvent found in GriefProfile: \n" + printData());
    }

    Optional<GriefEvent> eventOptional = Registry.lookupGriefEvent(eventIdOptional.get());

    if (!eventOptional.isPresent()) {
      throw new ProfileMalformedException("Invalid GriefEvent id saved in GriefProfile: \n" + printData());
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
    DataQuery query;
    if (dimensionType == DimensionTypes.OVERWORLD) {
      query = GriefProfileDataQueries.IGNORE_OVERWORLD;
    } else if (dimensionType == DimensionTypes.NETHER) {
      query = GriefProfileDataQueries.IGNORE_NETHER;
    } else {
      query = GriefProfileDataQueries.IGNORE_THE_END;
    }

    return dataContainer.getBoolean(query).orElse(false);

  }

  public String printData() {
    return dataContainer.getValues(true).toString();
  }

}
