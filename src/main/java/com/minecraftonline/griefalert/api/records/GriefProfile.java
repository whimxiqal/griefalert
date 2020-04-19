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
  private final Map<Colored, String> colors = Maps.newHashMap();

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
    return colors.putIfAbsent(component, color.getId()) != null;
  }

  @Nonnull
  public Map<Colored, String> getColored() {
    Map<Colored, String> out = Maps.newHashMap();
    out.putAll(colors);
    return out;
  }

  /**
   * Return whether this <code>GriefProfile</code> is configured such that an
   * event occurring in the given <code>DimensionType</code> would be ignored
   * by alert construction.
   *
   * @param dimensionType the <code>DimensionType</code>
   * @return true if this <code>DimensionType</code> is ignored by occurrences
   * matching this <code>GriefProfile</code>
   */
  public boolean isIgnoredIn(@Nonnull final DimensionType dimensionType) {
    return ignored.contains(dimensionType);
  }

  @Nonnull
  public Set<DimensionType> getIgnored() {
    Set<DimensionType> out = Sets.newHashSet();
    out.addAll(ignored);
    return out;
  }

  public Optional<TextColor> getColored(@Nonnull Colored component) {
    return Optional.ofNullable(colors.get(component))
            .flatMap(s -> Sponge.getRegistry().getType(TextColor.class, s));
  }

  public boolean isColored() {
    return !colors.isEmpty();
  }

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
