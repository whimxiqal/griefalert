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
