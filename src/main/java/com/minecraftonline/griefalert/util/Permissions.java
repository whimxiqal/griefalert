package com.minecraftonline.griefalert.util;

import com.minecraftonline.griefalert.api.data.Permission;

public abstract class Permissions {

  public static final Permission GRIEFALERT_COMMAND = new Permission(
      "griefalert.command"
  );
  public static final Permission GRIEFALERT_COMMAND_CHECK = new Permission(
      "griefalert.command.check"
  );
  public static final Permission GRIEFALERT_COMMAND_INFO = new Permission(
      "griefalert.command.info"
  );
  public static final Permission GRIEFALERT_COMMAND_RECENT = new Permission(
      "griefalert.command.recent"
  );

  public static final Permission GRIEFALERT_MESSAGING = new Permission(
      "griefalert.messaging"
  );
  public static final Permission GRIEFALERT_SILENT = new Permission(
      "griefalert.silent"
  );
  public static final Permission GRIEFALERT_DEGRIEF = new Permission(
      "griefalert.degrief"
  );
  public static final Permission GRIEFALERT_COMMAND_RELOAD = new Permission(
      "griefalert.reload"
  );

}
