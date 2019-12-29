package com.minecraftonline.griefalert.alerts;

import com.helion3.prism.api.records.PrismRecord;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.profiles.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.Prism;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;
import java.util.UUID;

public class BreakAlert extends PrismAlert {

  public BreakAlert(int cacheCode, GriefProfile griefProfile, PrismRecord prismRecord) {
    super(cacheCode, griefProfile, prismRecord);
  }

  @Override
  public Text getMessageText() {
    // TODO: Write message text for BreakAlert
    return Text.of(
        General.formatPlayerName(getGriefer()),
        Format.space(),
        getEventColor(), "broke ",
        getTargetColor(), Grammar.addIndefiniteArticle(griefProfile.getTarget().replace("minecraft:", "")),
        TextColors.RED, " in the ",
        getDimensionColor(), getTransform().get().getExtent().getDimension().getType());
  }

}
