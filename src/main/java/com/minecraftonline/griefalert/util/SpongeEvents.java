package com.minecraftonline.griefalert.util;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.event.entity.InteractEntityEvent;

import java.util.List;
import java.util.Optional;

public final class SpongeEvents {

  private SpongeEvents() {
  }

  public static Optional<String> getItemFrameContent(InteractEntityEvent event) {
    DataContainer container = event.getTargetEntity().toContainer();

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

  public static String getItemFrameContentMessage(InteractEntityEvent event) {
    return getItemFrameContent(event).map((id) -> "containing " + id).orElse("empty");
  }

  public static Optional<String> getArmorStandContent(InteractEntityEvent event) {
    // TODO: implement
    return Optional.empty();
  }

  public static String getArmorStandContentMessage(InteractEntityEvent event) {
    // TODO: implement
    return "empty";
  }

}
