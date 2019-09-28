package com.minecraftonline.griefalert.griefevents.profiles;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.commands.GriefAlertBuilderCommand;
import org.spongepowered.api.entity.living.player.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GriefProfileMuseum {

  private static final String GRIEF_PROFILES_FILE_NAME = "grief_profiles.txt";

  private final GriefAlert plugin;
  private HashMap<GriefAlert.GriefType, HashMap<String, GriefProfile>> warehouse = new HashMap<>();
  private final HashMap<UUID, GriefAlertBuilderCommand.GriefProfileBuilder> profileBuilderMap = new HashMap<>();

  private final GriefProfileImporter importer;
  private final GriefProfileExporter exporter;

  public GriefProfileMuseum(GriefAlert plugin) {
    this.plugin = plugin;
    this.importer = new GriefProfileImporter(plugin, GRIEF_PROFILES_FILE_NAME);
    this.exporter = new GriefProfileExporter(plugin, GRIEF_PROFILES_FILE_NAME);
    this.warehouse.put(GriefAlert.GriefType.DESTROY, new HashMap<>());
    this.warehouse.put(GriefAlert.GriefType.INTERACT, new HashMap<>());
    this.warehouse.put(GriefAlert.GriefType.USE, new HashMap<>());
    this.retrieve();
  }

  public void reload() {
    plugin.getLogger().warn("Grief Profile Museum was reloaded, but data was only retrieved. No data was saved from cache!");
    retrieve();
  }

  private void retrieve() {
    List<GriefProfile> events = (new GriefProfileImporter(plugin, GRIEF_PROFILES_FILE_NAME)).retrieve();
    for (GriefProfile event : events) {
      warehouse.get(event.getGriefType()).put(event.getGriefedId(), event);
    }
  }

  public Optional<GriefProfile> getMatchingProfile(EventWrapper event) {
    return Optional.ofNullable(warehouse.get(event.getType()).get(event.getGriefedId()));
  }

  public boolean setBuildingState(Player player, boolean state) {
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

  public void add(GriefProfile candidate) {
    warehouse.get(candidate.getGriefType()).put(candidate.getGriefedId(), candidate);
  }

  public void store(GriefProfile profile) throws IOException {
    exporter.store(profile);
  }
}
