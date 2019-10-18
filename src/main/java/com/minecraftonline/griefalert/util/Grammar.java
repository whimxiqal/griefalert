package com.minecraftonline.griefalert.util;

public class Grammar {

  /**
   * Converts all indefinite articles (<b>an, a</b>) to the appropriate version
   * in a string by reading whether the next word begins with a vowel.
   *
   * @param string The string to read and correct
   * @return The corrected string
   */
  public static String correctIndefiniteArticles(String string) {
    String[] tokens = string.replaceAll(" an ", " a ").split(" a ");
    String output = tokens[0];
    for (int i = 1; i < tokens.length; i++) {
      if ("aeiou".contains(String.valueOf(tokens[i].charAt(0)).toLowerCase())) {
        output = String.join(" an ", output, tokens[i]);
      } else {
        output = String.join(" a ", output, tokens[i]);
      }
    }
    return output;
  }

}
