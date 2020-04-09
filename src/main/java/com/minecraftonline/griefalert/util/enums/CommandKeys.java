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

import com.minecraftonline.griefalert.api.commands.CommandKey;
import org.spongepowered.api.text.Text;

public final class CommandKeys {

  private CommandKeys() {
  }

  public static final CommandKey ALERT_INDEX = CommandKey.of(
      Text.of("index"),
      Text.of("The index of an Alert in the list"));

  public static final CommandKey SINCE = CommandKey.of(
      Text.of("since"),
      Text.of("The earliest date in the query range"));

  public static final CommandKey BEFORE = CommandKey.of(
      Text.of("before"),
      Text.of("The latest date in the query range"));

  public static final CommandKey PLAYER = CommandKey.of(
      Text.of("player"),
      Text.of("The name of the player"));

  public static final CommandKey PRISM_TARGET = CommandKey.of(
      Text.of("target"),
      Text.of("The full target object id (entities without 'minecraft:')"));

  public static final CommandKey GA_TARGET = CommandKey.of(
      Text.of("target"),
      Text.of("The target object id, with or without 'minecraft:' prefix"));

  public static final CommandKey TARGET = CommandKey.of(
      Text.of("target"),
      Text.of("Any substring of a targeted object id"));

  public static final CommandKey PRISM_EVENT = CommandKey.of(
      Text.of("event"),
      Text.of("The id of a Prism event"));

  public static final CommandKey GA_EVENT = CommandKey.of(
      Text.of("event"),
      Text.of("The id of a GA event"));

  public static final CommandKey DIMENSION = CommandKey.of(
      Text.of("dimension"),
      Text.of("The full Sponge id of a Dimension Type"));

  public static final CommandKey PROFILE_COLOR_EVENT = CommandKey.of(
      Text.of("event_color"),
      Text.of("The color of the event in Alerts matching this profile"));

  public static final CommandKey PROFILE_COLOR_TARGET = CommandKey.of(
      Text.of("target_color"),
      Text.of("The color of the target in Alerts matching this profile"));

  public static final CommandKey PROFILE_COLOR_DIMENSION = CommandKey.of(
      Text.of("dimension_color"),
      Text.of("The color of the dimension in Alerts matching this profile"));

  public static final CommandKey MAXIMUM = CommandKey.of(
      Text.of("max"),
      Text.of("The maximum number of results to show"));

}
