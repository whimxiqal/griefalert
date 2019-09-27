package com.minecraftonline.griefalert.griefevents.profiles;

import com.minecraftonline.griefalert.GriefAlert;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class GriefProfileMuseum {

  private static final String GRIEF_PROFILES_FILE_NAME = "grief_profiles.txt";

  private final GriefAlert plugin;
  private HashMap<GriefAlert.GriefType, HashMap<String, GriefProfile>> warehouse = new HashMap<>();

  private final GriefProfileImporter importer;
  private final GriefProfileExporter exporter;

  public GriefProfileMuseum(GriefAlert plugin) {
    this.plugin = plugin;
    this.importer = new GriefProfileImporter(plugin, GRIEF_PROFILES_FILE_NAME);
    this.exporter = new GriefProfileExporter();
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
}
