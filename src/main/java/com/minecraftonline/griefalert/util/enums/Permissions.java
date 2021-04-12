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

import com.minecraftonline.griefalert.api.data.Permission;
import org.spongepowered.api.service.permission.Subject;

public final class Permissions {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private Permissions() {
  }

  public static boolean has(Subject user, Permission permission) {
    return user.hasPermission(permission.get());
  }

  /* === COMMANDS === */

  /* Generic command permissions for staff members */
  public static final Permission GRIEFALERT_COMMAND = Permission.of(
      "griefalert.staff.command"
  );
  /* Can use 'check' command */
  public static final Permission GRIEFALERT_COMMAND_CHECK = Permission.of(
      "griefalert.staff.command.check"
  );
  /* Can use 'info' command */
  public static final Permission GRIEFALERT_COMMAND_INFO = Permission.of(
      "griefalert.staff.command.info"
  );
  /* Can use 'query' command */
  public static final Permission GRIEFALERT_COMMAND_QUERY = Permission.of(
      "griefalert.staff.command.query"
  );
  /* Can use 'fix' command */
  public static final Permission GRIEFALERT_COMMAND_FIX = Permission.of(
      "griefalert.staff.command.fix"
  );
  /* Can use 'show' command */
  public static final Permission GRIEFALERT_COMMAND_SHOW = Permission.of(
      "griefalert.staff.command.show"
  );
  /* Can use 'profile' command */
  public static final Permission GRIEFALERT_COMMAND_PROFILE = Permission.of(
      "griefalert.admin.command.profile"
  );
  /* Can use 'reload' command */
  public static final Permission GRIEFALERT_COMMAND_RELOAD = Permission.of(
      "griefalert.admin.command.reload"
  );
  /* Can use 'logs' command */
  public static final Permission GRIEFALERT_COMMAND_LOGS = Permission.of(
      "griefalert.admin.command.logs"
  );
  /* Can use 'rollback' command */
  public static final Permission GRIEFALERT_COMMAND_ROLLBACK = Permission.of(
      "griefalert.admin.command.rollback"
  );

  /* === ABILITIES === */

  /* Receive incoming alert messages */
  public static final Permission GRIEFALERT_MESSAGING = Permission.of(
      "griefalert.staff.messaging"
  );
  /* Mute outgoing alert messages */
  public static final Permission GRIEFALERT_SILENT = Permission.of(
      "griefalert.staff.silent"
  );
  /* Can use the tool */
  public static final Permission GRIEFALERT_TOOL = Permission.of(
      "griefalert.staff.tool"
  );
  /* Allow 'fix' command on ones own alert */
  public static final Permission SELF_FIX = Permission.of(
      "griefalert.admin.selffix"
  );

}
