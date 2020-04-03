/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public final class DateUtil {

  private DateUtil() {
  }

  /**
   * Get the duration of time since now based on the input duration string.
   * The input string must follow the regular expression: "([0-9]*[smhdwMy])*".
   * s, m, h, d, w, M, and y represents seconds, minutes, hours, days, weeks,
   * months, and years respectively. "10d5h" -> 10 days and 5 hours.
   *
   * @param duration the string input of the duration
   * @return the date represented by the input duration since now
   * @throws IllegalArgumentException if the duration input is not formatted correctly
   */
  public static Date parseTimeAgo(String duration) throws IllegalArgumentException {

    String regex = "([0-9]*[smhdwMy])*";
    String syntaxError = String.format(
        "The given duration %s does not follow the format %s",
        Grammar.truncate(duration, 10),
        regex);

    if (!duration.matches(regex)) {
      throw new IllegalArgumentException(syntaxError);
    }

    LocalDateTime dateTime = LocalDateTime.now();
    for (String item : duration.split("(?<=[smhdwMy])")) {
      if (item.isEmpty()) {
        throw new IllegalArgumentException(syntaxError);
      }

      int durationValue;
      try {
        durationValue = Integer.parseUnsignedInt(item.substring(0, item.length() - 1));
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException(String.format(
            "The value %s cannot be parsed into an integer",
            item));
      }

      String tooHighError = String.format("That value %s is too high!", durationValue);
      switch (item.charAt(item.length() - 1)) {
        case 's':
          if (durationValue > 10000) {
            throw new IllegalArgumentException(tooHighError);
          }
          dateTime = dateTime.minus(Duration.ofSeconds(durationValue));
          break;
        case 'm':
          if (durationValue > 10000) {
            throw new IllegalArgumentException(tooHighError);
          }
          dateTime = dateTime.minus(Duration.ofMinutes(durationValue));
          break;
        case 'h':
          if (durationValue > 10000) {
            throw new IllegalArgumentException(tooHighError);
          }
          dateTime = dateTime.minus(Duration.ofHours(durationValue));
          break;
        case 'd':
          if (durationValue > 10000) {
            throw new IllegalArgumentException(tooHighError);
          }
          dateTime = dateTime.minus(Duration.ofDays(durationValue));
          break;
        case 'w':
          if (durationValue > 4800) {
            throw new IllegalArgumentException(tooHighError);
          }
          dateTime = dateTime.minus(Period.ofWeeks(durationValue));
          break;
        case 'M':
          if (durationValue > 1200) {
            throw new IllegalArgumentException(tooHighError);
          }
          dateTime = dateTime.minus(Period.ofMonths(durationValue));
          break;
        case 'y':
          if (durationValue > 100) {
            throw new IllegalArgumentException(tooHighError);
          }
          dateTime = dateTime.minus(Period.ofYears(durationValue));
          break;
        default:
          throw new IllegalArgumentException(syntaxError);
      }
    }
    return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

  }

  /**
   * Get the date from an input string of format "dd-MM-yyyy".
   *
   * @param date The corresponding date
   * @return The {@link Date} object
   * @throws IllegalArgumentException If the
   */
  public static Date parseDate(String date) throws IllegalArgumentException {
    String format = "yyyy-MM-dd";
    DateFormat dateFormat = new SimpleDateFormat(format);
    try {
      return dateFormat.parse(date);
    } catch (ParseException e) {
      throw new IllegalArgumentException(String.format(
          "Input %s does not follow format %s",
          Grammar.truncate(date, 10),
          format));
    }
  }

  /**
   * For GriefAlert, attempt to parse any string into a date format.
   *
   * @param anyFormat The string to parse
   * @return The formatted {@link Date}
   * @throws IllegalArgumentException if the string cannot be parsed
   */
  public static Date parseAnyDate(String anyFormat) throws IllegalArgumentException {
    Date date;
    try {
      date = parseDate(anyFormat);
    } catch (IllegalArgumentException e1) {
      try {
        date = parseTimeAgo(anyFormat);
      } catch (IllegalArgumentException e2) {
        throw new IllegalArgumentException(e1.getMessage() + ". " + e2.getMessage());
      }
    }
    if (date.before(Date.from(Instant.ofEpochSecond(1262307600)))) {
      throw new IllegalArgumentException("This date is before the server started!");
    }
    return date;
  }

}
