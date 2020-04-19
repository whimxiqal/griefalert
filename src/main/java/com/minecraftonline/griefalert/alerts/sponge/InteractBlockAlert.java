/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.griefalert.api.records.GriefProfile;

import javax.annotation.Nonnull;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class InteractBlockAlert extends SpongeAlert {

  private final Vector3i griefLocation;

  public InteractBlockAlert(GriefProfile griefProfile, InteractBlockEvent event) {
    super(griefProfile, event);
    griefLocation = event.getTargetBlock().getLocation().map(Location::getBlockPosition).orElseThrow(() ->
        new RuntimeException("Couldn't find the location of a block in an IneteractBlockAlert"));
  }

  @Nonnull
  @Override
  public Vector3i getGriefPosition() {
    return griefLocation;
  }
}
