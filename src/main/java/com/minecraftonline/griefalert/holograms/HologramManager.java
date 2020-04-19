/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
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

package com.minecraftonline.griefalert.holograms;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.util.Alerts;
import com.minecraftonline.griefalert.util.Format;
import de.randombyte.holograms.api.HologramsService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;

/**
 * A managing class for creating holograms for the purposes of GriefAlert.
 */
public class HologramManager {

  private static final long HOLOGRAM_LIFETIME = 100;
  private static final double DISTANCE_BETWEEN_LINES = 0.3;

  private Logger log = GriefAlert.getInstance().getLogger();
  private HologramsService hologramsService;
  private boolean error;
  private Set<HologramsService.Hologram> alertHolograms = Sets.newHashSet();

  /**
   * General constructor.
   */
  public HologramManager() {
    Optional<HologramsService> optionalHologramsService = Sponge.getServiceManager()
        .provide(HologramsService.class);
    if (optionalHologramsService.isPresent()) {
      hologramsService = optionalHologramsService.get();
      error = false;
    } else {
      error = true;
      log.error("Error finding Hologram Service. "
          + "GriefAlert might not work properly!");
    }
  }

  /**
   * Create a temporary hologram at the given location with the given texts. The
   * distance between the lines is pre-set as is the lifetime of the hologram.
   *
   * @param alert the alert for which to create a temporary alert
   */
  public void createTemporaryHologram(Alert alert) {
    if (error) {
      log.error(String.format(
          "Error creating a hologram at %s",
          alert.getGriefPosition().toString()));
      return;
    }

    List<HologramsService.Hologram> holograms = hologramsService
        .createMultilineHologram(
            new Location<>(
                Alerts.getWorld(alert),
                alert.getGriefPosition().toDouble().add(0.5, 0.2, 0.5)),
            Lists.newArrayList(
                Format.userName(Alerts.getGriefer(alert)),
                Format.bonus(
                    alert.getGriefEvent().toAction(),
                    Format.space(),
                    Format.item(alert.getTarget()))),
            DISTANCE_BETWEEN_LINES)
        .orElseThrow(() -> new RuntimeException("Failed to create MultiLine Hologram"));

    alertHolograms.addAll(holograms);

    Task.builder()
        .execute(() ->
            holograms.forEach(hologram -> {
              hologram.remove();
              alertHolograms.remove(hologram);
            }))
        .delayTicks(HOLOGRAM_LIFETIME)
        .submit(GriefAlert.getInstance());

  }

  public void deleteAllHolograms() {
    alertHolograms.forEach(HologramsService.Hologram::remove);
    alertHolograms.clear();
  }

}
