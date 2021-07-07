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

package com.minecraftonline.griefalert.common.alert.alerts.inspections;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvent;
import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * An inspection request.
 */
public final class InspectionRequest implements Serializable {

  public static InspectionRequest EMPTY = builder().build();

  private final Set<GriefEvent> events;
  private final Set<String> targets;
  private final Set<UUID> officerUuids;
  private final Set<UUID> grieferUuids;
  private final int maximum;

  private InspectionRequest(@NotNull Set<GriefEvent> events,
                            @NotNull Set<String> targets,
                            @NotNull Set<UUID> officerUuids,
                            @NotNull Set<UUID> grieferUuids,
                            int maximum) {
    this.events = events;
    this.targets = targets;
    this.officerUuids = officerUuids;
    this.grieferUuids = grieferUuids;
    this.maximum = maximum;
  }

  @NotNull
  public static InspectionRequest.Builder builder() {
    return new InspectionRequest.Builder();
  }

  @NotNull
  public Set<GriefEvent> getEvents() {
    return events;
  }

  @NotNull
  public Set<String> getTargets() {
    return targets;
  }

  @NotNull
  public Set<UUID> getOfficerUuids() {
    return officerUuids;
  }

  @NotNull
  public Set<UUID> getGrieferUuids() {
    return grieferUuids;
  }

  /**
   * Get the maximum number of returned values from the query.
   *
   * @return the maximum, if one exists
   */
  @NotNull
  public Optional<Integer> getMaximum() {
    if (maximum < 0) {
      return Optional.empty();
    }
    return Optional.of(maximum);
  }

  /**
   * A builder for a request.
   */
  public static class Builder {

    private final Set<GriefEvent> events = Sets.newHashSet();
    private final Set<String> targets = Sets.newHashSet();
    private final Set<UUID> officerUuids = Sets.newHashSet();
    private final Set<UUID> grieferUuid = Sets.newHashSet();
    private int maximum = -1;

    private Builder() {
    }

    public InspectionRequest build() {
      return new InspectionRequest(events, targets, officerUuids, grieferUuid, maximum);
    }

    /**
     * Adjusts Request to only get objects containing the given {@link GriefEvent}
     * or any other added one.
     *
     * @param event the event for filtering
     * @return the current builder for chaining
     */
    public Builder addEvent(@NotNull final GriefEvent event) {
      Preconditions.checkNotNull(event);
      this.events.add(event);
      return this;
    }

    /**
     * Adjusts Request to only get objects containing the given target id
     * or any other added one.
     *
     * @param target the target id for filtering
     * @return the current builder for chaining
     */
    public Builder addTarget(@NotNull final String target) {
      Preconditions.checkNotNull(target);
      this.targets.add(target);
      return this;
    }

    /**
     * Adjusts Request to only get objects containing the given {@link UUID}
     * or any other added one.
     *
     * @param officerUuid the player uuid for filtering
     * @return the current builder for chaining
     */
    public Builder addOfficerUuid(@NotNull final UUID officerUuid) {
      Preconditions.checkNotNull(officerUuid);
      this.officerUuids.add(officerUuid);
      return this;
    }

    /**
     * Adjusts Request to only get objects containing the given {@link UUID}
     * or any other added one.
     *
     * @param grieferUuid the player uuid for filtering
     * @return the current builder for chaining
     */
    public Builder addGrieferUuid(@NotNull final UUID grieferUuid) {
      Preconditions.checkNotNull(grieferUuid);
      this.grieferUuid.add(grieferUuid);
      return this;
    }

    /**
     * Set the maximum number of results to get from this request.
     * Input cannot be negative.
     *
     * @param maximum limit of results
     * @return the current builder for chaining
     * @throws IllegalArgumentException if input is negative
     */
    public Builder setMaximum(int maximum) {
      if (maximum < 0) {
        throw new IllegalArgumentException("Maximum for Request cannot be negative");
      }
      this.maximum = maximum;
      return this;
    }

  }

}
