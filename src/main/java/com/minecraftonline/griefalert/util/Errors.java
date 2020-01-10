/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.channel.ChatTypeMessageReceiver;
import org.spongepowered.api.text.channel.MessageReceiver;
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

  /**
   * Send a receiver an error message conveying the inability of performing a command
   * because only players may execute the command.
   *
   * @param receiver the receiver
   */
  public static void sendPlayerOnlyCommand(MessageReceiver receiver) {
    receiver.sendMessage(Format.error("Only players may execute this command"));
  }

}
