package com.minecraftonline.griefalert.listeners;

import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.api.records.PrismRecordPreSaveEvent;
import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.BreakAlert;
import com.minecraftonline.griefalert.alerts.DeathAlert;
import com.minecraftonline.griefalert.alerts.PlaceAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.profiles.GriefProfileOld;
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

    // PSEUDO-CODE

    // TODO: Finish handler in PrismRecordListener

    // See if this record matches any GriefProfiles

    Optional<String> targetOptional = Prism.getTarget(record);
    if (!targetOptional.isPresent()) {
      return;
    }

    Optional<DimensionType> dimensionTypeOptional = Prism.getDimensionType(record);
    if (!dimensionTypeOptional.isPresent()) {
      return;
    }

    Optional<GriefProfileOld> profileOptional = GriefAlert.getInstance().getProfileCabinet().getProfileOf(
        GriefEvents.Registry.of(record.getEvent()),
        targetOptional.get(),
        dimensionTypeOptional.get()
    );

    // If yes, create an Alert of the appropriate type
    if (profileOptional.isPresent()) {
      // Add the alert to the AlertQueue
      Alert alert = null;

      if (record.getEvent().equals(PrismEvents.BLOCK_BREAK.getName())) {
        alert = new BreakAlert(GriefAlert.getInstance().getAlertQueue().cursor());
        GriefAlert.getInstance().getAlertQueue().push(alert);
      } else if (record.getEvent().equals(PrismEvents.BLOCK_PLACE.getName())) {
        alert = new PlaceAlert(GriefAlert.getInstance().getAlertQueue().cursor());
        GriefAlert.getInstance().getAlertQueue().push(alert);
      } else if (record.getEvent().equals(PrismEvents.ENTITY_DEATH.getName())) {
        alert = new DeathAlert(GriefAlert.getInstance().getAlertQueue().cursor());
        GriefAlert.getInstance().getAlertQueue().push(alert);
      }
      // Broadcast the Alert's message
      if (alert == null) {
        GriefAlert.getInstance().getLogger().error("A PrismRecord matched a Grief Profile but "
            + "an Alert could not be made.");
        GriefAlert.getInstance().getLogger().error(Prism.printRecord(record).toPlain());
        GriefAlert.getInstance().getLogger().error(profileOptional.get().print());
      }
      Comms.getStaffBroadcastChannel().send(alert.getFullText());
    }
  }

}
