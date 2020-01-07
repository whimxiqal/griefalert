/* Created by PietElite */

package com.minecraftonline.griefalert.api.records;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.exceptions.ProfileMalformedException;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.DimensionType;

import java.util.Optional;

/**
 * TODO
 *
 * @author PietElite
 */
public class GriefProfile {

  private final DataContainer dataContainer;

  private GriefProfile(DataContainer dataContainer) {
    this.dataContainer = dataContainer;
  }

  public static GriefProfile of(DataContainer dataContainer) {
    return new GriefProfile(dataContainer);
  }

  /**
   * TODO
   * @return
   */
  public DataContainer getDataContainer() {
    return dataContainer;
  }

  /**
   * TODO
   * @return
   */
  public boolean isValid() {
    return dataContainer.getString(GriefProfileDataQueries.EVENT).isPresent()
        && dataContainer.getString(GriefProfileDataQueries.TARGET).isPresent();
  }

  /**
   * TODO
   * @return
   */
  public GriefEvent getGriefEvent() {
    return dataContainer.getCatalogType(GriefProfileDataQueries.EVENT, GriefEvent.class)
        .orElseThrow(() ->
            new ProfileMalformedException("No GriefEvent found in GriefProfile: \n" + printData()));
  }

  /**
   * TODO
   * @return
   */
  public String getTarget() {
    return dataContainer.getString(GriefProfileDataQueries.TARGET)
        .orElseThrow(() ->
            new ProfileMalformedException("No Target found in GriefProfile: \n" + printData()));
  }

  /**
   * TODO
   * @param dimensionType
   * @return
   */
  public boolean isIgnoredIn(DimensionType dimensionType) {
    return dataContainer.getCatalogTypeList(GriefProfileDataQueries.IGNORED, DimensionType.class)
        .map((list) -> list.contains(dimensionType)).orElse(false);
  }

  /**
   * TODO
   * @return
   */
  public String printData() {
    return dataContainer.getValues(true).toString();
  }

}
