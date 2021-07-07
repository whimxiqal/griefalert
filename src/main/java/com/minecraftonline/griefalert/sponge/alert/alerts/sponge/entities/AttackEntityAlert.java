/* Created by PietElite */

package com.minecraftonline.griefalert.sponge.alert.alerts.sponge.entities;

import com.minecraftonline.griefalert.common.alert.alerts.Alert;
import com.minecraftonline.griefalert.common.alert.alerts.Detail;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvent;
import com.minecraftonline.griefalert.common.alert.records.GriefProfile;
import com.minecraftonline.griefalert.sponge.alert.util.Format;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.AttackEntityEvent;

/**
 * An {@link Alert} for the Attack {@link GriefEvent}.
 *
 * @author PietElite
 */
public class AttackEntityAlert extends EntityAlert {

  private final UUID grieferUuid;

  /**
   * General constructor.
   *
   * @param griefProfile the grief profile
   * @param event        the event which triggered the alert
   */
  public AttackEntityAlert(@Nonnull final GriefProfile griefProfile,
                           @Nonnull final AttackEntityEvent event,
                           @Nonnull final UUID grieferUuid,
                           @Nonnull final Player player) {
    super(griefProfile, event, () -> player);
    this.grieferUuid = grieferUuid;

    if (griefProfile.getTarget().equals("minecraft:item_frame")) {
      addDetail(getItemFrameDetail());
    }
    if (griefProfile.getTarget().equals("minecraft:armor_stand")) {
      addDetail(getArmorStandDetail());
    }

  }

  /**
   * Explicit constructor.
   *
   * @param griefProfile the grief profile
   * @param event the event
   * @param grieferUuid the grief uuid
   * @param player the player
   * @param tool the tool which was used during the attack
   */
  public AttackEntityAlert(@Nonnull final GriefProfile griefProfile,
                           @Nonnull final AttackEntityEvent event,
                           @Nonnull final UUID grieferUuid,
                           @Nonnull final Player player,
                           @Nonnull final String tool) {
    this(griefProfile, event, grieferUuid, player);
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
