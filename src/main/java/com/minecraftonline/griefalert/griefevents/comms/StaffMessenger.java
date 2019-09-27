package com.minecraftonline.griefalert.griefevents.comms;

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.util.LinkedList;
import java.util.List;

public class StaffMessenger {

  private static MessageChannel staffBroadcastChannel = () -> {
    List<MessageReceiver> staff = new LinkedList<>();
    for (Player player : Sponge.getServer().getOnlinePlayers()) {
      if (player.hasPermission(GriefAlert.Permission.GRIEFALERT_MESSAGING.toString())) {
        staff.add(player);
      }
    }
    staff.add(Sponge.getServer().getConsole());
    return staff;
  };

  public static MessageChannel getStaffBroadcastChannel() {
    return staffBroadcastChannel;
  }

}
