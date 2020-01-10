package com.minecraftonline.griefalert.holograms;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.util.Format;
import de.randombyte.holograms.api.HologramsService;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

/**
 * A managing class for creating holograms for the purposes of GriefAlert.
 */
public class HologramManager {

  private static final long HOLOGRAM_LIFETIME = 100;
  private static final double DISTANCE_BETWEEN_LINES = 0.3;

  private Logger log = GriefAlert.getInstance().getLogger();
  private HologramsService hologramsService;
  private boolean error;

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
          alert.getGriefLocation().toString()));
      return;
    }

    List<HologramsService.Hologram> holograms = hologramsService
        .createMultilineHologram(
            alert.getGriefLocation(),
            Lists.newArrayList(
                Format.playerName(alert.getGriefer()),
                Format.bonus(
                    alert.getGriefEvent().getPreterite(),
                    Format.space(),
                    Format.removeMinecraftPrefix(alert.getTarget()))),
            DISTANCE_BETWEEN_LINES)
        .orElseThrow(() -> new RuntimeException("Failed to create MultiLine Hologram"));

    Task.builder()
        .execute(() ->
            holograms.forEach(HologramsService.Hologram::remove))
        .delayTicks(HOLOGRAM_LIFETIME)
        .submit(GriefAlert.getInstance());

  }

}
