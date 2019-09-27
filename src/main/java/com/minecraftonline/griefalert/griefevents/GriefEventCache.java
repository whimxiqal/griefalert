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
    maxCode = plugin.getRootNode().getNode("alertsCodeLimit").getInt();
  }

  public int offer(GriefEvent griefEvent) {
    cache.add(cursor, griefEvent);
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
}
