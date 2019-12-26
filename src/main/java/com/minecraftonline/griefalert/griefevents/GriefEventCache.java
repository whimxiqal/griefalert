//package com.minecraftonline.griefalert.griefevents;
//
//import com.minecraftonline.griefalert.GriefAlert;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import org.spongepowered.api.entity.EntitySnapshot;
//import org.spongepowered.api.entity.living.player.Player;
//
//public class GriefEventCache {
//
//  // -------------
//  // TO BE REMOVED
//  // -------------
//
//  private ArrayList<GriefEvent> cache = new ArrayList<>();
//  private final HashMap<UUID, EntitySnapshot> checkerMap = new HashMap<>();
//  private int cursor = 0;
//  private final int maxCacheSize;
//  private int maxRepeatedProfiles;
//
//  public GriefEventCache(GriefAlert plugin) {
//    maxCacheSize = plugin.getConfigHelper().getCachedEventLimit();
//    maxRepeatedProfiles = plugin.getConfigHelper().getHiddenRepeatedEventLimit();
//  }
//
//  int offer(GriefEvent griefEvent) {
//    if (cache.size() >= maxCacheSize) {
//      cache.set(cursor, griefEvent);
//    } else {
//      cache.add(griefEvent);
//    }
//    int prevCursor = cursor;
//    cursor = (cursor + 1) % maxCacheSize;
//    return prevCursor;
//  }
//
//  /**
//   * Given the index of an alert, find the corresponding GriefEvent in the cached
//   * list.
//   *
//   * @param alertCode The index of the GriefEvent in the list
//   * @return The corresponding GriefEvent
//   */
//  public Optional<GriefEvent> get(int alertCode) {
//    if (alertCode >= 0 && alertCode < maxCacheSize) {
//      return Optional.of(cache.get(alertCode));
//    }
//    return Optional.empty();
//  }
//
//  public void putSnapshot(Player staff) {
//    checkerMap.put(staff.getUniqueId(), staff.createSnapshot());
//  }
//
//  public Optional<EntitySnapshot> getSnapshot(Player staff) {
//    return Optional.ofNullable(checkerMap.get(staff.getUniqueId()));
//  }
//
//  /**
//   * Gets all cached GriefEvents sorted with respect to time.
//   *
//   * @param mostRecentLast Determines whether this list will be sorted in chronological or
//   *                       backwards chronological order. If true, the most recent event will
//   *                       be last in the list. If false, the most recent event will be first
//   *                       in the list.
//   * @return A list of cached Grief Events in backwards chronological order
//   */
//  public List<GriefEvent> getListChronologically(boolean mostRecentLast) {
//    LinkedList<GriefEvent> toSort = new LinkedList<>(cache);
//    toSort.sort((first, second) -> {
//      int firstPosition = ((first.getCacheCode() - cursor) + toSort.size()) % toSort.size();
//      int secondPosition = ((second.getCacheCode() - cursor) + toSort.size()) % toSort.size();
//      return Integer.compare(firstPosition, secondPosition) * (mostRecentLast ? 1 : -1);
//    });
//    return toSort;
//  }
//
//  /**
//   * Determines whether an event has been repeated by a player. If this is the case,
//   * the event must be made stealthy so staff members do not get overloaded with
//   * messages if a player starts doing one task consecutive times. There is a max
//   * number of repeated events which are allowed to be stealthy before the alert
//   * is thrown again so staff are aware of the excess alerts but are not bothered
//   * by them.
//   *
//   * @param event The specific event to check for repetition
//   * @return Whether the GriefEvent should be stealthy
//   */
//  boolean isStale(GriefEvent event) {
//    int repeatedProfiles = 0;
//    for (GriefEvent tmp : getListChronologically(false)) {
//      if (tmp.isSimilar(event)) {
//        repeatedProfiles++;
//      } else {
//        break;
//      }
//    }
//    return repeatedProfiles % maxRepeatedProfiles != 0;
//  }
//
//}
