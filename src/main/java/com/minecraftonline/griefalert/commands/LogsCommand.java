/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.google.common.collect.Maps;
import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.util.AsyncUtil;
import com.minecraftonline.griefalert.api.commands.GeneralCommand;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.PrismUtil;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.util.enums.Permissions;

import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class LogsCommand extends GeneralCommand {

  LogsCommand() {
    super(Permissions.GRIEFALERT_COMMAND_LOGS,
        Text.of("Query the logs from Prism in a WorldEdit region"));
    addAlias("logs");
    addAlias("l");
    addChild(new LogsInspectorCommand());
    setCommandElement(GenericArguments.flags()
        .valueFlag(GenericArguments.string(CommandKeys.SINCE.get()), "s")
        .valueFlag(GenericArguments.string(CommandKeys.BEFORE.get()), "b")
        .valueFlag(GenericArguments.string(CommandKeys.PLAYER.get()), "p")
        .valueFlag(GenericArguments.string(CommandKeys.PRISM_TARGET.get()), "t")
        .valueFlag(GenericArguments.string(CommandKeys.PRISM_EVENT.get()), "e")
        .flag("-group", "g")
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
            // Don't group the results if specified
            if (args.getOne("group").isPresent()) {
              flags.put(Text.of("group"), Text.of("true"));
            } else {
              session.addFlag(Flag.NO_GROUP);
              flags.put(Text.of("group"), Text.of("false"));
            }

            player.sendMessage(Format.info(
                "Using parameters: ",
                Text.joinWith(
                    Format.bonus(", "),
                    flags.entrySet()
                        .stream()
                        .map(entry ->
                            Format.bonus("{", entry.getKey(), ": ", entry.getValue(), "}"))
                        .collect(Collectors.toList()))));

            AsyncUtil.lookup(session);
          }));

      return CommandResult.success();
    } else {
      Errors.sendPlayerOnlyCommand(src);
      return CommandResult.empty();
    }
  }

  public static class LogsInspectorCommand extends GeneralCommand {

    LogsInspectorCommand() {
      super(Permissions.GRIEFALERT_COMMAND_LOGS,
          Text.of("Use Prism's inspection tool. Same as '/pr i'."));
      addAlias("inspect");
      addAlias("i");
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
      return Sponge.getGame().getCommandManager().process(src, "pr i");
    }
  }
}
