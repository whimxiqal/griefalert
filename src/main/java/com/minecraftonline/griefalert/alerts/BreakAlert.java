package com.minecraftonline.griefalert.alerts;

import com.helion3.prism.api.records.PrismRecord;
import com.minecraftonline.griefalert.api.profiles.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.Grammar;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class BreakAlert extends PrismAlert {

  BreakAlert(int cacheCode, GriefProfile griefProfile, DataContainer prismDataContainer) {
    super(cacheCode, griefProfile, prismDataContainer);
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
