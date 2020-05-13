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

package com.minecraftonline.griefalert.api.templates;

import com.minecraftonline.griefalert.api.alerts.Alert;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColors;

public class Templates {

  private Templates() {
  }

  /**
   * A template used for constructing messages for {@link Alert}s.
   * This template uses the names of the enumerated elements of
   * {@link Arg}.
   * <li>
   *   {@link Arg#GRIEFER}
   *   {@link Arg#EVENT_COLOR}
   *   {@link Arg#EVENT}
   *   {@link Arg#TARGET_COLOR}
   *   {@link Arg#TARGET}
   *   {@link Arg#WORLD_COLOR}
   *   {@link Arg#WORLD}
   * </li>
   */
  public static final Template ALERT = new Template(
      TextTemplate.of(
          TextTemplate.arg(Arg.GRIEFER.name()),
          " ",
          TextTemplate.arg(Arg.EVENT_COLOR.name()),
          TextTemplate.arg(Arg.EVENT.name()),
          " ",
          TextTemplate.arg(Arg.TARGET_COLOR.name()),
          TextTemplate.arg(Arg.TARGET.name()),
          TextColors.RED, " in ",
          TextTemplate.arg(Arg.WORLD_COLOR.name()),
          TextTemplate.arg(Arg.WORLD.name()),
          " ",
          TextTemplate.arg(Arg.SUFFIX.name())),
      ".* [a-z ]* .* in the .*");

  /**
   * A template used for constructing messages for notifying
   * staff when an officer fixes an {@link Alert}.
   * This template uses the names of the enumerated elements of
   * {@link Arg}.
   * <ul>
   *   <li>{@link Arg#PREFIX}
   *   <li>{@link Arg#OFFICER}
   *   <li>{@link Arg#TARGET}
   *   <li>{@link Arg#GRIEFER}
   *   <li>{@link Arg#EVENT}
   *   <li>{@link Arg#X}
   *   <li>{@link Arg#Y}
   *   <li>{@link Arg#Z}
   *   <li>{@link Arg#WORLD}
   *   <li>{@link Arg#SUFFIX}
   * </ul>
   */
  public static final Template FIX = new Template(
      TextTemplate.of(
          TextTemplate.arg(Arg.PREFIX.name()),
          TextColors.YELLOW,
          TextTemplate.arg(Arg.OFFICER.name()),
          " fixed ",
          TextTemplate.arg(Arg.TARGET.name()),
          " that ",
          TextTemplate.arg(Arg.GRIEFER.name()),
          " ",
          TextTemplate.arg(Arg.EVENT.name()),
          " at ",
          TextTemplate.arg(Arg.X.name()),
          ", ",
          TextTemplate.arg(Arg.Y.name()),
          ", ",
          TextTemplate.arg(Arg.Z.name()),
          " in ",
          TextTemplate.arg(Arg.WORLD.name()),
          " ",
          TextTemplate.arg(Arg.SUFFIX.name())),
      ".* .* fixed .* that .* [a-z ]* at [0-9]*, [0-9]*, [0-9]* in the .*");

}
