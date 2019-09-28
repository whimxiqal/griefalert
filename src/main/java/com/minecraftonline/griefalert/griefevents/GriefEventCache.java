package com.minecraftonline.griefalert.griefevents;

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;

import java.util.*;

public class GriefEventCache {

  private LinkedList<GriefEvent> cache;
  private final HashMap<UUID, EntitySnapshot> checkerMap = new HashMap<>();
  private int cursor = 0;
  private int maxCode;

  public GriefEventCache(GriefAlert plugin) {
    cache = new LinkedList<>();
    maxCode = 5; // plugin.getRootNode().getNode("alertsCodeLimit").getInt();
  }

  public int offer(GriefEvent griefEvent) {
    cache.add(cursor % maxCode, griefEvent);
    return cursor++;
  }

  public Optional<GriefEvent> get(int alert_code) {
    if (alert_code >= 0 && alert_code< maxCode) {
      return Optional.of(cache.get(alert_code));
    }
    return Optional.empty();
  }

  public Optional<EntitySnapshot> putSnapshot(Player staff) {
    return Optional.ofNullable(checkerMap.put(staff.getUniqueId(), staff.createSnapshot()));
  }

  public Optional<EntitySnapshot> getSnapshot(Player staff) {
    return Optional.ofNullable(checkerMap.get(staff.getUniqueId()));
  }

  public List<GriefEvent> getListByNumber() {
    return cache;
  }

  public List<GriefEvent> getListByTime() {
    LinkedList<GriefEvent> toSort = new LinkedList<>(cache);
    toSort.sort((first, second) -> {
      int firstPosition = ((first.getCacheCode() - cursor) + toSort.size()) % toSort.size();
      int secondPosition = ((second.getCacheCode() - cursor) + toSort.size()) % toSort.size();
      return -Integer.compare(firstPosition, secondPosition); // Negated because we want the most recent first
    });
    return toSort;
  }
}
