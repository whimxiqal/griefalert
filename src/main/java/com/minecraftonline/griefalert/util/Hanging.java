package com.minecraftonline.griefalert.util;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.event.entity.InteractEntityEvent;

import java.util.Optional;

public final class Hanging {

  private Hanging() {
  }

  public static String getItemFrameContentMessage(InteractEntityEvent event) {

    DataContainer container = event.getTargetEntity().toContainer();

    Optional<DataView> unsafeOptional = container.getView(DataQuery.of("UnsafeData"));
    if (!unsafeOptional.isPresent()) {
      return "empty";
    }

    Optional<DataView> itemOptional = unsafeOptional.get().getView(DataQuery.of("Item"));
    if (!itemOptional.isPresent()) {
      return "empty";
    }

    Optional<String> idOptional = itemOptional.get().getString(DataQuery.of("id")).map(Format::removeMinecraftPrefix);
    if (!idOptional.isPresent()) {
      return "empty";
    }

    return "containing " + idOptional.get();
  }

}
