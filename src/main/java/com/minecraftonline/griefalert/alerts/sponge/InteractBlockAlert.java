/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import javax.annotation.Nonnull;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.world.Location;

/**
 * An alert caused by block interaction.
 */
public class InteractBlockAlert extends SpongeAlert {

  private final Vector3i griefLocation;

  /**
   * Default constructor.
   *
   * @param griefProfile the grief profile
   * @param event        the event
   */
  public InteractBlockAlert(GriefProfile griefProfile, InteractBlockEvent event) {
    super(griefProfile, event);
    griefLocation = event.getTargetBlock().getLocation().map(Location::getBlockPosition).orElseThrow(() ->
        new RuntimeException("Couldn't find the location of a block in an InteractBlockAlert"));
  }

  @Nonnull
  @Override
  public Vector3i getGriefPosition() {
    return griefLocation;
  }
}
