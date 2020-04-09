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

import org.spongepowered.api.text.Text;

public final class Grammar {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private Grammar() {
  }

  /**
   * Add a correct indefinite article ("a" or "an") to a <code>Text</code> with the
   * purpose of introducing a noun correctly.
   *
   * @param text the input text to begin with a noun
   * @return a grammatically correct phrase
   */
  public static Text addIndefiniteArticle(Text text) {
    if (text.isEmpty()) {
      return text;
    }
    text = text.trim();
    if ("aeiou".contains(String.valueOf(text.toPlain().charAt(0)).toLowerCase())) {
      return Text.of("an ", text);
    } else {
      return Text.of("a ", text);
    }

  }

  /**
   * Format a string such that it only has at most a certain number of
   * characters. Changes the last three characters at the end to dots
   * ('...') if truncated.
   *
   * @param str  The input string. Must be at least 4 in length.
   * @param size The maximum size of the truncated string. Must be at least 4.
   * @return The truncated string
   */
  public static String truncate(String str, int size) {
    if (str.length() <= 3) {
      return str;
    }
    if (size <= 3) {
      throw new IllegalArgumentException("Truncation size cannot be less than or equal to 3");
    }

    if (str.length() > size) {
      return str.substring(0, size - 3) + "...";
    } else {
      return str;
    }
  }
}
