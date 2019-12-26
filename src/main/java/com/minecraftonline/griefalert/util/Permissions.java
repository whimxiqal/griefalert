package com.minecraftonline.griefalert.util;

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
  public static final Permission GRIEFALERT_COMMAND_LOGS = new Permission(
      "griefalert.command.logs"
  );
  public static final Permission GRIEFALERT_COMMAND_ROLLBACK = new Permission(
      "griefalert.command.rollback"
  );
  public static final Permission GRIEFALERT_COMMAND_BUILD = new Permission(
      "griefalert.command.build"
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

  public static class Permission {

    private final String permissionString;

    private Permission(String permissionString) {
      this.permissionString = permissionString;
    }

    public String toString() {
      return permissionString;
    }
  }

}
