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

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.event.entity.TargetEntityEvent;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class EntityAlert extends SpongeAlert {

  private final String entitySnapshotContainer;
  private final Vector3i griefPosition;

  EntityAlert(GriefProfile griefProfile, TargetEntityEvent event) {
    super(griefProfile, event);
    this.griefPosition = event.getTargetEntity().getLocation().getBlockPosition();
    String serialized;
    try {
      serialized = DataFormats.JSON.write(event.getTargetEntity().createSnapshot().toContainer());
    } catch (IOException e) {
      e.printStackTrace();
      serialized = "";
    }
    entitySnapshotContainer = serialized;
    addDetail(Details.lookingAt());
  }

  public DataContainer getEntitySnapshot() {
    try {
      return DataFormats.JSON.read(entitySnapshotContainer);
    } catch (IOException | ClassCastException e) {
      e.printStackTrace();
      return DataContainer.createNew();
    }
  }

  @Nonnull
  @Override
  public Vector3i getGriefPosition() {
    return griefPosition;
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
