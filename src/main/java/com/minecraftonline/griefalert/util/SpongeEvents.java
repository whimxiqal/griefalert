/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.event.entity.InteractEntityEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public final class SpongeEvents {

  private SpongeEvents() {
  }

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

  public static String getItemFrameContentMessage(EntitySnapshot entitySnapshot) {
    return getItemFrameContent(entitySnapshot).map((id) -> "containing " + id).orElse("empty");
  }

  public static Optional<List<String>> getArmorStandContent(EntitySnapshot entitySnapshot) {
    DataContainer container = entitySnapshot.toContainer();

    Optional<DataView> unsafeOptional = container.getView(DataQuery.of("UnsafeData"));
    if (!unsafeOptional.isPresent()) {
      return Optional.empty();
    }

    Optional<List<DataView>> attributesOptional = unsafeOptional.get().getViewList(DataQuery.of("ArmorItems"));
    if (!attributesOptional.isPresent()) {
      return Optional.empty();
    }

    List<String> output = new LinkedList<>();

    for (DataView view : attributesOptional.get()) {
      if (view.contains(DataQuery.of("id"))) {
        output.add(view.getString(DataQuery.of("id")).get());
      }
    }
    return Optional.of(output);
  }

  public static String getArmorStandContentMessage(EntitySnapshot entitySnapshot) {
    return getArmorStandContent(entitySnapshot).map((list) -> String.join(", ", list)).orElse("empty");
  }

}
