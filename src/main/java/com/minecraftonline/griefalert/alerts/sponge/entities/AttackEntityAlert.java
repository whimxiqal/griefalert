/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.event.entity.InteractEntityEvent;

/**
 * An <code>Alert</code> for the Attack <code>GriefEvent</code>.
 *
 * @see GriefEvents
 */
public class AttackEntityAlert extends EntityAlert {

  AttackEntityAlert(final GriefProfile griefProfile, final InteractEntityEvent.Primary event) {
    super(griefProfile, event);
  }

  /**
   * Generates an appropriate <code>InteractEntityPrimaryAlert</code>. If the target is any of
   * these, then it sets a unique text getter method.
   * <li>minecraft:item_frame</li>
   * <li>minecraft:armor_stand</li>
   *
   * @param griefProfile The profile flagging this Alert
   * @param event        The Sponge event causing this alert
   * @return The correctly modified AttackAlert
   */
  public static AttackEntityAlert of(
      final GriefProfile griefProfile,
      final InteractEntityEvent.Primary event) {

    // Look for special target ids
    switch (griefProfile.getTarget()) {

      case "minecraft:item_frame":
        return new AttackItemFrameAlert(griefProfile, event);
      case "minecraft:armor_stand":
        return new AttackArmorStandAlert(griefProfile, event);
      default:
        return new AttackEntityAlert(griefProfile, event);
    }
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.ATTACK;
  }

}
