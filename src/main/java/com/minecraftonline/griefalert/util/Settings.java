package com.minecraftonline.griefalert.util;

import com.minecraftonline.griefalert.api.configuration.Setting;

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
          Integer.class);

  public static final Setting<Integer> MAX_HIDDEN_REPEATED_EVENTS = Setting.of(
      "max_hidden_repeated_events",
      10,
      "Maximum number of repeated events to hide if they occurred in a row",
      Integer.class);

  public static final Setting<Boolean> SHOW_ALERTS_IN_CONSOLE = Setting.of(
      "show_alerts_in_console",
      true,
      "True if the alerts should be displayed in main console window",
      Boolean.class);

  public static final Setting<String> STORAGE_ENGINE = Setting.of(
      "storage_engine",
      "sqlite",
      "The storage engine in which the grief profiles are stored.\n"
          + "  (MySQL, SQLite)",
      String.class);

  public static final Setting<String> STORAGE_ADDRESS = Setting.of(
      "storage_address",
      "localhost",
      "The storage address to be used if storage type requires it",
      String.class);

  public static final Setting<String> STORAGE_DATABASE = Setting.of(
      "storage_database",
      "",
      "The storage database to be used if storage type requires it",
      String.class);

  public static final Setting<String> STORAGE_USERNAME = Setting.of(
      "storage_username",
      "",
      "The storage username to be used if storage type requires it",
      String.class);

  public static final Setting<String> STORAGE_PASSWORD = Setting.of(
      "storage_password",
      "",
      "The storage password to be used if storage type requires it",
      String.class);

}
