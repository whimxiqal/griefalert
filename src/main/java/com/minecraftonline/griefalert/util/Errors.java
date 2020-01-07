/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.channel.ChatTypeMessageReceiver;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.world.World;


public final class Errors {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private Errors() {
  }

  /**
   * Send a receiver an error message conveying the inability to set a <code>Transform</code>
   * safely.
   *
   * @param receiver  the receiver of the message
   * @param transform the inaccessible transform
   */
  public static void sendCannotTeleportSafely(ChatTypeMessageReceiver receiver,
                                              Transform<World> transform) {
    receiver.sendMessage(
        ChatTypes.CHAT,
        Format.error(
            "You could not be teleported safely to this location: ", Format.location(
                transform.getLocation())));
  }

}
