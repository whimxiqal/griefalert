/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import javax.annotation.Nonnull;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class UseAlert extends SpongeAlert {

  private UseAlert(GriefProfile griefProfile, InteractItemEvent.Secondary event) {
    super(griefProfile, event);
  }

  public static UseAlert of(GriefProfile griefProfile, InteractItemEvent.Secondary event) {
    return new UseAlert(griefProfile, event);
  }

  @Nonnull
  @Override
  public Location<World> getGriefLocation() {
    return getGrieferTransform().getLocation();
  }
}
