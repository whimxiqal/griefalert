/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert.api.records;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.services.AlertService;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.DimensionType;

/**
 * The information in a {@link GriefProfile} informs the {@link AlertService}
 * of which events to watch.
 * If an event matches a {@link GriefProfile}, then an {@link Alert} is generated.
 *
 * @author PietElite
 */
public final class GriefProfile implements Serializable {

  private final GriefEvent event;
  private final String target;
  private final Set<DimensionType> ignored = Sets.newHashSet();
  private final Map<Colorable, String> colors = Maps.newHashMap();

  public enum Colorable {
    EVENT,
    TARGET,
    DIMENSION
  }

  private GriefProfile(@Nonnull final GriefEvent event,
                       @Nonnull final String target) {
    this.event = event;
    this.target = target;
  }

  /**
   * Factory method for a {@link GriefProfile}.
   *
   * @param event  the event describing the type of action performed by a player
   * @param target the minecraft id for the target of this action
   * @return the generated {@link GriefProfile}
   */
  @Nonnull
  public static GriefProfile of(@Nonnull final GriefEvent event,
                                @Nonnull final String target) {
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
   * @param dimension the dimension type
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
  public boolean putColored(@Nonnull Colorable component, @Nonnull TextColor color) {
    return colors.putIfAbsent(component, color.getId()) != null;
  }

  /**
   * Return whether this profile is configured such that an
   * event occurring in the given {@link DimensionType} would be ignored
   * by {@link Alert} construction.
   *
   * @param dimensionType the dimension
   * @return true if ignored
   */
  public boolean isIgnoredIn(@Nonnull final DimensionType dimensionType) {
    return ignored.contains(dimensionType);
  }

  /**
   * Get a copy of all ignored dimensions for this profile.
   *
   * @return ignored dimensions
   */
  @Nonnull
  public Set<DimensionType> getIgnored() {
    Set<DimensionType> out = Sets.newHashSet();
    out.addAll(ignored);
    return out;
  }

  /**
   * Gets whether this profile has any specified colored components.
   *
   * @return true if there exists a special colored component
   */
  public boolean isColored() {
    return !colors.isEmpty();
  }

  /**
   * Get the {@link TextColor} for the given component.
   *
   * @param component the component of an profile
   * @return an optional possibly containing the {@link TextColor}
   */
  @Nonnull
  public Optional<TextColor> getColored(@Nonnull Colorable component) {
    return Optional.ofNullable(colors.get(component))
        .flatMap(s -> Sponge.getRegistry().getType(TextColor.class, s));
  }

  /**
   * Return a copy of the map of colored components.
   *
   * @return the mapped components
   */
  @Nonnull
  public Map<Colorable, TextColor> getAllColored() {
    Map<Colorable, TextColor> out = Maps.newHashMap();
    colors.forEach((colored, s) ->
        out.put(colored, Sponge.getRegistry().getType(TextColor.class, s).get()));
    return out;
  }

  /**
   * Gives whether or not the other object is a {@link GriefProfile}
   * and has the same {@link GriefEvent} and target.
   *
   * @param obj the other object
   * @return true if matches the above criteria
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof GriefProfile)) {
      return false;
    }
    GriefProfile other = (GriefProfile) obj;
    return this.event.equals(other.event)
        && this.target.equals(other.target);
  }
}
