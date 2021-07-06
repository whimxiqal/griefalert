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

package com.minecraftonline.griefalert.caches;

import com.google.common.collect.HashBasedTable;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import java.util.Collection;
import java.util.Optional;
import org.spongepowered.api.world.World;

/**
 * A cache to store all of the profiles in use on the server for fast retrieval.
 *
 * @author PietElite
 */
public class ProfileCache {

  private final HashBasedTable<GriefEvent, String, GriefProfile> storage = HashBasedTable.create();

  public ProfileCache() {
    load();
  }

  /**
   * Reloads all {@link GriefProfile}s from the storage into this cache.
   */
  public void reload() {
    storage.clear();
    load();
  }

  private void load() {
    try {
      GriefAlert.getInstance().getLogger().info("Loading Grief Profiles from storage into cache...");
      GriefAlert.getInstance().getProfileStorage().retrieve().forEach(this::add);
      GriefAlert.getInstance().getLogger().info("Grief Profiles were loaded into cache.");
    } catch (Exception e) {
      GriefAlert.getInstance().getLogger()
          .error("Could not load Grief Profiles from storage.");
    }
  }

  /**
   * Get a grief profile given some parameters.
   *
   * @param griefEvent The queried grief event
   * @param target     The queried target id
   * @param world      The queried world
   * @return The grief profile that matches the criteria
   */
  public Optional<GriefProfile> getProfileOf(GriefEvent griefEvent,
                                             String target,
                                             World world) {

    Optional<GriefProfile> profileOptional = Optional.ofNullable(storage.get(griefEvent, target));

    // Make sure the dimension is not ignored
    if (profileOptional.isPresent()) {
      if (!profileOptional.get().isIgnoredIn(world.getProperties())) {
        return profileOptional;
      } else {
        return Optional.empty();
      }
    }

    return profileOptional;
  }

  private void add(GriefProfile profile) {
    storage.put(profile.getGriefEvent(), profile.getTarget(), profile);
  }

  /**
   * Get all the <code>GriefProfile</code>s in cache storage.
   *
   * @return A <code>Collection</code> of all
   */
  public Collection<GriefProfile> getProfiles() {
    return storage.values();
  }

}
