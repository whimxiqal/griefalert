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
  public static final Permission GRIEFALERT_COMMAND_QUERY = Permission.of(
      "griefalert.command.query"
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

}
