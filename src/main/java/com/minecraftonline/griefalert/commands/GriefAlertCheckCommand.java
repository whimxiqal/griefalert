/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.Permissions;
import javax.annotation.Nonnull;

import com.minecraftonline.griefalert.util.enums.Settings;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.concurrent.TimeUnit;

public class GriefAlertCheckCommand extends AbstractCommand {

  GriefAlertCheckCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_CHECK,
        Text.of("Check the grief alert with the given id")
    );
    addAlias("check");
    addAlias("c");
    setCommandElement(GenericArguments.onlyOne(GenericArguments.integer(Text.of("index"))));
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) {
    if (src instanceof Player) {
      Player player = (Player) src;
      if (args.<Integer>getOne("index").isPresent()) {

        try {

          Alert alert = GriefAlert.getInstance()
              .getAlertManager().getAlertCache()
              .get(args.<Integer>getOne("index").get());

          // Give invulnerability
          EventListener<DamageEntityEvent> cancelDamage = event -> {
            if (event.getTargetEntity().getUniqueId().equals(player.getUniqueId())) {
              event.setCancelled(true);
            }
          };
          Sponge.getEventManager().registerListener(
              GriefAlert.getInstance(),
              DamageEntityEvent.class,
              cancelDamage);
          Task.builder().delay(Settings.CHECK_INVULNERABILITY.getValue(), TimeUnit.SECONDS)
              .execute(() -> {
                Sponge.getEventManager().unregisterListeners(cancelDamage);
                player.sendMessage(Format.info("Invulnerability revoked"));
              })
              .name("Remove invulnerability for player " + player.getName())
              .submit(GriefAlert.getInstance());

          GriefAlert.getInstance().getAlertManager().check(alert, player);

          player.sendMessage(Format.info(String.format(
              "You have been given %d seconds of invulnerability",
              Settings.CHECK_INVULNERABILITY.getValue())));

        } catch (IndexOutOfBoundsException e) {
          player.sendMessage(Format.error("That alert could not be found."));
        }
      } else {
        player.sendMessage(Format.error(Text.of(
            TextColors.RED,
            "The alert code could not be parsed.")));
      }
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
