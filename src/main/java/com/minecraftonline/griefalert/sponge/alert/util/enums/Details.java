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

package com.minecraftonline.griefalert.sponge.alert.util.enums;

import com.minecraftonline.griefalert.common.alert.alerts.Alert;
import com.minecraftonline.griefalert.common.alert.alerts.Detail;
import com.minecraftonline.griefalert.sponge.alert.util.Alerts;
import com.minecraftonline.griefalert.sponge.alert.util.Format;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.util.blockray.BlockRay;

/**
 * A utility class to enumerate {@link Detail}s.
 */
public final class Details {

  private Details() {
  }

  /**
   * Get a detail which determines a player from an alert.
   *
   * @return the detail
   */
  public static Detail<Alert> player() {
    return Detail.of(
        "Player",
        "The name of the player who caused this event.",
        alert -> Optional.of(Format.userName(Alerts.getGriefer(alert))),
        true);
  }

  /**
   * Get a detail which determines the event from an alert.
   *
   * @return the detail
   */
  public static Detail<Alert> event() {
    return Detail.of(
        "Event",
        "The classification for this type of event.",
        alert -> Optional.of(Format.bonus(Format.action(alert.getGriefEvent()))),
        true);
  }

  /**
   * Get a detail which determines the target from an alert.
   *
   * @return the target
   */
  public static Detail<Alert> target() {
    return Detail.of(
        "Target",
        "The targeted object.",
        alert -> Optional.of(Format.bonus(Format.item(alert.getTarget()))),
        true);
  }

  /**
   * Get a detail which determines the location from an alert.
   *
   * @return the detail
   */
  public static Detail<Alert> location() {
    return Detail.of(
        "Location",
        "The location of the event. This could either be the players location "
            + "or the location of the targeted object, depending on the event type.",
        alert -> Optional.of(Format.bonusLocation(Alerts.getGriefLocation(alert))),
        true);
  }

  /**
   * Get a detail which determines the time from an alert.
   *
   * @return the detail
   */
  public static Detail<Alert> time() {
    return Detail.of(
        "Time",
        "The time of the event.",
        alert -> Optional.of(Format.date(alert.getCreated())));
  }

  /**
   * Get a detail which determines Sponge's block creator player from an alert.
   *
   * @return the detail
   */
  public static Detail<Alert> blockCreator() {
    return Detail.of(
        "Block Creator",
        "The player who placed the block at this location before the break event. "
            + "Based on Sponge's default 'block creator' feature.",
        alert -> Alerts.getWorld(alert)
            .getCreator(alert.getGriefPosition())
            .flatMap(uuid -> Sponge.getServiceManager()
                .provide(UserStorageService.class)
                .flatMap(users -> users.get(uuid)))
            .map(Format::userName));
  }

  /**
   * Get a detail which determines the location at which a player is looking
   * from an alert.
   *
   * @return the detail
   */
  public static Detail<Alert> lookingAt() {
    return Detail.of(
        "Looking At",
        "The object in immediate view during usage of the target.",
        alert -> Sponge.getServer().getPlayer(alert.getGrieferUuid()).flatMap(griefer ->
            BlockRay.from(griefer)
                .stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                .build()
                .end()
                .map(hit -> Format.hover(
                    hit.getLocation().getBlockType().getTranslation().get(),
                    hit.getLocation().getBlockType().getId()))));
  }

  /**
   * Get a detail which determines what is in a player's hand at the time
   * of an alert.
   *
   * @return the detail
   */
  public static Detail<Alert> inHand() {
    return Detail.of(
        "In Hand",
        "The item in the hand of the player at the time of this event.",
        alert -> Sponge.getServer().getPlayer(alert.getGrieferUuid()).flatMap(griefer ->
            griefer.getItemInHand(HandTypes.MAIN_HAND)
                .filter(stack -> !stack.getType().equals(ItemTypes.AIR))
                .map(stack -> Format.hover(stack.getTranslation().get(), stack.getType().getId()))));
  }


}
