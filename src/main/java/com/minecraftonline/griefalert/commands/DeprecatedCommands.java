/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.api.commands.ReplacedCommand;
import com.minecraftonline.griefalert.util.Permissions;

import java.util.List;

public class DeprecatedCommands {

  private static final List<ReplacedCommand> list = Lists.newArrayList(
      ReplacedCommand.of(Permissions.GRIEFALERT_COMMAND_CHECK, "gcheck", "griefalert check"),
      ReplacedCommand.of(Permissions.GRIEFALERT_COMMAND_RECENT, "grecent", "griefalert recent")
  );

  public static List<ReplacedCommand> get() {
    return list;
  }
}
