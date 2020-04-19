/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.griefalert.alerts.sponge.SpongeAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.SpongeUtil;
import com.minecraftonline.griefalert.util.enums.Details;
import javax.annotation.Nonnull;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.event.entity.TargetEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class EntityAlert extends SpongeAlert {

  private String entitySnapshotContainer;

  EntityAlert(GriefProfile griefProfile, TargetEntityEvent event) {
    super(griefProfile, event);
    try {
      entitySnapshotContainer = DataFormats.JSON.write(event.getTargetEntity().createSnapshot().toContainer());
    } catch (IOException e) {
      e.printStackTrace();
      entitySnapshotContainer = "";
    }
    addDetail(Details.lookingAt());
  }

  public EntitySnapshot getEntitySnapshot() {
    try {
      return Sponge.getDataManager().deserialize(EntitySnapshot.class, DataFormats.JSON.read(entitySnapshotContainer)).get();
    } catch (IOException | ClassCastException e) {
      e.printStackTrace();
      return EntitySnapshot.builder().build();
    }
  }

  @Nonnull
  @Override
  public Vector3i getGriefPosition() {
    return getEntitySnapshot().getLocation().map(Location::getBlockPosition)
        .orElseThrow(() ->
            new RuntimeException("Couldn't find an entities location for an Entity Alert"));
  }

  protected Detail<Alert> getItemFrameDetail() {
    return Detail.of(
        "Content",
        "The item in this item frame at the time of the event.",
        entityAlert -> Optional.of(SpongeUtil.getItemFrameContent(getEntitySnapshot())
            .map(Format::item)
            .orElse(Format.bonus("none"))));
  }

  protected Detail<Alert> getArmorStandDetail() {
    return Detail.of(
        "Contents",
        "All the items in the armor stand at the time of the event.",
        entityAlert -> Optional.of(SpongeUtil.getArmorStandContent(getEntitySnapshot())
            .map(list -> list.stream()
                .map(Format::item)
                .collect(Collectors.toList()))
            .filter(list -> !list.isEmpty())
            .map(list -> Text.joinWith(Text.of(", ", list)))
            .orElse(Format.bonus("none"))));
  }

}
