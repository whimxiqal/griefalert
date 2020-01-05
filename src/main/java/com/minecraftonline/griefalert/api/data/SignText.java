package com.minecraftonline.griefalert.api.data;

import java.util.Optional;
import javax.annotation.Nullable;

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
    return getText(text1);
  }

  public Optional<String> getText2() {
    return getText(text2);
  }

  public Optional<String> getText3() {
    return getText(text3);
  }

  public Optional<String> getText4() {
    return getText(text4);
  }

  private Optional<String> getText(String text) {
    if (text == null || text.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(text);
  }
}