package com.minecraftonline.griefalert.api.alerts;

import com.minecraftonline.griefalert.util.Format;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.*;

public abstract class Alert {

  private int cacheCode;
  protected DataContainer dataContainer;
  protected Transform<World> transform;


  /**
   * Default constructor.
   * @param cacheCode The integer which corresponds to this Alert's location
   *                  in the AlertCache
   */
  public Alert(int cacheCode, Transform<World> transform) {
    this.cacheCode = cacheCode;
    this.transform = transform;
  }


  /**
   * Getter for the bare Text without interactivity.
   *
   * @return the Text representation of this Alert.
   */
  public abstract Text getMessageText();


  /**
   * Getter for the integer which represents this cached item in the ongoing.
   * AlertCache, which implements a RotatingQueue.
   *
   * @return integer for cache code
   */
  final int getCacheCode() {
    return cacheCode;
  }


  /**
   * Add the data to this Alert's data container. The appropriate data
   * must be put into the container to be appropriately parsed by
   * the Alert instance.
   * @param path The DataQuery to set with.
   * @param value The value to set with.
   */
  final void setData(DataQuery path, Object value) {
    dataContainer.set(path, value);
  }


  /**
   * Get the data from this Alert's data container.
   * @param path The DataQuery to access with.
   */
  final void getData(DataQuery path) {
    dataContainer.get(path);
  }


  public final Transform<World> getTransform() {
    return transform;
  }

  /**
   * Get the final text version of the Alert to broadcast to staff.
   * @return the Text for broadcasting.
   */
  public final Text getFullText() {
    return Text.of(getMessageText(), " ", Format.command(String.valueOf(getCacheCode()),
        "/g check " + getCacheCode(),
        Text.of("CHECK GRIEF ALERT")
    ));
  }


  /**
   * Get the final text version of the Alert to broadcast to staff with
   * other interactive items to access other alerts.
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
