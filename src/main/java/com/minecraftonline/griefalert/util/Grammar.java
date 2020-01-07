/* Created by PietElite */

package com.minecraftonline.griefalert.util;

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
    s = s.trim();
    if ("aeiou".contains(String.valueOf(s.charAt(0)).toLowerCase())) {
      return "an " + s;
    } else {
      return "a " + s;
    }
  }
}
