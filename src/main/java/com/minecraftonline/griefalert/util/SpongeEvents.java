/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.EntitySnapshot;


public final class SpongeEvents {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private SpongeEvents() {
  }

  /**
   * Get the contents of an item frame.
   *
   * @param entitySnapshot the entity snapshot of type item frame
   * @return an optional of the content id
   */
  public static Optional<String> getItemFrameContent(EntitySnapshot entitySnapshot) {
    DataContainer container = entitySnapshot.toContainer();

    Optional<DataView> unsafeOptional = container.getView(DataQuery.of("UnsafeData"));
    if (!unsafeOptional.isPresent()) {
      return Optional.empty();
    }

    Optional<DataView> itemOptional = unsafeOptional.get().getView(DataQuery.of("Item"));
    if (!itemOptional.isPresent()) {
      return Optional.empty();
    }

    return itemOptional.get().getString(DataQuery.of("id")).map(Format::removeMinecraftPrefix);
  }

  /**
   * Get the message associated with the content of an item frame.
   *
   * @param entitySnapshot the entity snapshot of type item frame
   * @return the message
   */
  public static String getItemFrameContentMessage(EntitySnapshot entitySnapshot) {
    return getItemFrameContent(entitySnapshot).map((id) -> "containing " + id).orElse("empty");
  }

  /**
   * Get the contents of an armor stand.
   *
   * @param entitySnapshot the entity snapshot of type item frame
   * @return an optional of the list of content ids
   */
  public static Optional<List<String>> getArmorStandContent(EntitySnapshot entitySnapshot) {
    DataContainer container = entitySnapshot.toContainer();

    Optional<DataView> unsafeOptional = container.getView(DataQuery.of("UnsafeData"));
    if (!unsafeOptional.isPresent()) {
      return Optional.empty();
    }

    Optional<List<DataView>> attributesOptional = unsafeOptional.get()
        .getViewList(DataQuery.of("ArmorItems"));
    if (!attributesOptional.isPresent()) {
      return Optional.empty();
    }

    List<String> output = new LinkedList<>();

    for (DataView view : attributesOptional.get()) {
      if (view.contains(DataQuery.of("id"))) {
        view.getString(DataQuery.of("id")).ifPresent(output::add);
      }
    }
    return Optional.of(output);
  }

  /**
   * Get the message associated with the content of an armor stand.
   *
   * @param entitySnapshot the entity snapshot of type armor stand
   * @return the message
   */
  @SuppressWarnings("unused")
  public static String getArmorStandContentMessage(EntitySnapshot entitySnapshot) {
    return getArmorStandContent(entitySnapshot).map(
        (list) -> String.join(", ", list))
        .orElse("empty");
  }

}
