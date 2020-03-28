/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.api.commands.LegacyCommand;
import com.minecraftonline.griefalert.util.enums.Permissions;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Helper final class to provide commands which are not used anymore.
 */
public final class LegacyCommands {

  /**
   * Private constructor so this class cannot be instantiated.
   */
  private LegacyCommands() {
  }

  /**
   * Immutable list of deprecated commands.
   */
  private static final List<LegacyCommand> list = Lists.newArrayList(
      LegacyCommand.of(Permissions.GRIEFALERT_COMMAND_CHECK, "gcheck", "griefalert check"),
      LegacyCommand.of(Permissions.GRIEFALERT_COMMAND_QUERY, "grecent", "griefalert query")
  );

  @Nonnull
  public static List<LegacyCommand> get() {
    return list;
  }

}
