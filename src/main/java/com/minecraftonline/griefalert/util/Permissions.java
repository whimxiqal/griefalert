/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import com.minecraftonline.griefalert.api.data.Permission;

public final class Permissions {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private Permissions() {
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
  public static final Permission GRIEFALERT_COMMAND_RECENT = Permission.of(
      "griefalert.command.recent"
  );
  public static final Permission GRIEFALERT_COMMAND_PROFILE = Permission.of(
      "griefalert.command.profile"
  );
  public static final Permission GRIEFALERT_COMMAND_SHOW = Permission.of(
      "griefalert.command.show"
  );
  public static final Permission GRIEFALERT_COMMAND_RELOAD = Permission.of(
      "griefalert.command.reload"
  );

  @SuppressWarnings("WeakerAccess")
  public static final Permission GRIEFALERT_MESSAGING = Permission.of(
      "griefalert.messaging"
  );
  public static final Permission GRIEFALERT_SILENT = Permission.of(
      "griefalert.silent"
  );

}
