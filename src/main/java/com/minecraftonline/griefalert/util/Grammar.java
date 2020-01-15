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
   * Add a correct indefinite article ("a" or "an") to a string with the purpose
   * of introducing a noun correctly.
   *
   * @param s the phrase beginning with a noun
   * @return a grammatically correct phrase
   */
  public static String addIndefiniteArticle(String s) {
    if (s.isEmpty()) {
      return s;
    }
    s = s.trim();
    if ("aeiou".contains(String.valueOf(s.charAt(0)).toLowerCase())) {
      return "an " + s;
    } else {
      return "a " + s;
    }
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
}
