/* Created by PietElite */

package com.minecraftonline.griefalert.api.records;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import javax.annotation.Nonnull;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.world.DimensionType;


/**
 * A holder for all information about a <code>GriefProfile</code>. To store data about what a
 * <code>GriefProfile</code> has, it stores data in a {@link DataContainer} using
 * {@link org.spongepowered.api.data.DataQuery} as keys for the data. The queries for the
 * <code>GriefAlert</code>s are held in the {@link GriefProfileDataQueries} utility class.
 * If a <code>GriefProfile</code> doesn't have the necessary data when it's queried from a
 * given method, it might throw a {@link MalformedProfileException}.
 *
 * @author PietElite
 */
public class GriefProfile {

  private final DataContainer dataContainer;

  private GriefProfile(@Nonnull final DataContainer dataContainer) {
    this.dataContainer = dataContainer;
  }

  @Nonnull
  public static GriefProfile of(@Nonnull final DataContainer dataContainer) {
    return new GriefProfile(dataContainer);
  }

  /**
   * A getter for the <code>DataContainer</code> which holds all information about this
   * <code>GriefProfile</code>.
   *
   * @return the <code>DataContainer</code>
   */
  @Nonnull
  public DataContainer getDataContainer() {
    return dataContainer;
  }

  /**
   * Tester for validity.
   *
   * @return true if this <code>GriefProfile</code> has all necessary parts
   */
  public boolean isValid() {
    return dataContainer.getString(GriefProfileDataQueries.EVENT).isPresent()
        && dataContainer.getString(GriefProfileDataQueries.TARGET).isPresent();
  }

  /**
   * Getter for the <code>GriefEvent</code>.
   *
   * @return the <code>GriefEvent</code>
   * @see MalformedProfileException
   */
  @Nonnull
  public GriefEvent getGriefEvent() {
    return dataContainer.getCatalogType(GriefProfileDataQueries.EVENT, GriefEvent.class)
        .orElseThrow(() ->
            new MalformedProfileException("No GriefEvent found in GriefProfile: \n" + printData()));
  }

  /**
   * Getter for the target.
   *
   * @return the target
   * @see MalformedProfileException
   */
  @Nonnull
  public String getTarget() {
    return dataContainer.getString(GriefProfileDataQueries.TARGET)
        .orElseThrow(() ->
            new MalformedProfileException("No Target found in GriefProfile: \n" + printData()));
  }

  /**
   * Return whether this <code>GriefProfile</code> is configured such that an
   * event occurring in the given <code>DimensionType</code> would be ignored
   * by alert construction.
   *
   * @param dimensionType the <code>DimensionType</code>
   * @return true if this <code>DimensionType</code> is ignored by occurrences
   *         matching this <code>GriefProfile</code>
   */
  public boolean isIgnoredIn(@Nonnull final DimensionType dimensionType) {
    return dataContainer.getCatalogTypeList(GriefProfileDataQueries.IGNORED, DimensionType.class)
        .map((list) -> list.contains(dimensionType)).orElse(false);
  }

  private String printData() {
    return dataContainer.getValues(true).toString();
  }

  public static class MalformedProfileException extends IllegalStateException {
    MalformedProfileException(String s) {
      super(s);
    }
  }

}
