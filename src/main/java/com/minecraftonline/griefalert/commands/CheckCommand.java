/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.GeneralCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.util.enums.Permissions;
import com.minecraftonline.griefalert.util.enums.Settings;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

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
      if (args.<Integer>getOne(CommandKeys.ALERT_INDEX.get()).isPresent()) {

        try {

          Alert alert = GriefAlert.getInstance()
              .getAlertManager().getAlertCache()
              .get(args.<Integer>getOne(CommandKeys.ALERT_INDEX.get()).get());

          if (GriefAlert.getInstance().getAlertManager()
              .check(alert, player, args.hasAny("force"))) {
            giveInvulnerability(player, Settings.CHECK_INVULNERABILITY.getValue());
          }

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

  private void giveInvulnerability(Player player, int seconds) {
    // Give invulnerability
    UUID playerUuid = player.getUniqueId();
    EventListener<DamageEntityEvent> cancelDamage = event -> {
      if (event.getTargetEntity().getUniqueId().equals(playerUuid)) {
        event.setCancelled(true);
      }
    };
    Sponge.getEventManager().registerListener(
        GriefAlert.getInstance(),
        DamageEntityEvent.class,
        cancelDamage);
    Optional<Player> playerOptional = Optional.of(player);
    Task.builder().delay(seconds, TimeUnit.SECONDS)
        .execute(() -> {
          Sponge.getEventManager().unregisterListeners(cancelDamage);
          // Garbage collection might get rid of this player? Made it optional just in case.
          playerOptional.ifPresent(p -> p.sendMessage(Format.info("Invulnerability revoked")));
        })
        .name("Remove invulnerability for player " + player.getName())
        .submit(GriefAlert.getInstance());
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
