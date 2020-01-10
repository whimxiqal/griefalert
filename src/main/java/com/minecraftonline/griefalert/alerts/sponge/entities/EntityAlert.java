/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.alerts.sponge.SpongeAlert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

abstract class EntityAlert extends SpongeAlert {

  private final EntitySnapshot entitySnapshot;

  EntityAlert(GriefProfile griefProfile, InteractEntityEvent event) {
    super(griefProfile, event);
    entitySnapshot = event.getTargetEntity().createSnapshot();
  }

  EntitySnapshot getEntitySnapshot() {
    return entitySnapshot;
  }

  @Nonnull
  @Override
  public Location<World> getGriefLocation() {
    return getEntitySnapshot().getLocation().map(location -> location.add(0.5, 0.5, 0.5))
        .orElseThrow(() ->
            new RuntimeException("Couldn't find an entities location for an Entity Alert"));
  }

}
