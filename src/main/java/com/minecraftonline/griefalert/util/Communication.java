/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import com.minecraftonline.griefalert.GriefAlert;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;

public final class Communication {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private Communication() {
  }

  /**
   * Get the Message Channel which connects to all members with the
   * GriefAlert Messaging permission.
   *
   * @return The corresponding MessageChannel
   */
  public static MessageChannel getStaffBroadcastChannel() {
    return getStaffBroadcastChannelWithout(/* no one omitted */);
  }

  /**
   * Get the Message Channel which connects to all members with the
   * GriefAlert Messaging permission but omits specific individuals.
   *
   * @param omittedReceiver all omitted receivers
   * @return a corresponding <code>MessageChannel</code>
   */
  public static MessageChannel getStaffBroadcastChannelWithout(MessageReceiver... omittedReceiver) {
    return () -> {
      List<MessageReceiver> staff = new LinkedList<>();
      for (Player player : Sponge.getServer().getOnlinePlayers()) {
        if (player.hasPermission(Permissions.GRIEFALERT_MESSAGING.toString())
            && !Arrays.stream(omittedReceiver)
            .collect(Collectors.toList())
            .contains(player)) {
          staff.add(player);
        }
      }
      if (Settings.SHOW_ALERTS_IN_CONSOLE.getValue()) {
        staff.add(Sponge.getServer().getConsole());
      }
      return staff;
    };
  }

}
