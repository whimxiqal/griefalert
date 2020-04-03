/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.minecraftonline.griefalert.api.records.GriefProfile;

import javax.annotation.Nonnull;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class InteractBlockAlert extends SpongeAlert {

  private final Location<World> griefLocation;
  private final Transform<World> grieferTransform;

  private InteractBlockAlert(GriefProfile griefProfile, InteractBlockEvent event) {
    super(griefProfile, event);
    griefLocation = event.getTargetBlock().getLocation().orElseThrow(() ->
        new RuntimeException("Couldn't find the location of a block in an IneteractBlockAlert"));
    this.grieferTransform = getGriefer().getTransform();
  }

  public static InteractBlockAlert of(GriefProfile griefProfile, InteractBlockEvent event) {
    return new InteractBlockAlert(griefProfile, event);
  }

  @Nonnull
  @Override
  public Transform<World> getGrieferTransform() {
    return grieferTransform;
  }

  @Nonnull
  @Override
  public Location<World> getGriefLocation() {
    return griefLocation.add(0.5, 0.5, 0.5);
  }

}
