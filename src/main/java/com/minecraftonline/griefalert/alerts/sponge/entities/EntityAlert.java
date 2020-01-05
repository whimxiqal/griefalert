/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.alerts.sponge.SpongeAlert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.event.entity.InteractEntityEvent;

abstract class EntityAlert extends SpongeAlert {

  private final EntitySnapshot entitySnapshot;

  EntityAlert(GriefProfile griefProfile, InteractEntityEvent event) {
    super(griefProfile, event);
    entitySnapshot = event.getTargetEntity().createSnapshot();
  }

  EntitySnapshot getEntitySnapshot() {
    return entitySnapshot;
  }

}
