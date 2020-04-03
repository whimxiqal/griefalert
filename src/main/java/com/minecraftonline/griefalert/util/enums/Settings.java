/* Created by PietElite */

package com.minecraftonline.griefalert.util.enums;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.api.configuration.Setting;

import java.text.SimpleDateFormat;

public final class Settings {

  /**
   * Private constructor to prevent instantiation.
   */
  private Settings() {
  }

  public static final Setting<Integer> ALERTS_CODE_LIMIT = Setting.of(
      "alerts_code_limit",
      10000,
      "Number of alerts to cache",
      val -> (val >= 10) && (val <= 100000),
      "The alerts_code_limit configuration value is out of the acceptable range {10, 100000}. ",
      Integer.class);

  public static final Setting<Integer> MAX_HIDDEN_REPEATED_EVENTS = Setting.of(
      "max_hidden_repeated_events",
      10,
      "Maximum number of repeated events to hide if they occurred in a row",
      val -> (val >= 1) && (val <= 1000),
      "The max_hidden_repeated_events configuration value is out of the "
          + "acceptable range {1, 1000}. ",
      Integer.class);

  public static final Setting<String> DATE_FORMAT = Setting.of(
      "date_format",
      "dd MMM yyyy HH:mm:ss z",
      "The date format used to display time records",
      s -> {
        try {
          new SimpleDateFormat(s);
          return true;
        } catch (IllegalArgumentException e) {
          return false;
        }
      },
      "The date_format configuration value can not be parsed by Java's SimpleDateFormat. ",
      String.class);

  public static final Setting<Boolean> SHOW_ALERTS_IN_CONSOLE = Setting.of(
      "show_alerts_in_console",
      true,
      "True if the alerts should be displayed in main console window",
      bool -> true,
      "",
      Boolean.class);

  public static final Setting<String> STORAGE_ENGINE = Setting.of(
      "storage_engine",
      "sqlite",
      "The storage engine in which the grief profiles are stored.\n"
          + "  (MySQL, SQLite)",
      s -> Lists.newArrayList("mysql", "sqlite").contains(s.toLowerCase()),
      "The storage_engine configuration value is not one of the options {MySQL, SQLite}. ",
      String.class);

  public static final Setting<String> STORAGE_ADDRESS = Setting.of(
      "storage_address",
      "localhost",
      "The storage address to be used if storage type requires it",
      s -> true,
      "",
      String.class);

  public static final Setting<String> STORAGE_DATABASE = Setting.of(
      "storage_database",
      "",
      "The storage database to be used if storage type requires it",
      s -> true,
      "",
      String.class);

  public static final Setting<String> STORAGE_USERNAME = Setting.of(
      "storage_username",
      "",
      "The storage username to be used if storage type requires it",
      s -> true,
      "",
      String.class);

  public static final Setting<String> STORAGE_PASSWORD = Setting.of(
      "storage_password",
      "",
      "The storage password to be used if storage type requires it",
      s -> true,
      "",
      String.class);

  public static final Setting<Integer> CHECK_INVULNERABILITY = Setting.of(
      "check_invulnerability",
      10,
      "The number of seconds of invulnerability staff get when they check alerts",
      val -> (val >= 0) && (val <= 60),
      "The check_invulnerability configuration value is out of the acceptable range {0, 60}. ",
      Integer.class);

}
