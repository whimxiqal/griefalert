package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.text.Text;

public class SignBreakAlert extends PrismAlert {

  SignBreakAlert(GriefProfile griefProfile, DataContainer prismDataContainer) {
    super(griefProfile, prismDataContainer);
  }

  @Override
  public Text getMessageText() {
    // TODO: Write message text for SignBreakAlert
    return Text.of("SignBreakAlert");
//    return Text.of(
//        defaultColor,
//        getSubject(record), " ",
//        getAction(record), " ",
//        getGriefedObject(record), " in ",
//        getDimension(record), ". ",
//        Format.command(String.valueOf(cacheCode),
//            "/g check " + cacheCode,
//            Text.of("CHECK GRIEF ALERT")
//        ),
//        Prism.getBrokenSignLines(record).map((lines) -> Text.of(
//            TextColors.WHITE,
//            "\n", "Line 1: ", lines.get(0),
//            "\n", "Line 2: ", lines.get(1),
//            "\n", "Line 3: ", lines.get(2),
//            "\n", "Line 4: ", lines.get(3)
//        )).orElse(Text.of("No sign data available"))
//    );
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.BREAK;
  }

}
