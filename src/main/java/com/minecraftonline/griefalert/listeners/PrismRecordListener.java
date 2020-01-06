/* Created by PietElite */

package com.minecraftonline.griefalert.listeners;

import com.helion3.prism.api.records.PrismRecordPreSaveEvent;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.prism.*;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.GriefEvents;
import com.minecraftonline.griefalert.util.Prism;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.world.DimensionType;

import java.util.Optional;

public class PrismRecordListener implements EventListener<PrismRecordPreSaveEvent> {


  @Override
  public void handle(PrismRecordPreSaveEvent event) throws Exception {
    PrismRecordArchived record = PrismRecordArchived.of(event.getPrismRecord());

    // Temporary print statement to see all information within PrismRecord
    GriefAlert.getInstance().getLogger().debug(Prism.printRecord(record));

    // See if this record matches any GriefProfiles

    if (!Sponge.getRegistry().getType(GriefEvent.class, record.getEvent()).isPresent()) {
      GriefAlert.getInstance().getLogger().debug(String.format("PrismEvent passed: Prism Event '%s' is not being checked.", record.getEvent()));
      return;
    }

    // Replace spaces with underscores because for some reason Prism removes them...
    Optional<String> targetOptional = Prism.getTarget(record).map(General::ensureIdFormat);
    if (!targetOptional.isPresent()) {
      GriefAlert.getInstance().getLogger().debug("PrismEvent passed: no target found.");
      return;
    }

    Optional<DimensionType> dimensionTypeOptional = Prism.getLocation(record).map((location) -> location.getExtent().getDimension().getType());
    if (!dimensionTypeOptional.isPresent()) {
      GriefAlert.getInstance().getLogger().debug("PrismEvent passed: no dimension type found.");
      return;
    }

    Optional<String> playerUuidOptional = Prism.getPlayerUuid(record);
    if (!playerUuidOptional.isPresent()) {
      GriefAlert.getInstance().getLogger().debug("PrismEvent passed: no player found.");
      return;
    }

    Optional<GriefProfile> profileOptional = GriefAlert.getInstance().getProfileCabinet().getProfileOf(
        Sponge.getRegistry().getType(GriefEvent.class, record.getEvent()).get(),
        targetOptional.get(),
        dimensionTypeOptional.get()
    );

    // If yes, create an Alert of the appropriate type
    profileOptional.ifPresent((profile) -> {
      GriefAlert.getInstance().getLogger().debug("PrismEvent matched a GriefProfile");

      Alert alert;
      if (profile.getGriefEvent().equals(GriefEvents.BREAK)) {
        if (targetOptional.get().contains("sign")) {
          alert = new SignBreakAlert(profile, record);
        } else {
          alert = new BreakAlert(profile, record);
        }
      } else if (profile.getGriefEvent().equals(GriefEvents.PLACE)) {
        alert = new PlaceAlert(profile, record);
      } else if (profile.getGriefEvent().equals(GriefEvents.DEATH)) {
        alert = new DeathAlert(profile, record);
      } else {
        GriefAlert.getInstance().getLogger().error("A PrismRecord matched a Grief Profile but "
            + "an Alert could not be made.");
        GriefAlert.getInstance().getLogger().error(Prism.printRecord(record));
        GriefAlert.getInstance().getLogger().error(profileOptional.get().printData());
        return;
      }

      // Cache the Alert and broadcast its message
      alert.run();
    });

    // Check for replacement alerts
    if (Sponge.getRegistry().getType(GriefEvent.class, record.getEvent()).get().equals(GriefEvents.PLACE)) {
      Optional<String> originalBlockId = Prism.getOriginalBlock(record).map((state) -> state.getType().getId());
      if (!originalBlockId.isPresent()) {
        GriefAlert.getInstance().getLogger().info("No original block present");
        return;
      }
      GriefAlert.getInstance().getLogger().info("Found: " + originalBlockId.get());

      Optional<GriefProfile> replaceOptional = GriefAlert.getInstance().getProfileCabinet()
          .getProfileOf(
              GriefEvents.REPLACE,
              originalBlockId.get(),
              dimensionTypeOptional.get());

      Optional<String> replacementBlockId = Prism.getReplacementBlock(record)
          .map((state) -> state.getType().getId());

      // If yes, create an Alert of the appropriate type
      replaceOptional.ifPresent((profile) -> {
        GriefAlert.getInstance().getLogger().debug("PrismEvent matched a GriefProfile");

        PrismAlert alert = new ReplaceAlert(profile, record, replacementBlockId.orElse("unknown block"));

        // Cache the Alert and broadcast its message
        alert.run();
      });
    }
  }

}
