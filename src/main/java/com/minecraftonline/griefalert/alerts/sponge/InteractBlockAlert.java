/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.GriefEvents;
import java.util.Optional;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.event.block.InteractBlockEvent;
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

  @Override
  public Text getMessageText() {
    Text.Builder builder = Text.builder();
    builder.append(Text.of(
        General.formatPlayerName(getGriefer()),
        Format.space(),
        getEventColor(), getGriefEvent().getPreterite(),
        Format.space(),
        getTargetColor(), Grammar.addIndefiniteArticle(getTarget().replace("minecraft:", "")),
        Format.space(),
        "holding",
        Format.space(),
        Optional.ofNullable(handHeldItem)
            .map((itemStack -> Grammar.addIndefiniteArticle(
                itemStack
                    .getType()
                    .getId()
                    .replace("minecraft:", ""))))
            .orElse("nothing")));

    getTransform().ifPresent((transform -> builder.append(Text.of(
        TextColors.RED, " in the ",
        getDimensionColor(), transform.getExtent().getDimension().getType().getName()))));
    return builder.build();
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.INTERACT;
  }
}
