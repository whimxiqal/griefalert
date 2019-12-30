package com.minecraftonline.griefalert.alerts;

import com.helion3.prism.api.records.PrismRecord;
import com.minecraftonline.griefalert.api.profiles.GriefProfile;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.text.Text;

public class SignPlaceAlert extends PrismAlert {

  SignPlaceAlert(int cacheCode, GriefProfile griefProfile, DataContainer prismDataContainer) {
    super(cacheCode, griefProfile, prismDataContainer);
  }

  @Override
  public Text getMessageText() {
    // TODO: Write message text for SignPlaceAlert
    return Text.of("SignPlaceAlert");
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
//        Prism.getPlacedSignLines(record).map((lines) -> Text.of(
//            TextColors.WHITE,
//            "\n", "Line 1: ", lines.get(0),
//            "\n", "Line 2: ", lines.get(1),
//            "\n", "Line 3: ", lines.get(2),
//            "\n", "Line 4: ", lines.get(3)
//        )).orElse(Text.of("No sign data available"))
//    );
  }

}
