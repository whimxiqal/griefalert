/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import java.util.Optional;

public final class Optionals {

  private Optionals() {
  }

  public static boolean anyPresent(Optional... optionals) {
    for (Optional optional : optionals) {
      if (optional.isPresent()) {
        return true;
      }
    }
    return false;
  }

  public static boolean allPresent(Optional... optionals) {
    for (Optional optional : optionals) {
      if (!optional.isPresent()) {
        return false;
      }
    }
    return true;
  }

}
