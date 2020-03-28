/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Communication;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.Permissions;

import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Class for handling command which shows a hologram displaying information about
 * a player at the location of grief.
 */
class GriefAlertShowCommand extends AbstractCommand {

  GriefAlertShowCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_SHOW,
        Text.of("Create a Hologram at the location of grief"));
    addAlias("show");
    addAlias("s");
    setCommandElement(GenericArguments.onlyOne(GenericArguments.integer(Text.of("index"))));
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
    if (src instanceof Player) {
      Player player = (Player) src;
      if (args.<Integer>getOne("index").isPresent()) {

        try {

          Alert alert = GriefAlert.getInstance()
              .getAlertManager().getAlertCache()
              .get(args.<Integer>getOne("index").get());

          // Create temporary hologram of grief
          GriefAlert.getInstance().getHologramManager().createTemporaryHologram(alert);

          // Apply night vision
          player.getOrCreate(PotionEffectData.class)
              .map(ths -> ths.addElement(
                  PotionEffect.builder()
                      .potionType(PotionEffectTypes.NIGHT_VISION)
                      .duration(300)
                      .amplifier(1)
                      .build()))
              .ifPresent(player::offer);

          // Broadcast the attempt at command
          Communication.getStaffBroadcastChannelWithout(player).send(Format.info(
              Format.userName(player),
              " is taking a closer look at alert ",
              Format.bonus(alert.getCacheIndex())));

          return CommandResult.success();
        } catch (IndexOutOfBoundsException e) {
          player.sendMessage(Format.error("That alert could not be found."));
          return CommandResult.empty();
        }
      } else {
        player.sendMessage(Format.error(Text.of(
            TextColors.RED,
            "The alert code could not be parsed.")));
        return CommandResult.empty();
      }
    } else {
      Errors.sendPlayerOnlyCommand(src);
      return CommandResult.empty();
    }
  }
}
