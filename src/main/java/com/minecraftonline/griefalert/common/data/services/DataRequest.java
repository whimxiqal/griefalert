/*
 * This file is part of Prism, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.minecraftonline.griefalert.common.data.services;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.minecraftonline.griefalert.common.data.struct.PrismEvent;
import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * A store for all filtering information necessary to locate logged events.
 */
public final class DataRequest implements Serializable {

  private static final long serialVersionUID = 4369983541428028962L;
  private final Set<PrismEvent> events;
  private final Set<String> targets;
  private final Set<UUID> playerUuids;
  private final Set<UUID> worldUuids;
  private final Range<Integer> xRange;
  private final Range<Integer> yRange;
  private final Range<Integer> zRange;
  private final Date earliest;
  private final Date latest;
  private final Set<Object> flags;

  private DataRequest(@Nonnull Set<PrismEvent> events,
                      @Nonnull Set<String> targets,
                      @Nonnull Set<UUID> playerUuids,
                      @Nonnull Set<UUID> worldUuids,
                      Range<Integer> xRange,
                      Range<Integer> yRange,
                      Range<Integer> zRange,
                      Date earliest,
                      Date latest,
                      @Nonnull Set<Object> flags) {
    this.events = events;
    this.targets = targets;
    this.playerUuids = playerUuids;
    this.worldUuids = worldUuids;
    this.xRange = xRange;
    this.yRange = yRange;
    this.zRange = zRange;
    this.earliest = earliest;
    this.latest = latest;
    this.flags = flags;
  }

  /**
   * Create a {@link DataRequest.Builder} to construct a {@link DataRequest} to use in a {@link DataService}.
   *
   * @return a builder
   */
  @Nonnull
  public static Builder builder() {
    return new Builder();
  }

  @Nonnull
  public Set<PrismEvent> getEvents() {
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

  @Nonnull
  public Set<UUID> getWorldUuids() {
    return worldUuids;
  }

  @Nonnull
  public Optional<Range<Integer>> getxRange() {
    return Optional.ofNullable(xRange);
  }

  @Nonnull
  public Optional<Range<Integer>> getyRange() {
    return Optional.ofNullable(yRange);
  }

  @Nonnull
  public Optional<Range<Integer>> getzRange() {
    return Optional.ofNullable(zRange);
  }

  @Nonnull
  public Optional<Date> getEarliest() {
    return Optional.ofNullable(earliest);
  }

  @Nonnull
  public Optional<Date> getLatest() {
    return Optional.ofNullable(latest);
  }

  @Nonnull
  public Set<Object> getFlags() {
    return flags;
  }

  public static class Builder {

    private final Set<PrismEvent> events = Sets.newHashSet();
    private final Set<String> targets = Sets.newHashSet();
    private final Set<UUID> playerUuids = Sets.newHashSet();
    private final Set<UUID> worldUuids = Sets.newHashSet();
    private Range<Integer> xRange = null;
    private Range<Integer> yRange = null;
    private Range<Integer> zRange = null;
    private Date earliest = null;
    private Date latest = null;
    private final Set<Object> flags = Sets.newHashSet();

    private Builder() {
    }

    public DataRequest build() {
      return new DataRequest(events, targets, playerUuids, worldUuids, xRange, yRange, zRange, earliest, latest, flags);
    }

    @SuppressWarnings("unused")
    public Builder addEvent(@Nonnull PrismEvent event) {
      Preconditions.checkNotNull(event);
      this.events.add(event);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder addTarget(@Nonnull String target) {
      Preconditions.checkNotNull(target);
      this.targets.add(target);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder addPlayerUuid(@Nonnull UUID playerUuid) {
      Preconditions.checkNotNull(playerUuid);
      this.playerUuids.add(playerUuid);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder addWorldUuid(@Nonnull UUID worldUuid) {
      Preconditions.checkNotNull(worldUuid);
      this.worldUuids.add(worldUuid);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder setxRange(int lower, int upper) {
      this.xRange = Range.closed(lower, upper);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder setyRange(int lower, int upper) {
      this.yRange = Range.closed(lower, upper);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder setzRange(int lower, int upper) {
      this.zRange = Range.closed(lower, upper);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder setEarliest(Date earliest) {
      this.earliest = earliest;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder setLatest(Date latest) {
      this.latest = latest;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder addFlag(@Nonnull Object flag) {
      Preconditions.checkNotNull(flag);
      this.flags.add(flag);
      return this;
    }

  }

}
