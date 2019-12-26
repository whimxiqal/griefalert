package com.minecraftonline.griefalert.util;

import java.util.LinkedList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;

public abstract class Comms {

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
      return staff;
    };
  }


}
