package com.minecraftonline.griefalert.util.enums;

import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.SpongeEvents;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.util.blockray.BlockRay;

import java.util.Optional;

public final class Details {

  private Details() {
  }


  public static final Detail PLAYER = Detail.of(
      "Player",
      "The name of the player who caused this event.",
      alert -> Optional.of(Format.userName(alert.getGriefer())));

  public static final Detail EVENT = Detail.of(
      "Event",
      "THe classification for this type of event.",
      alert -> Optional.of(Format.bonus(alert.getGriefEvent().getName())));

  public static final Detail TARGET = Detail.of(
      "Target",
      "The targeted object.",
      alert -> Optional.of(Format.bonus(Format.item(alert.getTarget()))));

  public static final Detail LOCATION = Detail.of(
      "Location",
      "The location of the event. This could either be the players location "
          + "or the location of the targeted object, depending on the event type.",
      alert -> Optional.of(Format.bonusLocation(alert.getGriefLocation())));

  public static final Detail TIME = Detail.of(
      "Time",
      "The time of the event.",
      alert -> Optional.of(Format.date(alert.getCreated())));

  public static final Detail BLOCK_CREATOR = Detail.of(
      "Block Creator",
      "The player who placed the block at this location before the break event. "
          + "Based on Sponge's default 'block creator' feature.",
      alert -> alert.getGriefLocation()
              .getExtent()
              .getCreator(alert.getGriefLocation().getBlockPosition())
              .flatMap(uuid -> Sponge.getServiceManager()
                  .provide(UserStorageService.class)
                  .flatMap(users -> users.get(uuid)))
              .map(Format::userName));

  public static final Detail LOOKING_AT = Detail.of(
      "Looking At",
      "The object in immediate view during usage of the target.",
      alert -> {
        if (alert.getGriefer() instanceof Player) {
          return BlockRay.from((Player) alert.getGriefer())
              .stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(),1))
              .build()
              .end()
              .map(hit -> Format.item(hit.getLocation().getBlockType().getId()));
        } else {
          return Optional.empty();
        }
      });

  public static final Detail IN_HAND = Detail.of(
      "In Hand",
      "The item in the hand of the player at the time of this event.",
      alert -> alert.getGriefer()
          .getItemInHand(HandTypes.MAIN_HAND)
          .map(itemStack -> Format.item(itemStack.getType().getId())));

}