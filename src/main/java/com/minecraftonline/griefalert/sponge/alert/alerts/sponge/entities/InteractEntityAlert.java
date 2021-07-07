/* Created by PietElite */

package com.minecraftonline.griefalert.sponge.alert.alerts.sponge.entities;

import com.minecraftonline.griefalert.common.alert.alerts.Detail;
import com.minecraftonline.griefalert.common.alert.records.GriefProfile;
import com.minecraftonline.griefalert.sponge.alert.util.Format;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.InteractEntityEvent;

/**
 * An alert caused by interacting with an entity.
 *
 * @author PietElite
 */
public class InteractEntityAlert extends EntityAlert {

  /**
   * Default constructor.
   *
   * @param griefProfile the grief profile
   * @param event        the event
   */
  public InteractEntityAlert(@Nonnull GriefProfile griefProfile,
                             @Nonnull InteractEntityEvent.Secondary event) {
    super(griefProfile, event, () -> event.getCause().first(Player.class).orElseThrow(() ->
        new RuntimeException("InteractEntityAlert couldn't find a player in the cause stack")));

    if (griefProfile.getTarget().equals("minecraft:item_frame")) {
      addDetail(getItemFrameDetail());
    }
    if (griefProfile.getTarget().equals("minecraft:armor_stand")) {
      addDetail(getArmorStandDetail());
    }

  }

  /**
   * Constructor to include the tool that was used during interacting with an entity.
   *
   * @param griefProfile the grief profile
   * @param event        the event
   * @param tool         the tool
   */
  public InteractEntityAlert(@Nonnull GriefProfile griefProfile,
                             @Nonnull InteractEntityEvent.Secondary event,
                             @Nonnull final String tool) {
    this(griefProfile, event);
    addDetail(Detail.of(
        "Tool",
        "The item in the hand of the player at the time of the event.",
        Format.item(tool)));
  }

}
