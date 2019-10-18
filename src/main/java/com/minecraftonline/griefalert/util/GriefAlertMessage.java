package com.minecraftonline.griefalert.util;

import java.net.MalformedURLException;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class GriefAlertMessage {

  private Text text;

  private static final TextColor DEFAULT_HOVER_MESSAGE_COLOR = TextColors.LIGHT_PURPLE;

  public static Builder builder(Text messageBody) {
    return new Builder(messageBody);
  }

  public static Builder builder() {
    return new Builder();
  }

  private GriefAlertMessage(Text text) {
    this.text = text;
  }

  public Text toText() {
    return text;
  }

  public static class Builder {

    private final Text.Builder builder;

    private Builder(Text messageBody) {
      builder = Text.builder().append(messageBody).append(Text.of(" "));
    }

    private Builder(Text.Builder messageBodyBuilder) {
      builder = messageBodyBuilder;
    }

    private Builder() {
      builder = Text.builder();
    }

    public Builder append(Text text) {
      builder.append(text);
      return this;
    }

    /**
     * To the end of the message under construction, adds a piece of text which can automatically
     * run a command with the clicker as the source once clicked.
     *
     * @param label         The text to display in the message
     * @param command      The command to run once clicked
     * @param hoverMessage The text to display once a cursor hovers over the clickable text
     * @return The builder, which can be used to continue building
     */
    public Builder addClickableCommand(String label, String command, Text hoverMessage) {
      builder.append(Format.command(label, command, hoverMessage));
      return this;
    }

    private Builder addClickableCommand(String name, String command, String hoverMessage) {
      return addClickableCommand(name, command, Text.builder()
          .append(Text.of(DEFAULT_HOVER_MESSAGE_COLOR, hoverMessage + "\n\n"))
          .append(Text.of(TextColors.GRAY, command))
          .build());
    }

    /**
     * To the end of the message under construction, adds a piece of text which can automatically
     * send the user to a designated URL once clicked.
     *
     * @param name The text to display in the message
     * @param url  The url to which the user will be sent
     * @return The builder, which can be used to continue building a ClickableMessage
     */
    public Builder addClickableUrl(String name, String url) throws MalformedURLException {
      builder.append(Format.url(name, url));
      return this;
    }

    public GriefAlertMessage build() {
      return new GriefAlertMessage(builder.build());
    }

  }

}
