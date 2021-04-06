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

package com.minecraftonline.griefalert.util.enums;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.api.configuration.Setting;

import java.text.SimpleDateFormat;

public final class Settings {

  public static final Setting<Integer> ALERTS_CODE_LIMIT = Setting.of(
      "alerts_code_limit",
      10000,
      "Number of alerts to cache",
      val -> (val >= 10) && (val <= 100000),
      "The alerts_code_limit configuration value is out of the acceptable range [10, 100000]. ",
      Integer.class);
  public static final Setting<Integer> ALERT_CHECK_TIMEOUT = Setting.of(
      "alert_check_timeout",
      5,
      "The number of seconds to wait before another staff member can check an alert after the first",
      val -> (val >= 0) && (val <= 60),
      "The alert_check_timeout configuration value is out of the acceptable range [0, 60]. ",
      Integer.class);
  public static final Setting<Integer> CHECK_INVULNERABILITY = Setting.of(
      "check_invulnerability",
      10,
      "The number of seconds of invulnerability staff get when they check alerts",
      val -> (val >= 0) && (val <= 60),
      "The check_invulnerability configuration value is out of the acceptable range [0, 60]. ",
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
  public static final Setting<Boolean> DIMENSIONED_ALERTS = Setting.of(
      "dimensioned_alerts",
      false,
      "Set to true to show dimension type on alerts instead of world names",
      b -> true,
      "",
      Boolean.class
  );
  public static final Setting<Integer> INSPECTION_RETURN_TIMEOUT = Setting.of(
      "alert_inspect_timeout",
      30,
      "The number of minutes before an alert inspection may not be returned from. Set too -1 to disable",
      val -> (val >= -1) && (val <= 1440),
      "The alert_inspect_timeout configuration value is out of the acceptable range [-1, 1440]. ",
      Integer.class);
  public static final Setting<Integer> MAX_HIDDEN_REPEATED_EVENTS = Setting.of(
      "max_hidden_repeated_events",
      10,
      "Maximum number of alerts with subsequent identical alerts to hide all but the first",
      val -> (val >= 1) && (val <= 1000),
      "The max_hidden_repeated_events configuration value is out of the "
          + "acceptable range {1, 1000}. ",
      Integer.class);
  public static final Setting<Integer> MAX_HIDDEN_REPEATED_EVENTS_TIMEOUT = Setting.of(
      "max_hidden_repeated_events_timeout",
      30,
      "Maximum number of seconds within which a subsequent identical alert could be silenced. Set to -1 to disable",
      val -> (val >= -1) && (val <= 3600),
      "The max_hidden_repeated_events configuration value is out of the "
          + "acceptable range {-1, 3600}. ",
      Integer.class);
  public static final Setting<Boolean> SHOW_ALERTS_IN_CONSOLE = Setting.of(
      "show_alerts_in_console",
      true,
      "True if the alerts should be displayed in main console window",
      bool -> true,
      "",
      Boolean.class);
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
  public static final Setting<String> STORAGE_ENGINE = Setting.of(
      "storage_engine",
      "sqlite",
      "The storage engine in which the grief profiles are stored.\n"
          + "  (MySQL, SQLite)",
      s -> Lists.newArrayList("mysql", "sqlite").contains(s.toLowerCase()),
      "The storage_engine configuration value is not one of the options {MySQL, SQLite}. ",
      String.class);
  public static final Setting<String> STORAGE_PASSWORD = Setting.of(
      "storage_password",
      "",
      "The storage password to be used if storage type requires it",
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
  /**
   * Private constructor to prevent instantiation.
   */
  private Settings() {
  }

}
