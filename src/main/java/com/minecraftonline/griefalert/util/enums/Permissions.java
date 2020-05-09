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

  public static final Permission GRIEFALERT_COMMAND = Permission.of(
      "griefalert.command"
  );
  public static final Permission GRIEFALERT_COMMAND_CHECK = Permission.of(
      "griefalert.command.check"
  );
  public static final Permission GRIEFALERT_COMMAND_INFO = Permission.of(
      "griefalert.command.info"
  );
  public static final Permission GRIEFALERT_COMMAND_QUERY = Permission.of(
      "griefalert.command.query"
  );
  public static final Permission GRIEFALERT_COMMAND_FIX = Permission.of(
      "griefalert.command.fix"
  );
  public static final Permission GRIEFALERT_COMMAND_SHOW = Permission.of(
      "griefalert.command.show"
  );
  public static final Permission GRIEFALERT_COMMAND_PROFILE = Permission.of(
      "griefalert.command.profile"
  );
  public static final Permission GRIEFALERT_COMMAND_RELOAD = Permission.of(
      "griefalert.command.reload"
  );
  public static final Permission GRIEFALERT_COMMAND_LOGS = Permission.of(
      "griefalert.command.logs"
  );
  public static final Permission GRIEFALERT_COMMAND_ROLLBACK = Permission.of(
      "griefalert.command.rollback"
  );


  @SuppressWarnings("WeakerAccess")
  public static final Permission GRIEFALERT_MESSAGING = Permission.of(
      "griefalert.messaging"
  );
  public static final Permission GRIEFALERT_SILENT = Permission.of(
      "griefalert.silent"
  );
  public static final Permission GRIEFALERT_UNRESTRICTED = Permission.of(
      "griefalert.unrestricted"
  );

}
