/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert.sponge.alert.listeners;

import com.minecraftonline.griefalert.SpongeGriefAlert;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvent;
import com.minecraftonline.griefalert.sponge.alert.alerts.sponge.ApplyAlert;
import com.minecraftonline.griefalert.sponge.alert.alerts.sponge.ChangeSignAlert;
import com.minecraftonline.griefalert.sponge.alert.alerts.sponge.InteractBlockAlert;
import com.minecraftonline.griefalert.sponge.alert.alerts.sponge.UseAlert;
import com.minecraftonline.griefalert.sponge.alert.alerts.sponge.entities.AttackEntityAlert;
import com.minecraftonline.griefalert.sponge.alert.alerts.sponge.entities.InteractEntityAlert;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.AttackEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.scheduler.Task;

/**
 * A collection of listeners.
 *
 * @author PietElite
 */
public final class SpongeListeners {

  private SpongeListeners() {
  }

  public static void register(SpongeGriefAlert plugin) {
    Sponge.getEventManager().registerListeners(plugin, new SpongeListeners());
  }

  /**
   * A Sponge listener.
   *
   * @param event the event for which to listen
   */
  @Listener(order = Order.LATE)
  public void onInteractItemEventSecondary(InteractItemEvent.Secondary event) {
    if (event.getCause().root() instanceof Player) {
      Player player = (Player) event.getCause().root();

      SpongeGriefAlert.getSpongeInstance()
          .getProfileCache()
          .getProfileOf(
              GriefEvent.ITEM_USE,
              event.getItemStack().getType().getId(),
              player.getLocation().getExtent())
          .ifPresent((profile) ->
              SpongeGriefAlert.getSpongeInstance()
                  .getAlertService()
                  .submit(new UseAlert(profile, event)));
    }
  }

  /**
   * A Sponge listener.
   *
   * @param event the event for which to listen
   */
  @Listener(order = Order.LATE)
  public void onInteractBlockEventSecondary(InteractBlockEvent.Secondary event) {
    if (event.getCause().root() instanceof Player) {
      Player player = (Player) event.getCause().root();

      SpongeGriefAlert.getSpongeInstance()
          .getProfileCache()
          .getProfileOf(
              GriefEvent.INTERACT,
              event.getTargetBlock().getState().getType().getId(),
              player.getLocation().getExtent())
          .ifPresent(profile ->
              Task.builder()
                  .execute(() -> SpongeGriefAlert.getSpongeInstance()
                      .getAlertService()
                      .submit(new InteractBlockAlert(profile, event)))
                  .submit(SpongeGriefAlert.getSpongeInstance()));

      player.getItemInHand(HandTypes.MAIN_HAND)
          .flatMap(stack -> SpongeGriefAlert.getSpongeInstance()
              .getProfileCache().getProfileOf(
                  GriefEvent.ITEM_APPLY,
                  stack.getType().getId(),
                  player.getLocation().getExtent()))
          .ifPresent(profile ->
              SpongeGriefAlert.getSpongeInstance()
                  .getAlertService()
                  .submit(new ApplyAlert(profile, event)));
    }
  }

  /**
   * A Sponge listener.
   *
   * @param event the event for which to listen
   */
  @Listener(order = Order.LATE)
  public void onInteractEntityEventSecondary(InteractEntityEvent.Secondary event) {
    if (event.getCause().root() instanceof Player) {
      Player player = (Player) event.getCause().root();

      SpongeGriefAlert.getSpongeInstance()
          .getProfileCache()
          .getProfileOf(
              GriefEvent.INTERACT,
              event.getTargetEntity().getType().getId(),
              player.getLocation().getExtent())
          .ifPresent((profile) ->
              SpongeGriefAlert.getSpongeInstance()
                  .getAlertService()
                  .submit(new InteractEntityAlert(profile, event)));
    }
  }

  /**
   * A Sponge listener.
   *
   * @param event the event for which to listen
   */
  @Listener(order = Order.LATE)
  public void onAttackEntityEvent(AttackEntityEvent event) {

    if (event.getCause().root() instanceof EntityDamageSource) {
      EntityDamageSource damageSource = (EntityDamageSource) event.getCause().root();

      if (damageSource instanceof IndirectEntityDamageSource) {
        IndirectEntityDamageSource indirectDamageSource = (IndirectEntityDamageSource) damageSource;

        if (indirectDamageSource.getIndirectSource() instanceof Player) {

          Player player = (Player) indirectDamageSource.getIndirectSource();
          String directCause = damageSource.getSource().getType().getId();

          SpongeGriefAlert.getSpongeInstance()
              .getProfileCache()
              .getProfileOf(
                  GriefEvent.ATTACK,
                  event.getTargetEntity().getType().getId(),
                  player.getLocation().getExtent())
              .ifPresent(profile ->
                  SpongeGriefAlert.getSpongeInstance()
                      .getAlertService()
                      .submit(new AttackEntityAlert(profile,
                          event,
                          player.getUniqueId(),
                          player,
                          directCause)));
        }

      } else if (damageSource.getSource() instanceof Player) {
        Player player = (Player) damageSource.getSource();

        SpongeGriefAlert.getSpongeInstance()
            .getProfileCache()
            .getProfileOf(
                GriefEvent.ATTACK,
                event.getTargetEntity().getType().getId(),
                player.getLocation().getExtent())
            .ifPresent(profile ->
                SpongeGriefAlert.getSpongeInstance()
                    .getAlertService()
                    .submit(new AttackEntityAlert(profile,
                        event,
                        player.getUniqueId(),
                        player)));
      }
    }
  }

  /**
   * A listener for the information on a sign.
   *
   * @param event the event
   */
  @Listener(order = Order.LATE)
  public void onChangeSignEvent(ChangeSignEvent event) {
    event.getCause()
        .first(Player.class)
        .flatMap(player ->
            SpongeGriefAlert.getSpongeInstance()
                .getProfileCache()
                .getProfileOf(
                    GriefEvent.EDIT,
                    event.getTargetTile().getBlock().getType().getId(),
                    player.getLocation().getExtent()))
        .ifPresent(griefProfile ->
            SpongeGriefAlert.getSpongeInstance()
                .getAlertService()
                .submit(new ChangeSignAlert(griefProfile, event)));
  }

}
