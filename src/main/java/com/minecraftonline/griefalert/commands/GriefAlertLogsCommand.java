package com.minecraftonline.griefalert.commands;

import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.query.*;
import com.helion3.prism.util.AsyncUtil;
import com.helion3.prism.util.DataQueries;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Permissions;
import com.minecraftonline.griefalert.util.WorldEditUtil;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.session.SessionOwner;
import com.sk89q.worldedit.sponge.SpongePlayer;
import com.sk89q.worldedit.sponge.SpongeWorld;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;
import com.sk89q.worldedit.sponge.adapter.SpongeImplAdapter;
import com.sk89q.worldedit.sponge.adapter.SpongeImplLoader;
import com.sk89q.worldedit.sponge.adapter.impl.Sponge_Dev_Impl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import javax.annotation.Nonnull;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class GriefAlertLogsCommand extends AbstractCommand {

  GriefAlertLogsCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_LOGS,
        Text.of("Query the logs from PrismUtil. Parameters are PrismUtil syntax.")
    );
    addAlias("logs");
    addAlias("l");
    setCommandElement(GenericArguments.flags()
        .valueFlag(GenericArguments.string(
            Text.of("since")),
            "-since")
        .valueFlag(GenericArguments.string(
            Text.of("before")),
            "-before")
        .valueFlag(GenericArguments.string(
            Text.of("player")),
            "-player")
        .flag("-group")
        .buildWith(GenericArguments.none()));
  }

  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) {
    if (src instanceof Player) {
      Player player = (Player) src;
      // Create a new query session
      final QuerySession session = new QuerySession(src);

      // Don't group the results if specified
      if (!args.getOne("group").isPresent()) {
        session.addFlag(Flag.NO_GROUP);
      }

      Query query = session.newQuery();
      src.sendMessage(Format.heading("Querying records from Prism..."));

      // Add location query with WE
      World world = ((Player) src).getLocation().getExtent();
      SpongeWorld spongeWorld = SpongeWorldEdit.inst().getWorld(world);
      try {
        query.addCondition(ConditionGroup.from(
            world,
            WorldEditUtil.convertVector(
                SpongeWorldEdit.inst().getSession(player)
                    .getSelection(spongeWorld).getMinimumPoint()),
            WorldEditUtil.convertVector(
                SpongeWorldEdit.inst().getSession(player)
                    .getSelection(spongeWorld).getMaximumPoint())));
      } catch (IncompleteRegionException e) {
        player.sendMessage(Format.error("No region selected"));
        return CommandResult.empty();
      }

      // Parse the 'since' with the given date format, or just do a year ago
      DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
      Date sinceFlag = args.<String>getOne("since").map(str -> {
        try {
          return dateFormat.parse(str);
        } catch (ParseException e) {
          src.sendMessage(Format.error("Date format: dd-MM-yyyy. Using a year ago."));
          return Date.from(Instant.now().minus(Duration.ofDays(365)));
        }
      }).orElse(Date.from(Instant.now().minus(Duration.ofDays(365))));
      query.addCondition(FieldCondition.of(DataQueries.Created, MatchRule.GREATER_THAN_EQUAL, sinceFlag));

      args.<String>getOne("before").ifPresent(str -> {
        try {
          Date beforeFlag = dateFormat.parse(str);
          query.addCondition(FieldCondition.of(DataQueries.Created, MatchRule.LESS_THAN_EQUAL, beforeFlag));
        } catch (ParseException e) {
          src.sendMessage(Format.error("Date format: dd-MM-yyyy"));
        }
      });

      if (args.<String>getOne("player").isPresent()) {
        String playerFlag = args.<String>getOne("player").get();
        CompletableFuture<GameProfile> future = Sponge.getServer()
            .getGameProfileManager().get(playerFlag, true);
        future.thenAccept(profile ->
            query.addCondition(FieldCondition.of(
                DataQueries.Player,
                MatchRule.EQUALS,
                profile.getUniqueId().toString()))).thenRun(() -> AsyncUtil.lookup(session));
      } else {
        AsyncUtil.lookup(session);
      }

      return CommandResult.success();
    } else {
      Errors.sendPlayerOnlyCommand(src);
      return CommandResult.empty();
    }
  }

}
