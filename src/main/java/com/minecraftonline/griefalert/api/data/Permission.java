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

package com.minecraftonline.griefalert.api.data;

import javax.annotation.Nonnull;

/**
 * An object to describe a permission to track which commands and actions can
 * be completed by people with certain levels of privilege.
 *
 * @author PietElite
 */
public final class Permission {

  private final String perm;

  private Permission(@Nonnull final String perm) {
    this.perm = perm;
  }

  /**
   * Factory method of a {@link Permission}.
   *
   * @param perm the string representation
   * @return generated {@link Permission}
   */
  public static Permission of(@Nonnull final String perm) {
    return new Permission(perm);
  }

  @Nonnull
  public String get() {
    return perm;
  }

  @Override
  public String toString() {
    return perm;
  }

}