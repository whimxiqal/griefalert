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

package com.minecraftonline.griefalert.sponge.alert.commands;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.sponge.alert.commands.common.LegacyCommand;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Permissions;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Helper final class to provide commands which are not used anymore.
 */
public final class LegacyCommands {

  /**
   * Immutable list of deprecated commands.
   */
  private static final List<LegacyCommand> list = Lists.newArrayList(
      LegacyCommand.of(Permissions.GRIEFALERT_COMMAND_CHECK, "gcheck", "griefalert check"),
      LegacyCommand.of(Permissions.GRIEFALERT_COMMAND_QUERY, "grecent", "griefalert query")
  );

  /**
   * Private constructor so this class cannot be instantiated.
   */
  private LegacyCommands() {
  }

  @Nonnull
  public static List<LegacyCommand> get() {
    return list;
  }

}
