/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.google.common.collect.Maps;
import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.query.Sort;
import com.helion3.prism.commands.ApplierCommand;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.prism.PrismAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.GeneralCommand;
import com.minecraftonline.griefalert.util.Communication;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.PrismUtil;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.util.enums.Permissions;

import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;


public class RollbackCommand extends GeneralCommand {


  RollbackCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_ROLLBACK,
        Text.of("Revert to previous states")
    );
    addAlias("rollback");
    addAlias("rb");
    addChild(new AlertCommand());
    addChild(new RegionCommand());
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
    sendHelp(src);
    return CommandResult.success();
  }

  public static class AlertCommand extends GeneralCommand {

    AlertCommand() {
      super(
          Permissions.GRIEFALERT_COMMAND_ROLLBACK,
          Text.of("Use an alert id to revert the specific event")
      );
      addAlias("alert");
      addAlias("a");
      setCommandElement(GenericArguments.onlyOne(
          GenericArguments.integer(CommandKeys.ALERT_INDEX.get())));
    }


    @Override
    @Nonnull
    public CommandResult execute(@Nonnull CommandSource src,
                                 @Nonnull CommandContext args) {

      PrismAlert alert;
      try {
        Alert alertGeneral = GriefAlert.getInstance()
            .getAlertManager().getAlertCache()
            .get(args.<Integer>getOne(CommandKeys.ALERT_INDEX.get())
                .orElseThrow(() -> new RuntimeException("Required argument")));
        if (alertGeneral instanceof PrismAlert) {
          alert = (PrismAlert) alertGeneral;
        } else {
          src.sendMessage(Format.error("This alert can not be rolled back"));
          return CommandResult.empty();
        }
      } catch (IndexOutOfBoundsException e) {
        src.sendMessage(Format.error("That alert could not be found"));
        return CommandResult.empty();
      }

      if (alert.isReversed()) {
        src.sendMessage(Format.error("This alert has already be rolled back"));
        return CommandResult.empty();
      }

      if (alert.rollback(src)) {
        src.sendMessage(Format.success("Rollback successful!"));
        Communication.getStaffBroadcastChannelWithout(src).send(
            Format.info(
                (src instanceof Player) ? Format.userName((Player) src) : src.getName(),
                Format.space(),
                "just rolled back alert number",
                Format.space(),
                Format.bonus(alert.getCacheIndex()),
                ".",
                Format.space(),
                Format.getTagInfo(alert.getCacheIndex())
            )
        );
        return CommandResult.success();
      } else {
        src.sendMessage(Format.error("Rollback failed"));
        return CommandResult.empty();
      }

    }

  }

  public static class RegionCommand extends GeneralCommand {

    RegionCommand() {
      super(
          Permissions.GRIEFALERT_COMMAND_ROLLBACK,
          Text.of("Use a region to rollback everything inside. "
              + "Use full target id for all except entities.")
      );
      addAlias("region");
      addAlias("r");
      setCommandElement(GenericArguments.flags()
          .valueFlag(GenericArguments.string(CommandKeys.SINCE.get()), "s")
          .valueFlag(GenericArguments.string(CommandKeys.BEFORE.get()), "b")
          .valueFlag(GenericArguments.string(CommandKeys.PLAYER.get()), "p")
          .valueFlag(GenericArguments.string(CommandKeys.PRISM_TARGET.get()), "t")
          .valueFlag(GenericArguments.string(CommandKeys.PRISM_EVENT.get()), "e")
          .buildWith(GenericArguments.none()));
    }

    @Override
    @Nonnull
    public CommandResult execute(@Nonnull CommandSource src,
                                 @Nonnull CommandContext args) {
      if (src instanceof Player) {
        Player player = (Player) src;

        Map<Text, Text> flags = Maps.newHashMap();

        PrismUtil.buildSession(player, args, flags).thenAccept(sessionOptional ->
            sessionOptional.ifPresent(session -> {
              player.sendMessage(Format.info(
                  "Using parameters: ",
                  Text.joinWith(
                      Format.bonus(", "),
                      flags.entrySet()
                          .stream()
                          .map(entry ->
                              Format.bonus("{", entry.getKey(), ": ", entry.getValue(), "}"))
                          .collect(Collectors.toList()))));
              session.addFlag(Flag.NO_GROUP);
              ApplierCommand.runApplier(session, Sort.NEWEST_FIRST);
            }));

        return CommandResult.success();
      } else {
        Errors.sendPlayerOnlyCommand(src);
        return CommandResult.empty();
      }
    }

  }

}
