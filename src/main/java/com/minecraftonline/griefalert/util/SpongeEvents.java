package com.minecraftonline.griefalert.util;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.event.entity.InteractEntityEvent;

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

  public static Optional<String> getArmorStandContent(EntitySnapshot entitySnapshot) {
    // TODO: implement
    return Optional.empty();
  }

  public static String getArmorStandContentMessage(EntitySnapshot entitySnapshot) {
    // TODO: implement
    return "empty";
  }

}
