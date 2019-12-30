package com.minecraftonline.griefalert.listeners;

import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.api.records.PrismRecordPreSaveEvent;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.*;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.Comms;
import com.minecraftonline.griefalert.util.GriefEvents;
import com.minecraftonline.griefalert.util.Prism;
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

    Optional<String> targetOptional = Prism.getTarget(record);
    if (!targetOptional.isPresent()) {
      GriefAlert.getInstance().getLogger().debug("PrismEvent passed: no target found.");
      return;
    }

    Optional<DimensionType> dimensionTypeOptional = Prism.getLocation(record).map((location) -> location.getExtent().getDimension().getType());
    if (!dimensionTypeOptional.isPresent()) {
      GriefAlert.getInstance().getLogger().debug("PrismEvent passed: no dimension type found.");
      return;
    }

    if (!GriefEvents.Registry.of(record.getEvent()).isPresent()) {
      GriefAlert.getInstance().getLogger().debug(String.format("PrismEvent passed: Prism Event '%s' is not being checked.", record.getEvent()));
      return;
    }

    Optional<GriefProfile> profileOptional = GriefAlert.getInstance().getProfileCabinet().getProfileOf(
        GriefEvents.Registry.of(record.getEvent()).get(),
        targetOptional.get(),
        dimensionTypeOptional.get()
    );

    // If yes, create an Alert of the appropriate type
    profileOptional.ifPresent((profile) -> {
      int nextCacheNum = GriefAlert.getInstance().getAlertQueue().cursor();

      Optional<PrismAlert> alert = PrismAlert.of(nextCacheNum, profile, record);

      if (!alert.isPresent()) {
        GriefAlert.getInstance().getLogger().error("A PrismRecord matched a Grief Profile but "
            + "an Alert could not be made.");
        GriefAlert.getInstance().getLogger().error(Prism.printRecord(record));
        GriefAlert.getInstance().getLogger().error(profileOptional.get().printData());
        return;
      }

      // Add the alert to the AlertQueue
      GriefAlert.getInstance().getAlertQueue().push(alert.get());

      // Broadcast the Alert's message
      if (!alert.get().isSilent()) {
        Comms.getStaffBroadcastChannel().send(alert.get().getFullText());
      }
    });
  }

}
