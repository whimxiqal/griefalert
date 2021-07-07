/* Created by PietElite */

package com.minecraftonline.griefalert.sponge.alert.alerts.sponge;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.griefalert.common.alert.records.GriefProfile;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Details;
import javax.annotation.Nonnull;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;

/**
 * An alert caused by "using" an item, which is really just interacting
 * with an item secondarily (right clicking).
 *
 * @author PietElite
 */
public class UseAlert extends SpongeAlert {

  public UseAlert(GriefProfile griefProfile, InteractItemEvent.Secondary event) {
    super(griefProfile, event);
    this.addDetail(Details.lookingAt());
  }

  @Nonnull
  @Override
  public Vector3i getGriefPosition() {
    return getGrieferPosition().toInt();
  }
}
