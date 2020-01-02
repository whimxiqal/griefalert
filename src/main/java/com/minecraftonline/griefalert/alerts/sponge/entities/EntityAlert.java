package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.alerts.sponge.SpongeAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.TargetEntityEvent;

public abstract class EntityAlert extends SpongeAlert {

  final EntitySnapshot entitySnapshot;

  protected EntityAlert(GriefProfile griefProfile, InteractEntityEvent event) {
    super(griefProfile, event);
    entitySnapshot = event.getTargetEntity().createSnapshot();
  }

  protected EntitySnapshot getEntitySnapshot() {
    return entitySnapshot;
  }

}
