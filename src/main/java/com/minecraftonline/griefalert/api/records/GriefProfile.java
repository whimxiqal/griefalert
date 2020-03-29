/* Created by PietElite */

package com.minecraftonline.griefalert.api.records;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.data.GriefEvent;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.DimensionType;


/**
 * A holder for all information about a <code>GriefProfile</code>, which informs
 * <code>GriefAlert</code> of which events to watch and send into {@link Alert}s for
 * notifying staff members.
 *
 * @author PietElite
 */
public class GriefProfile implements Serializable {

  private final GriefEvent event;
  private final String target;
  private final Set<DimensionType> ignored = Sets.newHashSet();
  private final Map<Colored, TextColor> colors = Maps.newHashMap();

  public enum Colored {
    EVENT,
    TARGET,
    DIMENSION
  }

  private GriefProfile(@Nonnull GriefEvent event, @Nonnull String target) {
    this.event = event;
    this.target = target;
  }

  @Nonnull
  public static GriefProfile of(@Nonnull GriefEvent event, @Nonnull String target) {
    return new GriefProfile(event, target);
  }

  @Nonnull
  public GriefEvent getGriefEvent() {
    return event;
  }

  @Nonnull
  public String getTarget() {
    return target;
  }

  /**
   * Add the {@link DimensionType} to the set of ignored dimensions for this alert.
   *
   * @param dimension the dimension
   * @return false if the set already contains the dimension
   */
  public boolean addIgnored(@Nonnull DimensionType dimension) {
    return ignored.add(dimension);
  }

  /**
   * Map a {@link TextColor} to the enumerated colored components of this
   * {@link GriefProfile} for printing the {@link Text} of {@link Alert}s.
   *
   * @param component the portion of the print message
   * @param color     the color
   * @return false if this component already has a color
   */
  public boolean putColored(@Nonnull Colored component, @Nonnull TextColor color) {
    return colors.putIfAbsent(component, color) != null;
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
    return ignored.contains(dimensionType);
  }

  public Optional<TextColor> getColored(@Nonnull Colored component) {
    return Optional.ofNullable(colors.get(component));
  }

  public Text print() {
    // TODO implement
    return Text.EMPTY;
  }

}
