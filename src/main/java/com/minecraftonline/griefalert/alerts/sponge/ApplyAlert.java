/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.data.GriefEvents;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import javax.annotation.Nonnull;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.text.Text;

/**
 * An alert type which uses a {@link GriefEvents#ITEM_APPLY} event.
 */
public class ApplyAlert extends SpongeAlert {

  /**
   * Default constructor.
   *
   * @param griefProfile the grief profile which flagged the event
   * @param event        the event which triggered the event
   */
  public ApplyAlert(GriefProfile griefProfile, InteractBlockEvent.Secondary event) {
    super(griefProfile, event);
    this.addDetail(Detail.of(
        "Applied To",
        "The object on which the target item is applied.",
        Text.of(event.getTargetBlock().getState().getType().getTranslation().get())));
  }

  @Nonnull
  @Override
  public Vector3i getGriefPosition() {
    return getGrieferPosition().toInt();
  }
}
