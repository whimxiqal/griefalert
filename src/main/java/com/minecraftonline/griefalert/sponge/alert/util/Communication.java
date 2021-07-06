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

package com.minecraftonline.griefalert.sponge.alert.util;

import com.minecraftonline.griefalert.sponge.alert.util.enums.Permissions;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Settings;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;

/**
 * A utility class to hold static methods relating to communication.
 */
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
