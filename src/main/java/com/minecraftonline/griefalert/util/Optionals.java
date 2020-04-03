/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import java.util.Optional;

public final class Optionals {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private Optionals() {
  }

  /**
   * Checks if any of the optionals are present in parameters.
   *
   * @param optionals an array of all optionals to check
   * @return true if any of the optionals are present
   */
  @SuppressWarnings("unused")
  public static boolean anyPresent(Optional<?>... optionals) {
    for (Optional<?> optional : optionals) {
      if (optional.isPresent()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if all of the optionals are present in parameters.
   *
   * @param optionals an array of all optionals to check
   * @return false if any of the optionals are absent
   */
  @SuppressWarnings("WeakerAccess")
  public static boolean allPresent(Optional<?>... optionals) {
    for (Optional<?> optional : optionals) {
      if (!optional.isPresent()) {
        return false;
      }
    }
    return true;
  }

}
