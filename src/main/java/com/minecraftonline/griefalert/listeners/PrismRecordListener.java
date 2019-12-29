package com.minecraftonline.griefalert.listeners;

import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.api.records.PrismRecordPreSaveEvent;
import com.helion3.prism.util.DataQueries;
import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.*;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.profiles.GriefProfile;
import com.minecraftonline.griefalert.util.Comms;
import com.minecraftonline.griefalert.util.GriefEvents;
import com.minecraftonline.griefalert.util.Prism;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.world.DimensionType;

import java.util.Optional;

public class PrismRecordListener implements EventListener<PrismRecordPreSaveEvent> {


  @Override
  public void handle(PrismRecordPreSaveEvent event) throws Exception {
    PrismRecord record = event.getPrismRecord();

    // Temporary print statement to see all information within PrismRecord
    Sponge.getServer().getBroadcastChannel().send(Prism.printRecord(record));

    // See if this record matches any GriefProfiles

    Optional<String> targetOptional = Prism.getTarget(record);
    if (!targetOptional.isPresent()) {
      return;
    }

    Optional<DimensionType> dimensionTypeOptional = Prism.getDimensionType(record);
    if (!dimensionTypeOptional.isPresent()) {
      return;
    }

    Optional<GriefProfile> profileOptional = GriefAlert.getInstance().getProfileCabinet().getProfileOf(
        GriefEvents.Registry.of(record.getEvent()),
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
        GriefAlert.getInstance().getLogger().error(Prism.printRecord(record).toPlain());
        GriefAlert.getInstance().getLogger().error(profileOptional.get().printData());
        return;
      }

      // Add the alert to the AlertQueue
      GriefAlert.getInstance().getAlertQueue().push(alert.get());

      // Broadcast the Alert's message
      Comms.getStaffBroadcastChannel().send(alert.get().getFullText());
    });
  }

}
