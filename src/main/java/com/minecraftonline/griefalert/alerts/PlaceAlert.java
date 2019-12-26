package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.util.Format;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlaceAlert extends Alert {

  private final TextColor eventColor = Format.ALERT_ACTION_COLOR;
  private final TextColor targetColor = Format.ALERT_TARGET_COLOR;
  private final TextColor dimensionColor = Format.ALERT_DIMENSION_COLOR;

  PlaceAlert(int cacheCode, Transform<World> transform) {
    super(cacheCode, transform);
  }

  public Text getMessageText() {
    // TODO: Write message text for PlaceAlert
    return Text.of("PlaceAlert text");
  }

}
