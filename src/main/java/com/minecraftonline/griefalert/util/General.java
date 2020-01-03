package com.minecraftonline.griefalert.util;

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

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
    Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.DARK_PURPLE, "    /         /\\     ", TextColors.GOLD, "GriefAlert ", TextColors.DARK_GRAY, "v" + GriefAlert.VERSION));
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
    return TextSerializers.FORMATTING_CODE.deserialize(
        player.getOption("prefix").orElse("")
            + player.getName()
            + player.getOption("suffix").orElse(""));
  }

  public static <P, S> List<S> convertList(List<P> original, Function<P, S> converter) {
    List<S> output = new LinkedList<>();
    for (P item : original) {
      output.add(converter.apply(item));
    }
    return output;
  }

  public static void printStackTraceToDebugLogger(Exception e) {
    GriefAlert.getInstance().getLogger().debug(e.getMessage());
    for (StackTraceElement element : e.getStackTrace()) {
      GriefAlert.getInstance().getLogger().debug(element.toString());
    }
  }

}
