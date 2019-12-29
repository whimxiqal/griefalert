package com.minecraftonline.griefalert.api.alerts;

import com.minecraftonline.griefalert.api.profiles.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.World;

import java.util.*;

public abstract class Alert {

  protected final int cacheCode;
  protected final GriefProfile griefProfile;
  protected DataContainer dataContainer;


  /**
   * Default constructor.
   *
   * @param cacheCode    The integer which corresponds to this Alert's location
   *                     in the AlertCache
   * @param griefProfile The grief profile which matched this Alert
   */
  protected Alert(int cacheCode, GriefProfile griefProfile) {
    this.cacheCode = cacheCode;
    this.griefProfile = griefProfile;
  }


  /**
   * Getter for the bare Text without interactivity.
   *
   * @return the Text representation of this Alert.
   */
  public abstract Text getMessageText();

  public abstract Optional<Transform<World>> getTransform();

  /**
   * Getter for the integer which represents this cached item in the ongoing.
   * AlertCache, which implements a RotatingQueue.
   *
   * @return integer for cache code
   */
  public final int getCacheCode() {
    return cacheCode;
  }


  /**
   * Add the data to this Alert's data container. The appropriate data
   * must be put into the container to be appropriately parsed by
   * the Alert instance.
   *
   * @param path  The DataQuery to set with.
   * @param value The value to set with.
   */
  public final void setData(DataQuery path, Object value) {
    dataContainer.set(path, value);
  }


  /**
   * Get the data from this Alert's data container.
   *
   * @param path The DataQuery to access with.
   */
  final void getData(DataQuery path) {
    dataContainer.get(path);
  }

  /**
   * Get the final text version of the Alert to broadcast to staff.
   *
   * @return the Text for broadcasting.
   */
  public final Text getFullText() {
    return Text.of(getMessageText(), " ", Format.command(String.valueOf(getCacheCode()),
        "/g check " + getCacheCode(),
        Text.of("CHECK GRIEF ALERT")
    ));
  }

  protected final TextColor getEventColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.EVENT_COLOR)
        .map(General::stringToColor)
        .orElse(Format.ALERT_EVENT_COLOR);
  }

  protected final TextColor getTargetColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.TARGET_COLOR)
        .map(General::stringToColor)
        .orElse(Format.ALERT_TARGET_COLOR);
  }

  protected final TextColor getDimensionColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.DIMENSION_COLOR)
        .map(General::stringToColor)
        .orElse(Format.ALERT_DIMENSION_COLOR);
  }


  /**
   * Get the final text version of the Alert to broadcast to staff with
   * other interactive items to access other alerts.
   *
   * @param otherCodes The list of other items to add to the text.
   * @return A full text to be broadcast to staff.
   */
  final Text getFullText(List<Integer> otherCodes) {
    Text.Builder builder = Text.builder().append(getMessageText());
    builder.append(Text.of(" "));
    List<Integer> codes = otherCodes;
    codes.add(getCacheCode());
    builder.append(Format.command(String.valueOf(getCacheCode()),
        "/g check " + getCacheCode(),
        Text.of("CHECK GRIEF ALERT")
    ));
    codes.forEach((i) -> {
      builder.append(Text.of(" "));
      builder.append(
          Format.command(String.valueOf(i),
              "/g check " + getCacheCode(),
              Text.of("CHECK GRIEF ALERT")
          )
      );
    });
    return builder.build();
  }


}
