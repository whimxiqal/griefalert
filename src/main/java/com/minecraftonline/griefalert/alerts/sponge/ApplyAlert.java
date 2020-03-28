package com.minecraftonline.griefalert.alerts.sponge;

import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;

public class ApplyAlert extends SpongeAlert {

  private final Transform<World> grieferTransform;

  private ApplyAlert(GriefProfile griefProfile, InteractBlockEvent.Secondary event) {
    super(griefProfile, event);
    this.grieferTransform = getGriefer().getTransform();
    this.addDetail(Detail.of(
        "Applied To",
        "The object on which the target item is applied.",
        Format.item(event.getTargetBlock().getState().getType().getId())));
  }


  public static ApplyAlert of(GriefProfile griefProfile, InteractBlockEvent.Secondary event) {
    return new ApplyAlert(griefProfile, event);
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
