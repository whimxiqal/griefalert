/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.minecraftonline.griefalert.api.records.GriefProfile;

import javax.annotation.Nonnull;

import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.Details;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class UseAlert extends SpongeAlert {

  private final Transform<World> grieferTransform;

  private UseAlert(GriefProfile griefProfile, InteractItemEvent.Secondary event) {
    super(griefProfile, event);
    this.grieferTransform = getGriefer().getTransform();
    this.addDetail(Details.LOOKING_AT);
  }

  public static UseAlert of(GriefProfile griefProfile, InteractItemEvent.Secondary event) {
    return new UseAlert(griefProfile, event);
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
