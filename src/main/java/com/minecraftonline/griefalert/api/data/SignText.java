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

import java.util.Optional;
import javax.annotation.Nullable;

/**
 * Holds the text within an instance of a sign.
 *
 * @author PietElite
 */
public class SignText {

  private final String text1;
  private final String text2;
  private final String text3;
  private final String text4;

  private SignText(
      @Nullable String text1,
      @Nullable String text2,
      @Nullable String text3,
      @Nullable String text4) {
    this.text1 = text1;
    this.text2 = text2;
    this.text3 = text3;
    this.text4 = text4;
  }

  /**
   * Create a new wrapper for the four text lines on a sign.
   *
   * @param text1 Text for first line
   * @param text2 Text for second line
   * @param text3 Text for third line
   * @param text4 Text for fourth line
   * @return The corresponding <code>SignText</code>
   */
  public static SignText of(
      @Nullable String text1,
      @Nullable String text2,
      @Nullable String text3,
      @Nullable String text4) {
    return new SignText(text1, text2, text3, text4);
  }

  public Optional<String> getText1() {
    return Optional.ofNullable(text1).filter(o -> !o.isEmpty());
  }

  public Optional<String> getText2() {
    return Optional.ofNullable(text2).filter(o -> !o.isEmpty());
  }

  public Optional<String> getText3() {
    return Optional.ofNullable(text3).filter(o -> !o.isEmpty());
  }

  public Optional<String> getText4() {
    return Optional.ofNullable(text4).filter(o -> !o.isEmpty());
  }

}
