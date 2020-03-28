/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.alerts.sponge.SpongeAlert;
import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import javax.annotation.Nonnull;

import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.SpongeEvents;
import com.minecraftonline.griefalert.util.enums.Details;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.event.entity.TargetEntityEvent;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

abstract class EntityAlert extends SpongeAlert {

  private final EntitySnapshot entitySnapshot;

  EntityAlert(GriefProfile griefProfile, TargetEntityEvent event) {
    super(griefProfile, event);
    entitySnapshot = event.getTargetEntity().createSnapshot();
    addDetail(Details.LOOKING_AT);
    addDetail(Details.IN_HAND);
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
