/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge.entities;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.GriefEvents;
import com.minecraftonline.griefalert.util.SpongeEvents;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.TargetEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tuple;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An <code>Alert</code> for the Attack <code>GriefEvent</code>.
 *
 * @see GriefEvents
 */
public class AttackEntityAlert extends EntityAlert {

  private final Player griefer;
  private final Transform<World> griefTransform;

  public AttackEntityAlert(@Nonnull final GriefProfile griefProfile,
                           @Nonnull final AttackEntityEvent event,
                           @Nonnull final Player griefer) {
    super(griefProfile, event);
    this.griefer = griefer;
    this.griefTransform = griefer.getTransform();

    if (griefProfile.getTarget().equals("minecraft:item_frame")) {
      addSummaryContent(
          "Content",
          SpongeEvents.getItemFrameContent(getEntitySnapshot())
              .map(Format::item)
              .orElse(Format.bonus("none")));
    }
    if (griefProfile.getTarget().equals("minecraft:armor_stand")) {
      addSummaryContent("Contents", SpongeEvents.getArmorStandContent(getEntitySnapshot())
          .map(list -> list.stream()
              .map(Format::item)
              .collect(Collectors.toList()))
          .filter(list -> !list.isEmpty())
          .map(list -> Text.joinWith(Text.of(", ", list)))
          .orElse(Format.bonus("none")));
    }

  }

  public AttackEntityAlert(@Nonnull final GriefProfile griefProfile,
                           @Nonnull final AttackEntityEvent event,
                           @Nonnull final Player griefer,
                           @Nonnull final String tool) {
    this(griefProfile, event, griefer);
    addSummaryContent("Tool", Format.item(tool));
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
