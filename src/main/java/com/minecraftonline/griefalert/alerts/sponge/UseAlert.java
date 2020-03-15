/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import javax.annotation.Nonnull;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class UseAlert extends SpongeAlert {

  private final Transform<World> grieferTransform;

  public UseAlert(GriefProfile griefProfile, InteractItemEvent.Secondary event) {
    super(griefProfile, event);
    this.grieferTransform = getGriefer().getTransform();
  }

  @Nonnull
  @Override
  public Transform<World> getGrieferTransform() {
    return grieferTransform;
  }

  @Nonnull
  @Override
  public Location<World> getGriefLocation() {
    return getGrieferTransform().getLocation();
  }
}
