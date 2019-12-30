package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.UseAlert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;

import java.util.Optional;

public final class ExtraListeners {

  public static void register(GriefAlert plugin) {
    Sponge.getEventManager().registerListeners(plugin, new ExtraListeners());
  }

  @Listener
  public void onInteractItemEventSecondary(InteractItemEvent.Secondary event) {
    if (event.getCause().root() instanceof Player) {
      Player player = (Player) event.getCause().root();

      Optional<GriefProfile> optionalProfile = GriefAlert.getInstance().getProfileCabinet().getProfileOf(
          GriefEvents.ITEM_USE,
          event.getItemStack().getType().getId(),
          player.getLocation().getExtent().getDimension().getType());

      optionalProfile.ifPresent((profile) -> UseAlert.of(profile, event).pushAndRun());
    }
  }

  // TODO: add listener for item frame
  // TODO: add listener for armor stand

  private ExtraListeners() {
  }

}
