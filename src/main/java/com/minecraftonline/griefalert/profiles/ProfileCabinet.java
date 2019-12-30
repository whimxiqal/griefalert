package com.minecraftonline.griefalert.profiles;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;

import java.util.Collection;
import java.util.Optional;

import com.minecraftonline.griefalert.api.records.GriefProfiles;
import org.spongepowered.api.world.DimensionType;

public class ProfileCabinet {

  private Table<GriefEvent, String, GriefProfile> storage = HashBasedTable.create();

  /**
   * Constructor for a new museum to hold all Grief Profiles. This museum should be considered
   * immutable, and should only be used as a tool to check possible Grief Events against.
   */
  public ProfileCabinet() {
    this.load();
  }

  /**
   * Load in all data from the Grief Profiles file on the local machine.
   */
  public void reload() {
    storage.clear();
    load();
    GriefAlert.getInstance().getLogger().warn("Grief Profiles were imported to profile museum cache.");
  }

  private void load() {

    // Add all constant GriefProfiles in load as well
    add(GriefProfiles.PLACE_SIGN);
    add(GriefProfiles.BREAK_SIGN);
    add(GriefProfiles.BREAK_COBBLESTONE_TEST);
    add(GriefProfiles.KILL_COW_TEST);
    add(GriefProfiles.USE_EGG_TEST);
    // ...

    // Get all other profiles from the onsite profile list
    // importer.retrieve().forEach(this::add);
  }


  public Optional<GriefProfile> getProfileOf(GriefEvent griefEvent, String target, DimensionType dimensionType) {

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

  public boolean add(GriefProfile profile) {
    if (storage.contains(profile.getGriefEvent(), profile.getTarget())) {
      return false;
    } else {
      storage.put(profile.getGriefEvent(), profile.getTarget(), profile);
      return true;
    }
  }

  public void store(GriefProfile profile) throws Exception {
    GriefAlert.getInstance().getProfileStorage().connect();
    GriefAlert.getInstance().getProfileStorage().write(profile);
  }

  public Collection<GriefProfile> getProfiles() {
    return storage.values();
  }
}
