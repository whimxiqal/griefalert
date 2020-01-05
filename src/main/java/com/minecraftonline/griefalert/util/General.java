package com.minecraftonline.griefalert.util;

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * A General tools class to house static methods for small data manipulations and methods.
 */
public abstract class General {

  /**
   * Send a series of colored messages to the console, signifying the initialization
   * of the plugin.
   */
  @SuppressWarnings("all")
  public static void stampConsole() {
    Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.DARK_PURPLE, "     ____          "));
    Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.DARK_PURPLE, "    /         /\\     ", TextColors.GOLD, "GriefAlert ", TextColors.DARK_GRAY, "v" + GriefAlert.VERSION, " by PietElite"));
    Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.DARK_PURPLE, "   |    ===  /__\\    ", TextColors.DARK_AQUA, "Built for MinecraftOnline"));
    Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.DARK_PURPLE, "    \\____/  /    \\    "));
    Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.DARK_PURPLE, "                   "));
  }

  /**
   * Format the grief checker's name to include prefix and suffix.
   *
   * @param player The grief checker
   * @return The Text form of the grief checker's name
   */
  public static Text formatPlayerName(Player player) {
    return player.getDisplayNameData().displayName().get();
  }

  /**
   * Reformats a string so all spaces are replaced with underscores
   * and it has a 'minecraft:' tag if it didn't have a tag previously.
   * @param unsure The input string
   * @return The updated string.
   */
  public static String ensureIdFormat(String unsure) {
    unsure = unsure.replaceAll(" ", "_");
    if (!unsure.contains(":")) {
      unsure = "minecraft:" + unsure;
    }
    return unsure;
  }

  /**
   * Print the stack trace of an exception only to the debug logger
   * to reduce clutter on the main console.
   * @param e The exception to print
   */
  public static void printStackTraceToDebugLogger(Exception e) {
    GriefAlert.getInstance().getLogger().debug(e.getMessage());
    for (StackTraceElement element : e.getStackTrace()) {
      GriefAlert.getInstance().getLogger().debug(element.toString());
    }
  }

}
