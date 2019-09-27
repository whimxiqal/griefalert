package com.minecraftonline.griefalert.tools;


import java.net.MalformedURLException;
import java.net.URL;

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class ClickableMessage {

  private Text text;

  private static final TextColor DEFAULT_HOVER_MESSAGE_COLOR = TextColors.LIGHT_PURPLE;

  private ClickableMessage(Text text) {
    this.text = text;
  }

  public Text toText() {
    return text;
  }

  public static Builder builder(Text messageBody) {
    return new Builder(messageBody);
  }

  public static Builder builder(String messageBodyRaw) {
    return builder(Text.of(TextColors.YELLOW, messageBodyRaw));
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private final Text.Builder builder;

    private Builder(Text messageBody) {
      builder = Text.builder().append(messageBody).append(Text.of(" "));
    }

    private Builder() {
      builder = Text.builder();
    }

    /**
     * To the end of the message under construction, adds a piece of text which can automatically
     * run a command with the clicker as the source once clicked.
     *
     * @param name         The text to display in the message
     * @param command      The command to run once clicked
     * @param hoverMessage The text to display once a cursor hovers over the clickable text
     * @return The builder, which can be used to continue building
     */
    public Builder addClickableCommand(String name, String command, Text hoverMessage) {
      Text clickable = Text.builder()
          .append(Text.of(TextColors.GOLD, TextStyles.ITALIC, "[",
              Text.of(TextColors.GRAY, name), "] "))
          .onClick(TextActions.runCommand(command))
          .onHover(TextActions.showText(hoverMessage))
          .build();
      builder.append(clickable);
      return this;
    }

    private Builder addClickableCommand(String name, String command, String hoverMessage) {
      return addClickableCommand(name, command, Text.builder()
          .append(Text.of(DEFAULT_HOVER_MESSAGE_COLOR, hoverMessage + "\n\n"))
          .append(Text.of(TextColors.GRAY, command))
          .build());
    }

    Builder addClickableCommand(ClickableCommand clickable) {
      return addClickableCommand(clickable.getName(),
          clickable.getCommand(),
          clickable.getHoverMessage());
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
      Text clickable = Text.builder()
          .append(Text.of(TextColors.GOLD, TextStyles.ITALIC, " [",
              Text.of(TextColors.GRAY, name), "]"))
          .onClick(TextActions.openUrl(new URL(url)))
          .onHover(TextActions.showText(Text.of(TextColors.LIGHT_PURPLE, url)))
          .build();
      builder.append(clickable);
      return this;
    }

    public ClickableMessage build() {
      return new ClickableMessage(builder.build());
    }

  }

  public static class ClickableCommand {

    private final String name;
    private final GriefAlert.Permission permission;
    private final String command;
    private final String hoverMessage;

    /**
     * Create an encapsulation for all objects necessary to create a clickable object.
     *
     * @param name         The display name of this clickable item
     * @param permission   The permission level associated with the command
     * @param command      The string version of the command
     * @param hoverMessage The message which displays above the display name once
     *                     the cursor hovers over it
     */
    public ClickableCommand(String name, GriefAlert.Permission permission,
                            String command, String hoverMessage) {
      this.name = name;
      this.permission = permission;
      this.command = command;
      this.hoverMessage = hoverMessage;
    }

    public String getName() {
      return name;
    }

    GriefAlert.Permission getPermission() {
      return permission;
    }

    public String getCommand() {
      return command;
    }

    String getHoverMessage() {
      return hoverMessage;
    }
  }

}
