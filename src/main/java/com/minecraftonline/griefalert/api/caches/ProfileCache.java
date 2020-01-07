/* Created by PietElite */

package com.minecraftonline.griefalert.api.caches;

import com.google.common.collect.HashBasedTable;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.storage.ProfileStorage;
import com.minecraftonline.griefalert.util.General;

import java.util.Collection;
import java.util.Optional;

import org.spongepowered.api.world.DimensionType;

/**
 * A cache to store all of the profiles in use on the server for fast retrieval.
 *
 * @author PietElite
 */
public class ProfileCache {

  private HashBasedTable<GriefEvent, String, GriefProfile> storage = HashBasedTable.create();

  public ProfileCache() {
    this.load();
  }

  /**
   * Reloads all {@link GriefProfile}s from the storage into this cache.
   */
  public void reload() {
    GriefAlert.getInstance().getLogger().info("Reloading Grief Profile cache...");
    storage.clear();
    load();
    GriefAlert.getInstance().getLogger().info("Grief Profile cache reload complete.");
  }

  private void load() {

    // Get all other profiles from the onsite profile list
    ProfileStorage profileStorage = GriefAlert.getInstance().getProfileStorage();
    try {
      GriefAlert.getInstance().getLogger().info("Loading Grief Profiles from SQL into cache...");
      profileStorage.retrieve().forEach(this::add);
      GriefAlert.getInstance().getLogger().info("Grief Profiles were loaded into cache.");
    } catch (Exception e) {
      GriefAlert.getInstance().getLogger()
          .error("Could not load Grief Profiles from SQL database. "
              + "See debug logger for more info.");
      General.printStackTraceToDebugLogger(e);
    }
  }

  /**
   * Get a <code>GriefProfile</code> which matches the given parameters. All of these parameters
   * are required to identify a <code>GriefProfile</code> from a list.
   *
   * @param griefEvent    The queried <code>GriefEvent</code>
   * @param target        The queried target id
   * @param dimensionType The queried <code>DimensionType</code>
   * @return An <code>Optional</code> containing the <code>GriefAlert</code> that matches the
   *         criteria, or an empty <code>Optional</code> if on does not exist
   */
  public Optional<GriefProfile> getProfileOf(GriefEvent griefEvent,
                                             String target,
                                             DimensionType dimensionType) {

    Optional<GriefProfile> profileOptional = Optional.ofNullable(storage.get(griefEvent, target));

    // Make sure the dimension is not ignored
    if (profileOptional.isPresent()) {
      if (!profileOptional.get().isIgnoredIn(dimensionType)) {
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
