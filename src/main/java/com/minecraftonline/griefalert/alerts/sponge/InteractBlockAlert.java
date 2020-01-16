/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Grammar;

import java.util.Optional;
import javax.annotation.Nonnull;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class InteractBlockAlert extends SpongeAlert {

  private final ItemStack handHeldItem;
  private final Location<World> griefLocation;
  private final Transform<World> grieferTransform;

  private InteractBlockAlert(GriefProfile griefProfile, InteractBlockEvent event) {
    super(griefProfile, event);
    handHeldItem = getGriefer().getItemInHand(HandTypes.MAIN_HAND).orElse(null);
    griefLocation = event.getTargetBlock().getLocation().orElseThrow(() ->
        new RuntimeException("Couldn't find the location of a block in an IneteractBlockAlert"));
    this.grieferTransform = getGriefer().getTransform();
  }

  public static InteractBlockAlert of(GriefProfile griefProfile, InteractBlockEvent event) {
    return new InteractBlockAlert(griefProfile, event);
  }

  @Nonnull
  @Override
  public Text.Builder getMessageTextBuilder() {
    Text.Builder builder = Text.builder();
    builder.append(Text.of(
        Format.playerName(getGriefer()),
        Format.space(),
        getEventColor(), getGriefEvent().getPreterite(),
        Format.space(),
        getTargetColor(), Grammar.addIndefiniteArticle(getTarget().replace("minecraft:", "")),
        Format.space(),
        "holding",
        Format.space(),
        Optional.ofNullable(handHeldItem)
            .filter((itemStack) -> !itemStack.getType().equals(ItemTypes.AIR))
            .map(itemStack -> Grammar.addIndefiniteArticle(Format.item(
                itemStack.getType().getId())))
            .orElse(Text.of("nothing")),
        TextColors.RED, " in the ",
        getDimensionColor(), getGrieferTransform().getExtent().getDimension().getType().getName()));
    return builder;
  }

  @Nonnull
  @Override
  public Transform<World> getGrieferTransform() {
    return grieferTransform;
  }

  @Nonnull
  @Override
  public Location<World> getGriefLocation() {
    return griefLocation.add(0.5, 0.5, 0.5);
  }

}
