/* Created by PietElite */

package com.minecraftonline.griefalert.listeners;

import com.helion3.prism.api.records.PrismRecordPreSaveEvent;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.prism.BreakAlert;
import com.minecraftonline.griefalert.alerts.prism.DeathAlert;
import com.minecraftonline.griefalert.alerts.prism.PlaceAlert;
import com.minecraftonline.griefalert.alerts.prism.PrismAlert;
import com.minecraftonline.griefalert.alerts.prism.ReplaceAlert;
import com.minecraftonline.griefalert.alerts.prism.SignBreakAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.PrismUtil;
import com.minecraftonline.griefalert.util.enums.GriefEvents;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.world.DimensionType;

public class PrismRecordListener implements EventListener<PrismRecordPreSaveEvent> {


  @Override
  public void handle(PrismRecordPreSaveEvent event) {
    PrismRecordArchived record = PrismRecordArchived.of(event.getPrismRecord());

    // Temporary print statement to see all information within PrismRecord
    GriefAlert.getInstance().getLogger().debug(PrismUtil.printRecord(record));

    // See if this record matches any GriefProfiles
    if (!Sponge.getRegistry().getType(GriefEvent.class, record.getEvent()).isPresent()) {
      return;
    }

    // Replace spaces with underscores because for some reason PrismUtil removes them...
    Optional<String> targetOptional = PrismUtil.getTarget(record).map(General::ensureIdFormat);
    if (!targetOptional.isPresent()) {
      return;
    }

    Optional<DimensionType> dimensionTypeOptional = PrismUtil.getLocation(record)
        .map((location) -> location.getExtent().getDimension().getType());
    if (!dimensionTypeOptional.isPresent()) {
      return;
    }

    Optional<String> playerUuidOptional = PrismUtil.getPlayerUuid(record);
    if (!playerUuidOptional.isPresent()) {
      return;
    }

    Optional<GriefProfile> profileOptional = GriefAlert.getInstance()
        .getProfileCache()
        .getProfileOf(
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
        GriefAlert.getInstance().getLogger().error(PrismUtil.printRecord(record));
        GriefAlert.getInstance().getLogger().error(profile.print().toPlain());
        return;
      }

      // Cache the Alert and broadcast its message
      alert.run();
    });

    // Check for replacement alerts
    if (Sponge.getRegistry().getType(GriefEvent.class,
        record.getEvent()).get().equals(GriefEvents.PLACE)) {

      Optional<String> originalBlockId = PrismUtil.getOriginalBlockState(record).map(
          (state) -> state.getType().getId());

      if (!originalBlockId.isPresent()) {
        GriefAlert.getInstance().getLogger().info("No original block present");
        return;
      }

      Optional<GriefProfile> replaceOptional = GriefAlert.getInstance().getProfileCache()
          .getProfileOf(
              GriefEvents.REPLACE,
              originalBlockId.get(),
              dimensionTypeOptional.get());

      Optional<String> replacementBlockId = PrismUtil.getReplacementBlock(record)
          .map((state) -> state.getType().getId());

      replaceOptional.ifPresent((profile) -> {
        GriefAlert.getInstance().getLogger().debug("PrismEvent matched a GriefProfile");

        PrismAlert alert = new ReplaceAlert(
            profile,
            record,
            replacementBlockId.orElse("unknown block"));

        alert.run();
      });
    }
  }

}
