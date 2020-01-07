/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.minecraftonline.griefalert.GriefAlert;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public final class Format {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private Format() {
  }

  public static final TextColor GRIEF_ALERT_THEME = TextColors.DARK_PURPLE;
  public static final TextColor ALERT_EVENT_COLOR = TextColors.RED;
  public static final TextColor ALERT_TARGET_COLOR = TextColors.RED;
  public static final TextColor ALERT_DIMENSION_COLOR = TextColors.RED;

  /**
   * Returns content formatted as an error message.
   *
   * @param objects Object[] Content to format
   * @return Text Formatted content.
   */
  public static Text error(Object... objects) {
    return error(Text.of(objects));
  }

  /**
   * Returns content formatted as an error message.
   *
   * @param content Text Content to format
   * @return Text Formatted content.
   */
  public static Text error(Text content) {
    checkNotNull(content);
    return Text.of(prefix(), TextColors.RED, content);
  }

  /**
   * Returns content formatted as a "heading".
   *
   * @param objects Object[] Content to format
   * @return Text Formatted content.
   */
  public static Text heading(Object... objects) {
    return heading(Text.of(objects));
  }

  /**
   * Returns content formatted as a "heading".
   *
   * @param content Text Content to format
   * @return Text Formatted content.
   */
  @SuppressWarnings("WeakerAccess")
  public static Text heading(Text content) {
    checkNotNull(content);
    return Text.of(prefix(), TextColors.GOLD, TextStyles.BOLD, content);
  }

  /**
   * Returns content formatted as a standard message.
   *
   * @param objects Object[] Content to format
   * @return Text Formatted content.
   */
  @SuppressWarnings("unused")
  public static Text message(Object... objects) {
    return message(Text.of(objects));
  }

  /**
   * Returns content formatted as a standard message.
   *
   * @param content Text Content to format
   * @return Text Formatted content.
   */
  @SuppressWarnings("WeakerAccess")
  public static Text message(Text content) {
    checkNotNull(content);
    return Text.of(TextColors.WHITE, content);
  }

  /**
   * Returns content formatted as a "subdued heading".
   *
   * @param objects Object[] Content to format
   * @return Text Formatted content.
   */
  @SuppressWarnings("unused")
  public static Text subduedHeading(Object... objects) {
    return subduedHeading(Text.of(objects));
  }

  /**
   * Returns content formatted as a "subdued heading".
   *
   * @param content Text Content to format
   * @return Text Formatted content.
   */
  @SuppressWarnings("WeakerAccess")
  public static Text subduedHeading(Text content) {
    checkNotNull(content);
    return Text.of(prefix(), TextColors.GRAY, content);
  }

  /**
   * Returns content formatted as a success message.
   *
   * @param objects Object[] Content to format
   * @return Text Formatted content.
   */
  public static Text success(Object... objects) {
    return success(Text.of(objects));
  }

  /**
   * Returns content formatted as a success message.
   *
   * @param content Text Content to format
   * @return Text Formatted content.
   */
  public static Text success(Text content) {
    checkNotNull(content);
    return Text.of(prefix(), TextColors.GREEN, content);
  }

  /**
   * Returns content formatted as a success message.
   *
   * @param objects Object[] Content to format
   * @return Text Formatted content.
   */
  public static Text info(Object... objects) {
    return info(Text.of(objects));
  }

  /**
   * Returns content formatted as a success message.
   *
   * @param content Text Content to format
   * @return Text Formatted content.
   */
  public static Text info(Text content) {
    checkNotNull(content);
    return Text.of(prefix(), TextColors.YELLOW, content);
  }

  /**
   * Returns content formatted as a bonus message.
   *
   * @param objects Object[] Content to format
   * @return Text Formatted content.
   */
  public static Text bonus(Object... objects) {
    return bonus(Text.of(objects));
  }

  /**
   * Returns content formatted as a bonus string. Usually used
   * for fun wording inside other messages.
   *
   * @param content Text Content to format
   * @return Text Formatted content.
   */
  public static Text bonus(Text content) {
    checkNotNull(content);
    return Text.of(TextColors.GRAY, content);
  }

  /**
   * Returns content formatted with the Plugin name.
   *
   * @return Text Formatted content.
   */
  @SuppressWarnings("WeakerAccess")
  public static Text prefix() {
    return Text.of(GRIEF_ALERT_THEME, "|", Reference.NAME, "|", TextColors.RESET, " ");
  }

  /**
   * Returns content formatted with a URL.
   *
   * @param label the label of the URL
   * @param url   URL
   * @return Text Formatted content.
   */
  @SuppressWarnings("unused")
  public static Text url(String label, String url) {
    Text.Builder textBuilder = Text.builder();
    textBuilder.append(Text.of(TextColors.BLUE, label));

    try {
      textBuilder.onClick(TextActions.openUrl(new URL(url)));
    } catch (MalformedURLException ex) {
      textBuilder.onClick(TextActions.suggestCommand(url));
      GriefAlert.getInstance().getLogger().error("A url was not formed correctly for a"
          + " click action: " + url);
    }

    return textBuilder.build();
  }

  /**
   * Returns content formatted for a clickable command.
   *
   * @param label        the visible label to click
   * @param command      the command which is run. Format "/command arg arg arg"
   * @param hoverMessage the message to display when hovering over clickable item
   * @return the command <code>Text</code>
   */
  public static Text command(String label, String command, Text hoverMessage) {
    return Text.builder()
        .append(Text.of(TextColors.GOLD, TextStyles.ITALIC, "[",
            Text.of(TextColors.GRAY, label), "] "))
        .onClick(TextActions.runCommand(command))
        .onHover(TextActions.showText(hoverMessage))
        .build();
  }

  /**
   * Returns content formatted with an Item name.
   * Optionally a hover action can be added to display
   * the full Item id.
   *
   * @param id          Item Id
   * @param hoverAction Hover Action
   * @return Text Formatted content.
   */
  @SuppressWarnings("unused")
  public static Text item(String id, boolean hoverAction) {
    checkNotNull(id);

    Text.Builder textBuilder = Text.builder();
    if (StringUtils.contains(id, ":")) {
      textBuilder.append(Text.of(StringUtils.substringAfter(id, ":")));
    } else {
      textBuilder.append(Text.of(id));
    }

    if (hoverAction) {
      textBuilder.onHover(TextActions.showText(Text.of(id)));
    }

    return textBuilder.build();
  }

  /**
   * Format a readable location.
   *
   * @param location the location to format
   * @return the <code>Text</code> formatted content
   */
  public static Text location(Location<World> location) {
    return Text.of(String.format(
        "(x: %s, y: %s, z: %s, dimension: %s)",
        location.getBlockX(),
        location.getBlockY(),
        location.getBlockZ(),
        location.getExtent().getDimension().getType().getName()));

  }

  public static Text endLine() {
    return Text.of("\n");
  }

  public static Text space() {
    return Text.of(" ");
  }

  /**
   * Reconstruct a string to remove it's prefix "minecraft:" if it
   * has one. If not, then it does nothing.
   *
   * @param s the input string
   * @return the formatted string
   */
  @SuppressWarnings("WeakerAccess")
  public static String removeMinecraftPrefix(String s) {
    return s.replace("minecraft:", "");
  }

  /**
   * Format the grief checker's name to include prefix and suffix.
   *
   * @param player The grief checker
   * @return The Text form of the grief checker's name
   */
  public static Text playerName(Player player) {
    return player.getDisplayNameData().displayName().get();
  }
}