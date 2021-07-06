/*
 * MIT License
 *
 * Copyright (c) 2021 Pieter Svenson
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

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.enums.Settings;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

/**
 * A structure to store recent alerts for a single cause (as in a player).
 * This structure is in place to ensure alert silencing behavior.
 */
public class RecentAlertHistory {

  private final Map<GriefProfile, Integer> recents = new ConcurrentHashMap<>();
  private final Map<GriefProfile, UUID> cancelers = new ConcurrentHashMap<>();

  /**
   * put a profile in the recent profile cache and find out
   * if the corresponding alert should be canceled.
   *
   * @param profile the profile to add
   * @return true if the alert should be silenced, false if not
   */
  public boolean put(GriefProfile profile) {
    if (recents.containsKey(profile)) {
      int repeated = recents.get(profile) + 1;
      recents.put(profile, repeated);
      if (repeated >= Settings.MAX_HIDDEN_REPEATED_EVENTS.getValue()) {
        recents.remove(profile);
        if (cancelers.containsKey(profile)) {
          Sponge.getScheduler()
              .getTaskById(cancelers.get(profile))
              .orElseThrow(() ->
                  new RuntimeException("An error occurred with a GriefAlert profile silencing canceler"))
              .cancel();
          cancelers.remove(profile);
        }
      }
      return true;
    } else {
      recents.put(profile, 1);
      if (Settings.MAX_HIDDEN_REPEATED_EVENTS_TIMEOUT.getValue() >= 0) {
        cancelers.put(profile, Sponge.getScheduler().createTaskBuilder()
            .delay(Settings.MAX_HIDDEN_REPEATED_EVENTS_TIMEOUT.getValue(), TimeUnit.SECONDS)
            .execute(() -> {
              recents.remove(profile);
              cancelers.remove(profile);
            })
            .submit(GriefAlert.getInstance())
            .getUniqueId());
      }
      return false;
    }
  }

  /**
   * Clear all values from this history and cancel any pending tasks.
   */
  public void clear() {
    recents.clear();
    for (UUID taskId : cancelers.values()) {
      Sponge.getScheduler().getTaskById(taskId).ifPresent(Task::cancel);
    }
    cancelers.clear();
  }

}
