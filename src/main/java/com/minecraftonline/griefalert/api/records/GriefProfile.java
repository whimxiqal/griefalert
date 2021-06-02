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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.services.AlertService;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

/**
 * The information in a {@link GriefProfile} informs the {@link AlertService}
 * of which events to watch.
 * If an event matches a {@link GriefProfile}, then an {@link Alert} is generated.
 *
 * @author PietElite
 */
public final class GriefProfile implements Serializable {

  private final String eventId;
  private final String target;
  private final Set<String> ignored;
  private final Map<Colorable, String> colors;
  private final boolean translucent;

  public enum Colorable {
    EVENT,
    TARGET,
    WORLD
  }

  private GriefProfile(@Nonnull final GriefEvent event,
                       @Nonnull final String target,
                       @Nonnull final Set<WorldProperties> ignored,
                       @Nonnull final Map<Colorable, String> colors,
                       boolean translucent) {
    this.eventId = event.getId();
    this.target = target;
    this.ignored = ignored.stream().map(WorldProperties::getWorldName).collect(Collectors.toCollection(Sets::newLinkedHashSet));
    this.colors = Maps.newLinkedHashMap(colors);
    this.translucent = translucent;
  }

  @Nonnull
  public GriefEvent getGriefEvent() {
    return Sponge.getRegistry().getType(GriefEvent.class, eventId)
        .orElseThrow(() -> new RuntimeException(
            "GriefProfile contained an invalid GriefEvent id: "
                + eventId));
  }

  @Nonnull
  public String getTarget() {
    return target;
  }

  /**
   * Return whether this profile is configured such that an
   * event occurring in the given {@link DimensionType} would be ignored
   * by {@link Alert} construction.
   *
   * @param world the world
   * @return true if ignored
   */
  public boolean isIgnoredIn(@Nonnull final WorldProperties world) {
    return ignored.contains(world.getWorldName());
  }

  /**
   * Get a copy of all ignored dimensions for this profile.
   *
   * @return ignored dimensions
   */
  @Nonnull
  public Set<World> getIgnored() {
    Set<World> out = Sets.newHashSet();
    ignored.forEach(worldName -> out.add(Sponge.getServer().getWorld(worldName).orElseThrow(
        () -> new RuntimeException(
            "GriefProfile contained an invalid DimensionType id: "
                + worldName))));
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

  public boolean isTranslucent() {
    return translucent;
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
    return this.eventId.equals(other.eventId)
        && this.target.equals(other.target);
  }

  public static Builder builder(@Nonnull final GriefEvent event,
                                @Nonnull final String target) {
    return new Builder(event, target);
  }

  public static class Builder {

    private final GriefEvent event;
    private final String target;
    private final Set<WorldProperties> ignored = Sets.newHashSet();
    private final Map<Colorable, String> colors = Maps.newHashMap();
    private boolean translucent = false;

    public Builder(@Nonnull final GriefEvent event,
                   @Nonnull final String target) {
      this.event = event;
      this.target = target;
    }

    /**
     * Generate.
     *
     * @return the {@link GriefProfile}
     */
    public GriefProfile build() {
      return new GriefProfile(
          event,
          target,
          ImmutableSet.<WorldProperties>builder().addAll(ignored).build(),
          ImmutableMap.<Colorable, String>builder().putAll(colors).build(),
          translucent);
    }

    /**
     * Add the {@link DimensionType}s to the set of ignored dimensions for this alert.
     *
     * @param worlds the worlds
     * @return builder for chaining
     */
    public Builder addAllIgnored(@Nonnull Collection<WorldProperties> worlds) {
      for (WorldProperties world : worlds) {
        addIgnored(world);
      }
      return this;
    }

    /**
     * Add the {@link DimensionType} to the set of ignored dimensions for this alert.
     *
     * @param world the world
     * @return builder for chaining
     */
    public Builder addIgnored(@Nullable WorldProperties world) {
      if (world != null) {
        ignored.add(world);
      }
      return this;
    }

    public Builder setTranslucent(boolean translucent) {
      this.translucent = translucent;
      return this;
    }

    /**
     * Map a {@link TextColor} to the enumerated colored components of this
     * {@link GriefProfile} for printing the {@link Text} of {@link Alert}s.
     *
     * @param component the portion of the print message
     * @param color     the color
     * @return builder for chaining
     */
    public Builder putColored(@Nonnull Colorable component, @Nonnull TextColor color) {
      colors.put(component, color.getId());
      return this;
    }

  }

}
