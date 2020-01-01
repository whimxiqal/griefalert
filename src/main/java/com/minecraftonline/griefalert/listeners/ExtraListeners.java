package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.sponge.InteractBlockAlert;
import com.minecraftonline.griefalert.alerts.sponge.UseAlert;
import com.minecraftonline.griefalert.alerts.sponge.entities.AttackEntityAlert;
import com.minecraftonline.griefalert.alerts.sponge.entities.InteractEntityAlert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;

import java.util.Optional;

public final class ExtraListeners {

  private ExtraListeners() {
  }

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

  @Listener
  public void onInteractBlockEventSecondary(InteractBlockEvent.Secondary event) {
    if (event.getCause().root() instanceof Player) {
      Player player = (Player) event.getCause().root();

      Optional<GriefProfile> optionalProfile = GriefAlert.getInstance().getProfileCabinet().getProfileOf(
          GriefEvents.INTERACT,
          event.getTargetBlock().getState().getType().getId(),
          player.getLocation().getExtent().getDimension().getType());

      optionalProfile.ifPresent((profile) -> InteractBlockAlert.of(profile, event).pushAndRun());
    }
  }

  @Listener
  public void onInteractEntityEventSecondary(InteractEntityEvent.Secondary event) {
    if (event.getCause().root() instanceof Player) {
      Player player = (Player) event.getCause().root();

      Optional<GriefProfile> optionalProfile = GriefAlert.getInstance().getProfileCabinet().getProfileOf(
          GriefEvents.INTERACT,
          event.getTargetEntity().getType().getId(),
          player.getLocation().getExtent().getDimension().getType());

      optionalProfile.ifPresent((profile) -> InteractEntityAlert.of(profile, event).pushAndRun());
    }
  }

  @Listener
  public void onInteractEntityEventPrimary(InteractEntityEvent.Primary event) {

    if (event.getCause().root() instanceof Player) {

      Player player = (Player) event.getCause().root();

      Optional<GriefProfile> optionalProfile = GriefAlert.getInstance().getProfileCabinet().getProfileOf(
          GriefEvents.ATTACK,
          event.getTargetEntity().getType().getId(),
          player.getLocation().getExtent().getDimension().getType());

      optionalProfile.ifPresent((profile) -> AttackEntityAlert.of(profile, event).pushAndRun());
    }
  }

}
