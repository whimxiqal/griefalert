/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.api.commands.LegacyCommand;
import com.minecraftonline.griefalert.util.Permissions;

import java.util.List;

public class DeprecatedCommands {

  private static final List<LegacyCommand> list = Lists.newArrayList(
      LegacyCommand.of(Permissions.GRIEFALERT_COMMAND_CHECK, "gcheck", "griefalert check"),
      LegacyCommand.of(Permissions.GRIEFALERT_COMMAND_RECENT, "grecent", "griefalert recent")
  );

  public static List<LegacyCommand> get() {
    return list;
  }
}
