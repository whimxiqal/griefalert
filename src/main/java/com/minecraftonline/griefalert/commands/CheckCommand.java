/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.caches.AlertManager;
import com.minecraftonline.griefalert.api.commands.GeneralCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.util.enums.Permissions;
import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;


public class CheckCommand extends GeneralCommand {

  CheckCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_CHECK,
        Text.of("Teleport to the location of event")
    );
    addAlias("check");
    addAlias("c");
    setCommandElement(GenericArguments
        .flags()
        .flag("-force", "f")
        .buildWith(GenericArguments.onlyOne(
            GenericArguments.integer(CommandKeys.ALERT_INDEX.get()))));
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) {
    if (src instanceof Player) {
      Player player = (Player) src;
      args.<Integer>getOne(CommandKeys.ALERT_INDEX.get()).ifPresent(i -> {
        try {
          AlertManager manager = GriefAlert.getInstance().getAlertManager();
          manager.check(
              manager.getAlertCache().get(i),
              player,
              args.hasAny("force"));

        } catch (IndexOutOfBoundsException e) {
          player.sendMessage(Format.error("That alert could not be found."));
        }
      });
    } else {
      src.sendMessage(Format.error(Text.of(
          TextColors.RED,
          "Only players may execute this command")));
    }
    return CommandResult.success();
  }



  /**
   * Get a clickable message that allows the officer to check the alert.
   *
   * @param index the index of the alert saved in the cache
   * @return the formatted <code>Text</code>
   */
  public static Text clickToCheck(int index) {
    return Format.command(String.valueOf(index),
        String.format("/griefalert check %s", index),
        Text.of("Check this alert"));
  }

}
