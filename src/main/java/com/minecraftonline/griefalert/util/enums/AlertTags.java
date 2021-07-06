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

package com.minecraftonline.griefalert.util.enums;

import com.minecraftonline.griefalert.util.Format;
import org.spongepowered.api.text.Text;

/**
 * A utility class to hold static methods which generate various "tags",
 * which are helpful clickable {@link Text} objects to make running
 * commands easier.
 */
public final class AlertTags {

  private AlertTags() {
  }

  /**
   * Get a tag allowing the user to immediately use the info command.
   *
   * @param index The alert index
   * @return the formatted text
   */
  public static Text getTagInfo(int index) {
    return Format.command(
        "INFO",
        String.format("/griefalert info %s", index),
        Text.of("Get a summary of information about this alert")
    );
  }

  /**
   * Get a tag allowing the user to immediately use the check command.
   *
   * @param index The alert index
   * @return the formatted text
   */
  public static Text getTagCheck(int index) {
    return Format.command(
        "CHECK",
        String.format("/griefalert check %s", index),
        Text.of("Teleport to this location")
    );
  }

  /**
   * Get a tag allowing the user to immediately use the rollback command.
   *
   * @param index The alert index
   * @return the formatted text
   */
  public static Text getTagFix(int index) {
    return Format.command(
        "FIX",
        String.format("/griefalert fix %s", index),
        Text.of("Undo this event")
    );
  }

  /**
   * Get a tag allowing the user to immediately use the return command.
   *
   * @return the formatted text
   */
  public static Text getTagReturn() {
    return Format.command(
        "RETURN",
        "/griefalert return",
        Text.of("Return to last saved location before checking an alert")
    );
  }

  /**
   * Get a tag allowing the user to immediately use a simple query command.
   *
   * @param playerName The name of the player to query
   * @return the formatted text
   */
  public static Text getTagRecent(String playerName) {
    return Format.command(
        "RECENT",
        String.format("/griefalert query -p %s", playerName),
        Text.of("Search for recent events caused by this player")
    );
  }

  /**
   * Get a tag allowing the user to immediately use the show command.
   *
   * @param index The alert index
   * @return the formatted text
   */
  public static Text getTagShow(int index) {
    return Format.command(
        "SHOW",
        String.format("/griefalert show %s", index),
        Text.of("Show the alert location in the world")
    );
  }


}
