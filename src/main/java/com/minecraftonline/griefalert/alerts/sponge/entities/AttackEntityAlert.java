/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.SpongeEvents;
import com.minecraftonline.griefalert.util.enums.GriefEvents;

import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;


/**
 * An <code>Alert</code> for the Attack <code>GriefEvent</code>.
 *
 * @see GriefEvents
 */
public class AttackEntityAlert extends EntityAlert {

  private final Player griefer;
  private final Transform<World> griefTransform;

  /**
   * General constructor.
   *
   * @param griefProfile the <code>GriefProfile</code>
   * @param event        the event which triggered the alert
   * @param griefer      the cause of the event/alert
   */
  public AttackEntityAlert(@Nonnull final GriefProfile griefProfile,
                           @Nonnull final AttackEntityEvent event,
                           @Nonnull final Player griefer) {
    super(griefProfile, event);
    this.griefer = griefer;
    this.griefTransform = griefer.getTransform();

    if (griefProfile.getTarget().equals("minecraft:item_frame")) {
      addDetail(Detail.of(
          "Content",
          "The item in this item frame at the time of the event.",
          SpongeEvents.getItemFrameContent(getEntitySnapshot())
              .map(Format::item)
              .orElse(Format.bonus("none"))));
    }
    if (griefProfile.getTarget().equals("minecraft:armor_stand")) {
      addDetail(Detail.of(
          "Contents",
          "All the items in the armor stand at the time of the event.",
          SpongeEvents.getArmorStandContent(getEntitySnapshot())
              .map(list -> list.stream()
                  .map(Format::item)
                  .collect(Collectors.toList()))
              .filter(list -> !list.isEmpty())
              .map(list -> Text.joinWith(Text.of(", ", list)))
              .orElse(Format.bonus("none"))));
    }

  }

  public AttackEntityAlert(@Nonnull final GriefProfile griefProfile,
                           @Nonnull final AttackEntityEvent event,
                           @Nonnull final Player griefer,
                           @Nonnull final String tool) {
    this(griefProfile, event, griefer);
    addDetail(Detail.of(
        "Tool",
        "The item in the hand of the player at the time of the event.",
        Format.item(tool)));
  }


  @Nonnull
  @Override
  public Player getGriefer() {
    return griefer;
  }

  @Nonnull
  @Override
  public Transform<World> getGrieferTransform() {
    return griefTransform;
  }


}
