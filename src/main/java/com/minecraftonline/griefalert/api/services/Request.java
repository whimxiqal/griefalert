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

package com.minecraftonline.griefalert.api.services;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.data.GriefEvent;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * A store for all filtering information necessary to locate stored {@link Alert}s.
 * Used in combination with an {@link AlertService}.
 *
 * @author PietElite
 */
public final class Request implements Serializable {

  public static Request EMPTY = builder().build();

  private final Set<GriefEvent> events;
  private final Set<String> targets;
  private final Set<UUID> playerUuids;
  private final int maximum;

  @Nonnull
  public static Request.Builder builder() {
    return new Request.Builder();
  }

  private Request(@Nonnull Set<GriefEvent> events,
                  @Nonnull Set<String> targets,
                  @Nonnull Set<UUID> playerUuids,
                  int maximum) {
    this.events = events;
    this.targets = targets;
    this.playerUuids = playerUuids;
    this.maximum = maximum;
  }

  @Nonnull
  public Set<GriefEvent> getEvents() {
    return events;
  }

  @Nonnull
  public Set<String> getTargets() {
    return targets;
  }

  @Nonnull
  public Set<UUID> getPlayerUuids() {
    return playerUuids;
  }

  public Optional<Integer> getMaximum() {
    if (maximum < 0) {
      return Optional.empty();
    }
    return Optional.of(maximum);
  }

  public static class Builder {

    private final Set<GriefEvent> events = Sets.newHashSet();
    private final Set<String> targets = Sets.newHashSet();
    private final Set<UUID> playerUuids = Sets.newHashSet();
    private int maximum = -1;

    private Builder() {
    }

    public Request build() {
      return new Request(events, targets, playerUuids, maximum);
    }

    /**
     * Adjusts Request to only get objects containing the given {@link GriefEvent}
     * or any other added one.
     *
     * @param event the event for filtering
     * @return the current builder for chaining
     */
    public Builder addEvent(@Nonnull final GriefEvent event) {
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
    public Builder addTarget(@Nonnull final String target) {
      Preconditions.checkNotNull(target);
      this.targets.add(target);
      return this;
    }

    /**
     * Adjusts Request to only get objects containing the given {@link UUID}
     * or any other added one.
     *
     * @param playerUuid the player uuid for filtering
     * @return the current builder for chaining
     */
    public Builder addPlayerUuid(@Nonnull final UUID playerUuid) {
      Preconditions.checkNotNull(playerUuid);
      this.playerUuids.add(playerUuid);
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
