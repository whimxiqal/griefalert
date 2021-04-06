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

package com.minecraftonline.griefalert.util;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.ChatTypeMessageReceiver;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.world.Location;
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
    sendCannotTeleportSafely(receiver, transform.getLocation());
  }

  /**
   * Send a receiver an error message conveying the inability to set a Location
   * safely.
   *
   * @param receiver  the receiver of the message
   * @param location the inaccessible location
   */
  public static void sendCannotTeleportSafely(ChatTypeMessageReceiver receiver,
                                              Location<World> location) {
    receiver.sendMessage(
        ChatTypes.CHAT,
        Format.error(
            "You could not be teleported safely to this location: ",
            Format.bonusLocation(location)));
  }

  public static CommandException playerOnlyException() {
    return new CommandException(Format.error("Only players may execute this command"));
  }

  public static CommandException noPlayerFoundException(String username) {
    return new CommandException(Format.error("Cannot find player: " + username));
  }

  public static CommandException parseException() {
    return new CommandException(Format.error(
        "Your arguments couldn't be parsed. Try ",
        Format.command("/ga ?", "/ga ?", null)));
  }

  public static CommandException noAlertException() {
    return new CommandException(Format.error("No alert could be found with that index"));
  }

}
