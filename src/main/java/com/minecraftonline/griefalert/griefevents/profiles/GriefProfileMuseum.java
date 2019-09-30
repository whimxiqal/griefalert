package com.minecraftonline.griefalert.griefevents.profiles;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.commands.GriefAlertBuilderCommand;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;

public class GriefProfileMuseum {

  private static final String GRIEF_PROFILES_FILE_NAME = "grief_profiles.txt";

  private final GriefAlert plugin;
  private HashMap<GriefAlert.GriefType, HashMap<String, GriefProfile>> warehouse
      = new HashMap<>();
  private final HashMap<UUID, GriefAlertBuilderCommand.GriefProfileBuilder> profileBuilderMap
      = new HashMap<>();

  private final GriefProfileImporter importer;
  private final GriefProfileExporter exporter;

  /**
   * Constructor for a new museum to hold all Grief Profiles. This museum should be considered
   * immutable, and should only be used as a tool to check possible Grief Events against.
   *
   * @param plugin The main Grief Alert plugin
   */
  public GriefProfileMuseum(final GriefAlert plugin) {
    this.plugin = plugin;
    this.importer = new GriefProfileImporter(plugin, GRIEF_PROFILES_FILE_NAME);
    this.exporter = new GriefProfileExporter(plugin, GRIEF_PROFILES_FILE_NAME);
    this.warehouse.put(GriefAlert.GriefType.DESTROY, new HashMap<>());
    this.warehouse.put(GriefAlert.GriefType.INTERACT, new HashMap<>());
    this.warehouse.put(GriefAlert.GriefType.USE, new HashMap<>());
    this.retrieve();
  }

  /**
   * Load in all data from the Grief Profiles file on the local machine.
   */
  public void reload() {
    for (GriefAlert.GriefType type : warehouse.keySet()) {
      warehouse.get(type).clear();
    }
    retrieve();
    plugin.getLogger().warn("Grief Profiles were imported to profile museum cache.");
  }

  private void retrieve() {
    List<GriefProfile> events = importer.retrieve();
    for (GriefProfile event : events) {
      add(event);
    }
  }

  /**
   * Get the profile which matches the data within the given event.
   *
   * @param event The wrapper around the event, containing all relevant information
   *              about the actual Sponge event
   * @return An optional GriefProfile corresponding to the given event
   */
  public Optional<GriefProfile> getMatchingProfile(final EventWrapper event) {
    GriefProfile profile = warehouse.get(event.getType()).get(event.getGriefedId());
    if (profile != null) {
      if (event.getGriefedLocation().isPresent()) {
        if (!profile.getDimensionStructure().isIgnored(
            event.getGriefedLocation().get().getExtent().getDimension().getType())
        ) {
          return Optional.of(profile);
        }
      }
    }
    return Optional.empty();
  }

  /**
   * Dictate whether a player is in Profile Build mode. This is used only with the
   * build command.
   *
   * @param player The player
   * @param state  True if the player will be put into build mode, false if the player
   *               will not be in build mode.
   * @return True if the player changed states. False if the player did not change
   * states after applying the given input state.
   */
  public boolean setBuildingState(final Player player, final boolean state) {
    if (getProfileBuilder(player).isPresent()) {
      if (!state) {
        removeProfileBuilder(player);
        return true;
      }
    } else {
      if (state) {
        profileBuilderMap.put(player.getUniqueId(), new GriefAlertBuilderCommand.GriefProfileBuilder());
        return true;
      }
    }
    return false;
  }

  public Optional<GriefAlertBuilderCommand.GriefProfileBuilder> getProfileBuilder(Player player) {
    return Optional.ofNullable(profileBuilderMap.get(player.getUniqueId()));
  }

  private void removeProfileBuilder(Player player) {
    profileBuilderMap.remove(player.getUniqueId());
  }

  public boolean contains(GriefProfile candidate) {
    for (GriefAlert.GriefType type : warehouse.keySet()) {
      for (String griefedId : warehouse.get(type).keySet()) {
        if (type.equals(candidate.getGriefType()) && griefedId.equalsIgnoreCase(candidate.getGriefedId())) {
          return true;
        }
      }
    }
    return false;
  }

  public void add(GriefProfile profile) {
    warehouse.get(profile.getGriefType()).put(profile.getGriefedId(), profile);
    plugin.getLogger().info("A grief profile has been added to the museum: " + profile.getGriefType().getName() + ", " + profile.getGriefedId());
  }

  public void store(GriefProfile profile) throws IOException {
    exporter.store(profile);
  }
}
