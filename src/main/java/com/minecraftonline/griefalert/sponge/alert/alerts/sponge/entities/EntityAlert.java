/* Created by PietElite */

package com.minecraftonline.griefalert.sponge.alert.alerts.sponge.entities;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.griefalert.sponge.alert.alerts.sponge.SpongeAlert;
import com.minecraftonline.griefalert.common.alert.alerts.Alert;
import com.minecraftonline.griefalert.common.alert.alerts.Detail;
import com.minecraftonline.griefalert.common.alert.records.GriefProfile;
import com.minecraftonline.griefalert.sponge.alert.util.Format;
import com.minecraftonline.griefalert.sponge.alert.util.SpongeUtil;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Details;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.entity.ArtData;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.TargetEntityEvent;
import org.spongepowered.api.text.Text;

/**
 * An alert caused by an entity.
 */
public abstract class EntityAlert extends SpongeAlert {

  private final String entitySnapshotContainer;
  private final Vector3i griefPosition;

  EntityAlert(GriefProfile griefProfile, TargetEntityEvent event, Supplier<Player> supplier) {
    super(griefProfile, supplier);
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

    // Add Art type, in the case of paintings
    addDetail(Detail.of(
        "Art Type",
        "The displayed piece of art",
        alert -> event.getTargetEntity()
            .get(ArtData.class)
            .flatMap(data -> data.get(Keys.ART))
            .map(art -> Format.hover(art.getName(), "ID: " + art.getId()))));
  }

  /**
   * Get the entity snapshot associated with this alert.
   *
   * @return the data container of the entity snapshot
   */
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
