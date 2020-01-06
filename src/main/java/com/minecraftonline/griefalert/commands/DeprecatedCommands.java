/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.api.commands.DeprecatedCommand;
import com.minecraftonline.griefalert.util.Permissions;

import java.util.List;

public class DeprecatedCommands {

  private static final List<DeprecatedCommand> list = Lists.newArrayList(
      DeprecatedCommand.of(Permissions.GRIEFALERT_COMMAND_CHECK, "gcheck", "griefalert check"),
      DeprecatedCommand.of(Permissions.GRIEFALERT_COMMAND_RECENT, "grecent", "griefalert recent")
  );

  public static List<DeprecatedCommand> get() {
    return list;
  }
}
