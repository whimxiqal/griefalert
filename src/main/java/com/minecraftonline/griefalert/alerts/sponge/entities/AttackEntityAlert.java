/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.GriefEvents;

import java.util.UUID;
import javax.annotation.Nonnull;

import org.spongepowered.api.event.entity.AttackEntityEvent;

/**
 * An <code>Alert</code> for the Attack <code>GriefEvent</code>.
 *
 * @see GriefEvents
 */
public class AttackEntityAlert extends EntityAlert {

  private UUID grieferUuid;

  /**
   * General constructor.
   *
   * @param griefProfile the <code>GriefProfile</code>
   * @param event        the event which triggered the alert
   */
  public AttackEntityAlert(@Nonnull final GriefProfile griefProfile,
                           @Nonnull final AttackEntityEvent event,
                           @Nonnull final UUID grieferUuid) {
    super(griefProfile, event);
    this.grieferUuid = grieferUuid;

    if (griefProfile.getTarget().equals("minecraft:item_frame")) {
      addDetail(getItemFrameDetail());
    }
    if (griefProfile.getTarget().equals("minecraft:armor_stand")) {
      addDetail(getArmorStandDetail());
    }

  }

  public AttackEntityAlert(@Nonnull final GriefProfile griefProfile,
                           @Nonnull final AttackEntityEvent event,
                           @Nonnull final UUID grieferUuid,
                           @Nonnull final String tool) {
    this(griefProfile, event, grieferUuid);
    addDetail(Detail.of(
        "Tool",
        "The item in the hand of the player at the time of the event.",
        Format.item(tool)));
  }

  @Nonnull
  @Override
  public UUID getGrieferUuid() {
    return grieferUuid;
  }

}
