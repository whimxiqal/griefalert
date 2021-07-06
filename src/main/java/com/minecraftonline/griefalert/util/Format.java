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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.helion3.prism.api.data.PrismEvent;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.data.GriefEvents;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.services.Request;
import com.minecraftonline.griefalert.commands.CheckCommand;
import com.minecraftonline.griefalert.util.enums.Settings;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A utility class to format messages for the purpose of sending users
 * neat and useful information.
 */
public final class Format {

  public static final TextColor GRIEF_ALERT_THEME = TextColors.DARK_PURPLE;
  public static final TextColor ALERT_EVENT_COLOR = TextColors.RED;
  public static final TextColor ALERT_TARGET_COLOR = TextColors.RED;
  public static final TextColor ALERT_WORLD_COLOR = TextColors.RED;
  public static final TextColor CLICK_COMMAND_COLOR = TextColors.LIGHT_PURPLE;

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private Format() {
  }

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
  public static Text error(@NotNull Text content) {
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
  public static Text heading(@NotNull Text content) {
    return Text.of(prefix(), TextColors.GOLD, content);
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
  public static Text message(@NotNull Text content) {
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
  public static Text subduedHeading(@NotNull Text content) {
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
  public static Text success(@NotNull Text content) {
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
  public static Text info(@NotNull Text content) {
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
  public static Text bonus(@NotNull Text content) {
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
  @Nonnull
  public static Text url(@Nonnull String label, @Nonnull String url) {
    Text.Builder textBuilder = Text.builder();
    textBuilder.append(Text.of(TextColors.BLUE, label));
    textBuilder.onHover(TextActions.showText(Text.of(url)));
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
   * @param command      the command which is run. Format "/command args..."
   * @param hoverMessage the message to display when hovering over clickable item
   * @return the command <code>Text</code>
   */
  @Nonnull
  public static Text command(@Nonnull String label,
                             @Nonnull String command,
                             @Nullable Text hoverMessage) {
    Text.Builder builder = Text.builder()
        .append(Text.of(TextColors.GOLD, TextStyles.ITALIC, "[",
            Text.of(Format.CLICK_COMMAND_COLOR, label), "]"))
        .onClick(TextActions.runCommand(command));
    if (hoverMessage != null) {
      builder.onHover(TextActions.showText(Text.of(
          hoverMessage,
          hoverMessage.isEmpty() ? Text.EMPTY : Format.endLine(),
          Format.bonus(command))));
    }
    return builder.build();
  }

  /**
   * Returns content formatted with an Item name.
   * Optionally a hover action can be added to display
   * the full Item id.
   *
   * @param id the item id
   * @return Text Formatted content.
   */
  @Nonnull
  public static Text item(@Nonnull String id) {
    Text.Builder textBuilder = Text.builder();
    if (StringUtils.contains(id, ":")) {
      textBuilder.append(Text.of(StringUtils.substringAfter(id, ":").replaceAll("_", " ")));
    } else {
      textBuilder.append(Text.of(id.replaceAll("_", " ")));
    }

    textBuilder.onHover(TextActions.showText(Format.bonus(id)));

    return textBuilder.build();
  }

  @Nonnull
  public static Text date(@Nonnull Date date) {
    DateFormat dateFormat = new SimpleDateFormat(Settings.DATE_FORMAT.getValue());
    return Text.of(dateFormat.format(date));
  }

  /**
   * Format a readable location.
   *
   * @param location the location to format
   * @return the <code>Text</code> formatted content
   */
  @Nonnull
  public static Text bonusLocation(Location<World> location) {
    return Format.bonus(String.format(
        "(%s, %s, %s, %s)",
        location.getBlockX(),
        location.getBlockY(),
        location.getBlockZ(),
        location.getExtent().getDimension().getType().getName()));
  }

  public static Text endLine() {
    return Text.of("\n");
  }

  public static Text space() {
    return space(1);
  }

  public static Text space(int count) {
    return Text.of(Strings.repeat(" ", count));
  }

  /**
   * Format the grief checker's name to include prefix and suffix. The function is not implemented
   * correctly yet because it requires an API call to an API not yet developed (MCOUtils).
   *
   * @param user The grief checker
   * @return The Text form of the grief checker's name
   */
  public static Text userName(User user) {
    // TODO use a name formatting service
    return Text.of(user.getName());
    /*
    return Sponge.getServiceManager()
        .provide(NameService.class)
        .flatMap(service -> service.getNameData(user))
        .map(data -> Text.builder()
            .append(data.getShortName())
            .onHover(TextActions.showText(data.getFullName()))
            .build())
        .orElse(Text.of(user.getName()));
     */
  }

  /**
   * Format the label with text to show upon hovering with the cursor.
   *
   * @param label   the label to be printed
   * @param onHover the text to show upon hovering
   * @return the formatted text
   */
  public static Text hover(String label, String onHover) {
    return hover(Text.of(TextStyles.ITALIC, label), Text.of(onHover));
  }

  /**
   * Format the label with text to show upon hovering with the cursor.
   *
   * @param label   the label to be printed
   * @param onHover the text to show upon hovering
   * @return the formatted text
   */
  public static Text hover(Text label, Text onHover) {
    return Text.builder()
        .append(label)
        .onHover(TextActions.showText(onHover))
        .build();
  }

  /**
   * Build a formatted alert message from an alert.
   *
   * @param alert the alert
   * @param index the index of the alert in the larger alert structure
   * @return the formatted message
   */
  public static Text buildBroadcast(Alert alert, int index) {
    return Text.of(
        // TODO fix shrink function (and make shrink value configurable)
        Format.shrink(alert.getMessage(), Integer.MAX_VALUE /* temporarily disabled */),
        Format.space(),
        CheckCommand.clickToCheck(index));
  }

  /**
   * Shorten a formatted message input my removing unnecessary words with
   * shorter alternatives.
   *
   * @param input the original formatted message
   * @param size  the desired maximum size
   * @return the shrunken message
   */
  public static Text shrink(Text input, int size) {
    Text replacement = input;
    if (shrunkenLength(replacement) > size) {
      replacement = shrinkWord(replacement, "the");
    }
    if (shrunkenLength(replacement) > size) {
      replacement = shrinkWord(replacement, "in");
    }
    if (shrunkenLength(replacement) > size) {
      replacement = shrinkWord(replacement, "a");
      replacement = shrinkWord(replacement, "an");
    }
    return replacement;
  }

  private static Text shrinkWord(Text input, String word) {
    return input.replace(Pattern.compile("( |(\\.\\.\\.))*" + " " + word + " " + "( |(\\.\\.\\.))*"),
        Text.of(TextColors.GRAY, " ... "), true);
  }

  private static int shrunkenLength(Text input) {
    return input.toPlain().replace(".", "").length();
  }

  /**
   * Format a request into a message.
   *
   * @param request the request
   * @return the formatted message
   */
  public static Text request(Request request) {
    List<Text> tokens = Lists.newLinkedList();
    if (!request.getPlayerUuids().isEmpty()) {
      Text.Builder builder = Text.builder();
      builder.append(Text.of("Players: "));
      builder.append(Text.of("{"));
      builder.append(Text.joinWith(Text.of(","),
          request.getPlayerUuids()
              .stream()
              .map(uuid -> SpongeUtil.getUser(uuid).orElseThrow(() ->
                  new RuntimeException("Invalid UUID in Request")))
              .map(Format::userName)
              .collect(Collectors.toList())));
      builder.append(Text.of("}"));
      tokens.add(builder.build());
    }
    if (!request.getEvents().isEmpty()) {
      Text.Builder builder = Text.builder();
      builder.append(Text.of("Events: "));
      builder.append(Text.of("{"));
      builder.append(Text.joinWith(Text.of(","),
          request.getEvents()
              .stream()
              .map(GriefEvent::getId)
              .map(Text::of)
              .collect(Collectors.toList())));
      builder.append(Text.of("}"));
      tokens.add(builder.build());
    }
    if (!request.getTargets().isEmpty()) {
      Text.Builder builder = Text.builder();
      builder.append(Text.of("Targets: "));
      builder.append(Text.of("{"));
      builder.append(Text.joinWith(Text.of(","),
          request.getTargets()
              .stream()
              .map(Text::of)
              .collect(Collectors.toList())));
      builder.append(Text.of("}"));
      tokens.add(builder.build());
    }
    request.getMaximum().ifPresent(max -> tokens.add(Text.of("Maximum: ", max)));
    return Detail.of("Parameters", "The parameters used for this query",
        tokens.isEmpty()
            ? Text.of("None")
            : Text.joinWith(Text.of(", "), tokens)).get(request).get();
  }

  /**
   * Format a prism request into a message.
   *
   * @param request the request
   * @return the formatted message
   */
  public static Text request(com.helion3.prism.api.services.Request request) {
    List<Text> tokens = Lists.newLinkedList();
    if (!request.getPlayerUuids().isEmpty()) {
      Text.Builder builder = Text.builder();
      builder.append(Text.of("Players: "));
      builder.append(Text.of("{"));
      builder.append(Text.joinWith(Text.of(","),
          request.getPlayerUuids()
              .stream()
              .map(uuid -> SpongeUtil.getUser(uuid).orElseThrow(() ->
                  new RuntimeException("Invalid UUID in Request")))
              .map(Format::userName)
              .collect(Collectors.toList())));
      builder.append(Text.of("}"));
      tokens.add(builder.build());
    }
    if (!request.getEvents().isEmpty()) {
      Text.Builder builder = Text.builder();
      builder.append(Text.of("Events: "));
      builder.append(Text.of("{"));
      builder.append(Text.joinWith(Text.of(","),
          request.getEvents()
              .stream()
              .map(PrismEvent::getId)
              .map(Text::of)
              .collect(Collectors.toList())));
      builder.append(Text.of("}"));
      tokens.add(builder.build());
    }
    if (!request.getTargets().isEmpty()) {
      Text.Builder builder = Text.builder();
      builder.append(Text.of("Targets: "));
      builder.append(Text.of("{"));
      builder.append(Text.joinWith(Text.of(","),
          request.getTargets()
              .stream()
              .map(Text::of)
              .collect(Collectors.toList())));
      builder.append(Text.of("}"));
      tokens.add(builder.build());
    }
    if (!request.getWorldUuids().isEmpty()) {
      Text.Builder builder = Text.builder();
      builder.append(Text.of("Worlds: "));
      builder.append(Text.of("{"));
      builder.append(Text.joinWith(Text.of(","),
          request.getWorldUuids()
              .stream()
              .map(uuid -> SpongeUtil.getWorld(uuid).orElseThrow(() ->
                  new RuntimeException("Invalid UUID in Request")))
              .map(world -> Text.of(world.getName()))
              .collect(Collectors.toList())));
      builder.append(Text.of("}"));
      tokens.add(builder.build());
    }
    request.getEarliest().ifPresent(date -> tokens.add(Text.of("Earliest: ", Format.date(date))));
    request.getLatest().ifPresent(date -> tokens.add(Text.of("Latest: ", Format.date(date))));
    return Detail.of("Parameters", "The parameters used for this query",
        tokens.isEmpty()
            ? Text.of("None")
            : Text.joinWith(Text.of(", "), tokens)).get(request).get();
  }

  /**
   * Format a dimension type into a message.
   *
   * @param type the dimension type
   * @return the formatted message
   */
  public static Text dimension(@Nonnull DimensionType type) {
    if (type.equals(DimensionTypes.OVERWORLD)) {
      return Text.of("overworld");
    } else if (type.equals(DimensionTypes.NETHER)) {
      return Text.of("nether");
    } else if (type.equals(DimensionTypes.THE_END)) {
      return Text.of("end");
    } else {
      throw new IllegalArgumentException("Invalid dimension type");
    }
  }

  /**
   * Format a GriefEvent into readable text.
   *
   * @param event the event
   * @return the readable text
   */
  public static Text action(@Nonnull GriefEvent event) {
    return action(event, event.getPreterit());
  }

  /**
   * Format a GriefEvent into readable text with a specific label string.
   *
   * @param event the event
   * @param label the message label
   * @return the readable text
   */
  public static Text action(@Nonnull GriefEvent event, String label) {
    return Text.builder(label)
        .onHover(TextActions.showText(Text.of(
            Format.heading("Event"),
            Format.endLine(),
            Text.joinWith(Format.endLine(),
                Detail.of(
                    "Name",
                    "",
                    Text.of(event.getName())).get(event).get(),
                Detail.of(
                    "ID",
                    "",
                    Text.of(event.getId())).get(event).get(),
                Detail.of(
                    "Description",
                    "",
                    Text.of(event.getDescription())).get(event).get()))))
        .build();
  }

  /**
   * Format a grief profile into a message.
   *
   * @param griefProfile the grief profile
   * @return the formatted message
   */
  public static Text profile(@Nonnull GriefProfile griefProfile) {
    List<Text> details = new LinkedList<>();
    Detail.of(
        "Event",
        "The event type for this profile; one of: "
            + GriefEvents.REGISTRY_MODULE.getAll()
            .stream().map(GriefEvent::getId)
            .collect(Collectors.joining(", ")),
        Format.hover(
            griefProfile.getGriefEvent().getId(),
            griefProfile.getGriefEvent().getDescription()))
        .get(griefProfile).ifPresent(details::add);
    Detail.of(
        "Target",
        "The ID for the target object of this grief event.",
        Format.item(griefProfile.getTarget()))
        .get(griefProfile).ifPresent(details::add);
    if (griefProfile.isTranslucent()) {
      Detail.of("Trnslt.", "The translucency of the profile, "
              + "which ignores events which have already been considered not grief",
          Text.of("yes")).get(griefProfile).ifPresent(details::add);
    }
    Optional.of(griefProfile.getIgnored()).filter(ignored -> !ignored.isEmpty())
        .flatMap(ignored -> Detail.of(
            "Ignored",
            "All dimension types in which events with this profile are ignored.",
            Format.bonus(Text.joinWith(
                Text.of(", "),
                ignored.stream()
                    .map(world -> Text.of(world.getName()))
                    .collect(Collectors.toList()))))
            .get(griefProfile)).ifPresent(details::add);
    Optional.of(griefProfile.getAllColored()).filter(colors -> !colors.isEmpty())
        .flatMap(colors -> Detail.of(
            "Colored",
            "Any components of the alert messages flagged by this alert "
                + "and their corresponding specified colors",
            Format.bonus(
                Text.joinWith(
                    Text.of(", "),
                    colors.entrySet()
                        .stream()
                        .map(entry -> Text.of(
                            "{",
                            entry.getKey().toString().toLowerCase(),
                            ", ",
                            entry.getValue().getName(),
                            "}"))
                        .collect(Collectors.toList()))))
            .get(griefProfile)).ifPresent(details::add);
    return Text.joinWith(Format.bonus(", "), details);
  }

}