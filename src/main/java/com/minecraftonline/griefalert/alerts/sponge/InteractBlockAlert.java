/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Grammar;

import java.util.Optional;
import javax.annotation.Nonnull;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class InteractBlockAlert extends SpongeAlert {

  private final ItemStack handHeldItem;

  private InteractBlockAlert(GriefProfile griefProfile, InteractBlockEvent event) {
    super(griefProfile, event);
    handHeldItem = getGriefer().getItemInHand(HandTypes.MAIN_HAND).orElse(null);
  }

  public static InteractBlockAlert of(GriefProfile griefProfile, InteractBlockEvent event) {
    return new InteractBlockAlert(griefProfile, event);
  }

  @Nonnull
  @Override
  public Text getMessageText() {
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
            .map((itemStack -> Grammar.addIndefiniteArticle(
                itemStack
                    .getType()
                    .getId()
                    .replace("minecraft:", ""))))
            .orElse("nothing"),
        TextColors.RED, " in the ",
        getDimensionColor(), getGrieferTransform().getExtent().getDimension().getType().getName()));
    return builder.build();
  }

}
