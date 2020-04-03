/* Created by PietElite */

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
