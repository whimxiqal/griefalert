package com.minecraftonline.griefalert.griefevents.profiles;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class GriefProfileStorageLine {

  private static final String DATA_DELIMITER = " ";
  private static final String COMMENT_DELIMITER = "#";
  private String line;
  private String comment = "";

  private GriefProfileStorageLine(List<String> tokens) {
    this.line = String.join(DATA_DELIMITER, tokens);
  }

  GriefProfileStorageLine(String line) {
    this.line = line;
  }

  public static GriefProfileStorageLine.Builder builder() {
    return new GriefProfileStorageLine.Builder();
  }

  public String toString() {
    return line + (comment.isEmpty() ? "" : ("  " + COMMENT_DELIMITER + comment));
  }

  /**
   * Get all individual pieces of data saved in the storage line.
   *
   * @return An array of String-type objects from the storage line
   */
  public String[] getTokens() {
    String[] macroTokens = line.split(COMMENT_DELIMITER);
    String data = macroTokens[0];
    return data.split(DATA_DELIMITER);
  }

  /**
   * Gets the comment portion of the storage line.
   *
   * @return The comment of the line
   */
  @SuppressWarnings("unused")
  public String getComment() {
    String[] tokens = line.split(COMMENT_DELIMITER, 2);
    if (tokens.length == 2) {
      return tokens[1];
    } else {
      return "";
    }
  }

  boolean hasData() {
    for (String token : getTokens()) {
      if (!token.isEmpty()) {
        return true;
      }
    }
    return false;
  }

  public void setComment(@Nonnull String comment) {
    this.comment = comment;
  }

  public static class Builder {

    private List<String> items;

    Builder() {
      items = new LinkedList<>();
    }

    public GriefProfileStorageLine.Builder addItem(String item) {
      items.add(removeDelimiters(item));
      return this;
    }

    public GriefProfileStorageLine.Builder addItem(double item) {
      return addItem(String.valueOf(item));
    }

    public GriefProfileStorageLine.Builder addItem(boolean item) {
      return addItem(String.valueOf(item));
    }

    public GriefProfileStorageLine build() {
      return new GriefProfileStorageLine(items);
    }

    private String removeDelimiters(String line) {
      return line.replaceAll(DATA_DELIMITER, "").replaceAll(COMMENT_DELIMITER, "");
    }
  }
}
