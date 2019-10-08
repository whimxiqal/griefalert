package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.logging.LogMessage;
import com.minecraftonline.griefalert.griefevents.logging.LogQuery;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class GriefAlertLogsCommand extends AbstractCommand {

  private static final int MAX_LOG_COUNT_PRINTED = 10;

  GriefAlertLogsCommand(GriefAlert plugin) {
    super(
        plugin,
        GriefAlert.Permission.GRIEFALERT_COMMAND_LOGS,
        Text.of("Get information about the logged data")
    );
    addAlias("logs");
    addAlias("l");
    addCommandElement(GenericArguments.flags()
        .valueFlag(GenericArguments.string(Text.of("after")), "-after")
        .valueFlag(GenericArguments.string(Text.of("before")), "-before")
        .valueFlag(GenericArguments.string(Text.of("object")), "-id")
        .valueFlag(GenericArguments.string(Text.of("type")), "-type")
        .buildWith(GenericArguments.none())
    );
  }

  @Override
  @NonnullByDefault
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;
      LogQuery.Builder queryBuilder = LogQuery.builder();

      // Add block conditions
      com.sk89q.worldedit.entity.Player wePlayer = SpongeWorldEdit.inst().wrapPlayer(player);
      LocalSession wePlayerLocSess = plugin.getWorldEditInstance().getSessionManager().get(wePlayer);
      try {
        Region selection = wePlayerLocSess.getSelection(wePlayer.getWorld());
        queryBuilder.addBlockCondition(
            selection.getMinimumPoint().getBlockX(),
            selection.getMinimumPoint().getBlockY(),
            selection.getMinimumPoint().getBlockZ(),
            selection.getMaximumPoint().getBlockX(),
            selection.getMaximumPoint().getBlockY(),
            selection.getMaximumPoint().getBlockZ(),
            player.getLocation().getExtent().getDimension().getType()
        );
      } catch (IncompleteRegionException e) {
        player.sendMessage(Text.of(TextColors.YELLOW, "You do not have a region selected. Selecting block at your location."));
        queryBuilder.addBlockCondition(
            player.getLocation().getBlockX(),
            player.getLocation().getBlockY(),
            player.getLocation().getBlockZ(),
            player.getLocation().getExtent().getDimension().getType()
        );
      }

      // Add time condition
      if (args.<String>getOne("after").isPresent()) {
        String format = "yyyy-MM-dd";
        DateFormat inputDateFormat = new SimpleDateFormat(format);
        inputDateFormat.setTimeZone(TimeZone.getTimeZone("BST"));
        try {
          Date date = inputDateFormat.parse(args.<String>getOne("after").get());
          queryBuilder.addAfterCondition(date);
        } catch (ParseException e) {
          player.sendMessage(Text.of(TextColors.RED, "Invalid date format for flag 'after'. Use " + format));
          return CommandResult.success();
        }
      } else {
        queryBuilder.addAfterCondition(new Date((new Date()).getTime() - 31536000000L)); // Look for after one year ago
      }

      if (args.<String>getOne("before").isPresent()) {
        String format = "yyyy-MM-dd";
        DateFormat inputDateFormat = new SimpleDateFormat(format);
        inputDateFormat.setTimeZone(TimeZone.getTimeZone("BST"));
        try {
          Date date = inputDateFormat.parse(args.<String>getOne("before").get());
          queryBuilder.addBeforeCondition(date);
        } catch (ParseException e) {
          player.sendMessage(Text.of(TextColors.RED, "Invalid date format for flag 'before'. Use " + format));
          return CommandResult.success();
        }
      }

      // Add object id condition
      for (String object : args.<String>getAll("object")) {
        String addNamespace = object;
        if (!addNamespace.contains(":")) {
          addNamespace = "minecraft:" + addNamespace;
        }
        queryBuilder.addGriefedCondition(addNamespace);
      }

      // Add grief type condition
      for (String type : args.<String>getAll("type")) {
        queryBuilder.addTypeCondition(type);
      }

      LogQuery query = queryBuilder.build();
      List<LogMessage> logs = plugin.getGriefLogger().getLogs(query);
      if (logs.isEmpty()) {
        player.sendMessage(Text.of(TextColors.GREEN, "No logs match those conditions."));
      } else {
        Collections.sort(logs);  // Logs are comparable in chronological order
        for (int i = Math.max(0, logs.size() - MAX_LOG_COUNT_PRINTED); i < logs.size(); i++) {
          player.sendMessage(logs.get(i).asText());
        }
        if (logs.size() > MAX_LOG_COUNT_PRINTED) {
          player.sendMessage(Text.of(
              TextColors.YELLOW, "Only showing ",
              TextColors.LIGHT_PURPLE, MAX_LOG_COUNT_PRINTED,
              TextColors.YELLOW, " out of ",
              TextColors.LIGHT_PURPLE, logs.size(),
              TextColors.YELLOW, " total search results"));
        }
        player.sendMessage(Text.of(TextColors.GREEN, "Showed logs with these conditions: ", TextColors.RED, "COMING SOON"));
      }
    }
    return CommandResult.success();
  }
}
