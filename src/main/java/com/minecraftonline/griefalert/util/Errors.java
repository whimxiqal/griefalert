package com.minecraftonline.griefalert.util;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.channel.ChatTypeMessageReceiver;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.world.World;


public final class Errors {

  private Errors() {
  }

  public static void sendCannotTeleportSafely(ChatTypeMessageReceiver receiver, Transform<World> transform) {
    receiver.sendMessage(
        ChatTypes.CHAT,
        Format.error(
            String.format(
                "You could not be teleported safely to this location: %s, %s, %s; %s",
                transform.getPosition().getFloorX(),
                transform.getPosition().getFloorY(),
                transform.getPosition().getFloorZ(),
                transform.getExtent().getDimension().getType().getName())));
  }

}
