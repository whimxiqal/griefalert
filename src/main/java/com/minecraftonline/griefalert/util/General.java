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

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * A General tools class to house static methods for small data manipulations and methods.
 *
 * @author PietElite
 */
public final class General {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private General() {
  }

  /**
   * Send a series of colored messages to the console, signifying the initialization
   * of the plugin.
   */
  public static void stampConsole() {
    Sponge.getServer()
        .getConsole()
        .sendMessage(
            Text.of(TextColors.DARK_PURPLE, "     ____          "));
    Sponge.getServer()
        .getConsole()
        .sendMessage(
            Text.of(
                TextColors.DARK_PURPLE, "    /         /\\     ",
                TextColors.GOLD, "GriefAlert ",
                TextColors.DARK_GRAY, "v" + GriefAlert.VERSION, " by PietElite"));
    Sponge.getServer()
        .getConsole()
        .sendMessage(
            Text.of(
                TextColors.DARK_PURPLE, "   |    ===  /__\\    ",
                TextColors.DARK_AQUA, "Built for MinecraftOnline"));
    Sponge.getServer()
        .getConsole()
        .sendMessage(
            Text.of(
                TextColors.DARK_PURPLE, "    \\____/  /    \\    "));
    Sponge.getServer()
        .getConsole()
        .sendMessage(
            Text.of(
                TextColors.DARK_PURPLE, "                   "));
  }

  /**
   * Reformats a string so all spaces are replaced with underscores
   * and it has a 'minecraft:' tag if it didn't have a tag previously.
   *
   * @param unsure The input string
   * @return The updated string.
   */
  public static String ensureIdFormat(String unsure) {
    unsure = unsure.replaceAll(" ", "_");
    if (!unsure.contains(":")) {
      unsure = "minecraft:" + unsure;
    }
    return unsure;
  }

  /**
   * Print the stack trace of an exception only to the debug logger
   * to reduce clutter on the main console.
   *
   * @param e The exception to print
   */
  public static void printStackTraceToDebugLogger(Exception e) {
    GriefAlert.getInstance().getLogger().debug(e.getMessage());
    for (StackTraceElement element : e.getStackTrace()) {
      GriefAlert.getInstance().getLogger().debug(element.toString());
    }
  }

  public static String capitalize(String s) {
    return s.substring(0, 1).toUpperCase() + s.substring(1);
  }

}
