package com.minecraftonline.griefalert.listeners;

import com.helion3.prism.api.records.PrismRecordPreSaveEvent;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.prism.PrismAlert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.GriefEvents;
import com.minecraftonline.griefalert.util.Prism;
import com.minecraftonline.griefalert.util.Registry;
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

    if (!Registry.lookupGriefEvent(record.getEvent()).isPresent()) {
      GriefAlert.getInstance().getLogger().debug(String.format("PrismEvent passed: Prism Event '%s' is not being checked.", record.getEvent()));
      return;
    }

    // Replace spaces with underscores because for some reason Prism removes them...
    Optional<String> targetOptional = Prism.getTarget(record).map((s) -> s.replace(" ", "_"));
    if (!targetOptional.isPresent()) {
      GriefAlert.getInstance().getLogger().debug("PrismEvent passed: no target found.");
      return;
    }

    Optional<DimensionType> dimensionTypeOptional = Prism.getLocation(record).map((location) -> location.getExtent().getDimension().getType());
    if (!dimensionTypeOptional.isPresent()) {
      GriefAlert.getInstance().getLogger().debug("PrismEvent passed: no dimension type found.");
      return;
    }

    Optional<GriefProfile> profileOptional = GriefAlert.getInstance().getProfileCabinet().getProfileOf(
        Registry.lookupGriefEvent(record.getEvent()).get(),
        targetOptional.get(),
        dimensionTypeOptional.get()
    );

    // If yes, create an Alert of the appropriate type
    profileOptional.ifPresent((profile) -> {
      GriefAlert.getInstance().getLogger().debug("PrismEvent matched a GriefProfile");

      Optional<PrismAlert> alert = PrismAlert.of(profile, record);

      if (!alert.isPresent()) {
        GriefAlert.getInstance().getLogger().error("A PrismRecord matched a Grief Profile but "
            + "an Alert could not be made.");
        GriefAlert.getInstance().getLogger().error(Prism.printRecord(record));
        GriefAlert.getInstance().getLogger().error(profileOptional.get().printData());
        return;
      }

      // Cache the Alert and broadcast its message
      alert.get().pushAndRun();
    });
  }

}
