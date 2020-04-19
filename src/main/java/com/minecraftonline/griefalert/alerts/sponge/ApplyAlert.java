/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class ApplyAlert extends SpongeAlert {

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
