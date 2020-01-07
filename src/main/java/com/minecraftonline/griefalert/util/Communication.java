/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import com.minecraftonline.griefalert.GriefAlert;

import java.util.LinkedList;
import java.util.List;

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
   * GriefAlert_Messaging permission.
   *
   * @return The corresponding MessageChannel
   */
  public static MessageChannel getStaffBroadcastChannel() {
    return () -> {
      List<MessageReceiver> staff = new LinkedList<>();
      for (Player player : Sponge.getServer().getOnlinePlayers()) {
        if (player.hasPermission(Permissions.GRIEFALERT_MESSAGING.toString())) {
          staff.add(player);
        }
      }
      if (GriefAlert.getInstance().getConfigHelper().isAlertEventsToConsole()) {
        staff.add(Sponge.getServer().getConsole());
      }
      return staff;
    };
  }

}
