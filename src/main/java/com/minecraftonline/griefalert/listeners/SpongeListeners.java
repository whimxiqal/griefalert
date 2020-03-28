/* Created by PietElite */

package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.sponge.InteractBlockAlert;
import com.minecraftonline.griefalert.alerts.sponge.UseAlert;
import com.minecraftonline.griefalert.alerts.sponge.entities.AttackEntityAlert;
import com.minecraftonline.griefalert.alerts.sponge.entities.InteractEntityAlert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.enums.GriefEvents;

import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;

public final class SpongeListeners {

  private SpongeListeners() {
  }

  public static void register(GriefAlert plugin) {
    Sponge.getEventManager().registerListeners(plugin, new SpongeListeners());
  }

  /**
   * A Sponge listener.
   *
   * @param event the event for which to listen
   */
  @Listener
  public void onInteractItemEventSecondary(InteractItemEvent.Secondary event) {
    if (event.getCause().root() instanceof Player) {
      Player player = (Player) event.getCause().root();

      Optional<GriefProfile> optionalProfile = GriefAlert.getInstance()
          .getProfileCache().getProfileOf(
              GriefEvents.ITEM_USE,
              event.getItemStack().getType().getId(),
              player.getLocation().getExtent().getDimension().getType());

      optionalProfile.ifPresent((profile) -> new UseAlert(profile, event).run());
    }
  }

  /**
   * A Sponge listener.
   *
   * @param event the event for which to listen
   */
  @Listener
  public void onInteractBlockEventSecondary(InteractBlockEvent.Secondary event) {
    if (event.getCause().root() instanceof Player) {
      Player player = (Player) event.getCause().root();

      Optional<GriefProfile> optionalProfile = GriefAlert.getInstance()
          .getProfileCache().getProfileOf(
              GriefEvents.INTERACT,
              event.getTargetBlock().getState().getType().getId(),
              player.getLocation().getExtent().getDimension().getType());

      optionalProfile.ifPresent((profile) -> InteractBlockAlert.of(profile, event).run());
    }
  }

  /**
   * A Sponge listener.
   *
   * @param event the event for which to listen
   */
  @Listener
  public void onInteractEntityEventSecondary(InteractEntityEvent.Secondary event) {
    if (event.getCause().root() instanceof Player) {
      Player player = (Player) event.getCause().root();

      Optional<GriefProfile> optionalProfile = GriefAlert.getInstance()
          .getProfileCache().getProfileOf(
              GriefEvents.INTERACT,
              event.getTargetEntity().getType().getId(),
              player.getLocation().getExtent().getDimension().getType());

      optionalProfile.ifPresent((profile) -> InteractEntityAlert.of(profile, event).run());
    }
  }

  /**
   * A Sponge listener.
   *
   * @param event the event for which to listen
   */
  @Listener
  public void onAttackEntityEvent(AttackEntityEvent event) {

    if (event.getCause().root() instanceof EntityDamageSource) {
      EntityDamageSource damageSource = (EntityDamageSource) event.getCause().root();

      if (damageSource instanceof IndirectEntityDamageSource) {
        IndirectEntityDamageSource indirectDamageSource = (IndirectEntityDamageSource) damageSource;

        if (indirectDamageSource.getIndirectSource() instanceof Player) {

          Player player = (Player) indirectDamageSource.getIndirectSource();
          String directCause = damageSource.getSource().getType().getId();

          Optional<GriefProfile> optionalProfile = GriefAlert.getInstance()
              .getProfileCache().getProfileOf(
                  GriefEvents.ATTACK,
                  event.getTargetEntity().getType().getId(),
                  player.getLocation().getExtent().getDimension().getType());

          optionalProfile.ifPresent((profile) ->
              new AttackEntityAlert(profile, event, player, directCause).run());
        }

      } else if (damageSource.getSource() instanceof Player) {
        Player player = (Player) damageSource.getSource();

        Optional<GriefProfile> optionalProfile = GriefAlert.getInstance()
            .getProfileCache().getProfileOf(
                GriefEvents.ATTACK,
                event.getTargetEntity().getType().getId(),
                player.getLocation().getExtent().getDimension().getType());

        optionalProfile.ifPresent((profile) ->
            new AttackEntityAlert(profile, event, player).run());
      }
    }
  }

}
