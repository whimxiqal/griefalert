package com.minecraftonline.griefalert.profiles;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.helion3.prism.api.records.PrismRecord;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.profiles.GriefProfile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.minecraftonline.griefalert.api.profiles.GriefProfiles;
import com.minecraftonline.griefalert.profiles.io.Exporter;
import com.minecraftonline.griefalert.profiles.io.Importer;
import com.minecraftonline.griefalert.util.Prism;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.World;

public class ProfileCabinet {

  private static final String GRIEF_PROFILES_FILE_NAME = "grief_profiles.txt";

  private Table<GriefEvent, String, GriefProfile> storage = HashBasedTable.create();

  private final Importer importer = new Importer(GRIEF_PROFILES_FILE_NAME);
  private final Exporter exporter = new Exporter(GRIEF_PROFILES_FILE_NAME);

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
    // ...

    // Get all other profiles from the onsite profile list
    importer.retrieve().forEach(this::add);
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

  /**
   * Add a new Grief Profile to the museum. This will not check if a similar one already exists,
   * so check that it does not before calling this method. An info alert will be sent to the
   * console.
   *
   * @param profile The profile to add
   */
  public boolean add(GriefProfile profile) {
    if (storage.contains(profile.getEventType(), profile.getTarget())) {
      return false;
    } else {
      storage.put(profile.getEventType(), profile.getTarget(), profile);
      return true;
    }
  }

  public void store(GriefProfile profile) throws IOException {
    exporter.store(profile);
  }
}
