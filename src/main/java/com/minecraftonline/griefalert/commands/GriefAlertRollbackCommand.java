package com.minecraftonline.griefalert.commands;

import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.query.ConditionGroup;
import com.helion3.prism.api.query.FieldCondition;
import com.helion3.prism.api.query.MatchRule;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.query.Sort;
import com.helion3.prism.commands.ApplierCommand;
import com.helion3.prism.util.DataQueries;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.prism.PrismAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Communication;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Permissions;
import com.minecraftonline.griefalert.util.WorldEditUtil;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.sponge.SpongeWorld;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.annotation.Nonnull;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;




public class GriefAlertRollbackCommand extends AbstractCommand {


  GriefAlertRollbackCommand() {
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

  public static class AlertCommand extends AbstractCommand {

    AlertCommand() {
      super(
          Permissions.GRIEFALERT_COMMAND_ROLLBACK,
          Text.of("Use an alert id to revert the specific event")
      );
      addAlias("alert");
      addAlias("a");
      setCommandElement(GenericArguments.onlyOne(GenericArguments.integer(Text.of("index"))));
    }


    @Override
    @Nonnull
    public CommandResult execute(@Nonnull CommandSource src,
                                 @Nonnull CommandContext args) {

      PrismAlert alert;
      try {
        Alert alertGeneral = GriefAlert.getInstance().getRotatingAlertList()
            .get(args.<Integer>getOne("index").get());
        if (alertGeneral instanceof PrismAlert) {
          alert = (PrismAlert) alertGeneral;
        } else {
          src.sendMessage(Format.error("This alert can not be rolled back."));
          return CommandResult.empty();
        }
      } catch (IndexOutOfBoundsException e) {
        src.sendMessage(Format.error("That alert could not be found."));
        return CommandResult.empty();
      }

      if (alert.isReversed()) {
        src.sendMessage(Format.error("This alert has already be rolled back."));
        return CommandResult.empty();
      }

      if (alert.rollback(src)) {
        src.sendMessage(Format.success("Rollback successful!"));
        Communication.getStaffBroadcastChannelWithout(src).send(
            Format.info(
                (src instanceof Player) ? Format.playerName((Player) src) : src.getName(),
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
        src.sendMessage(Format.error("Rollback failed."));
        return CommandResult.empty();
      }

    }

  }

  public static class RegionCommand extends AbstractCommand {

    RegionCommand() {
      super(
          Permissions.GRIEFALERT_COMMAND_ROLLBACK,
          Text.of("Use a region to rollback everything inside")
      );
      addAlias("region");
      addAlias("r");
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
          .valueFlag(GenericArguments.string(
              Text.of("target")),
              "-target")
          .valueFlag(GenericArguments.string(
              Text.of("event")),
              "-event")
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

        Query query = session.newQuery();
        session.addFlag(Flag.NO_GROUP);

        // Add location query with WE
        World world = player.getLocation().getExtent();
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
        query.addCondition(FieldCondition.of(
            DataQueries.Created,
            MatchRule.GREATER_THAN_EQUAL,
            sinceFlag));

        args.<String>getOne("before").ifPresent(str -> {
          try {
            Date beforeFlag = dateFormat.parse(str);
            query.addCondition(FieldCondition.of(
                DataQueries.Created,
                MatchRule.LESS_THAN_EQUAL,
                beforeFlag));
          } catch (ParseException e) {
            src.sendMessage(Format.error("Date format: dd-MM-yyyy"));
          }
        });

        args.<String>getOne("target").ifPresent(str ->
            query.addCondition(FieldCondition.of(DataQueries.Target, MatchRule.EQUALS, str)));

        args.<String>getOne("player").ifPresent(str -> {
          Optional<Player> playerFlag = Sponge.getServer().getPlayer(str);
          if (playerFlag.isPresent()) {
            query.addCondition(FieldCondition.of(
                DataQueries.Player,
                MatchRule.EQUALS,
                playerFlag.get().getUniqueId().toString()));
          } else {
            src.sendMessage(Format.error("Player not found: " + str));
          }
        });

        args.<String>getOne("event").ifPresent(str ->
            query.addCondition(FieldCondition.of(
                DataQueries.EventName,
                MatchRule.EQUALS,
                str)));

        ApplierCommand.runApplier(src, session, Sort.NEWEST_FIRST);

        return CommandResult.success();
      } else {
        Errors.sendPlayerOnlyCommand(src);
        return CommandResult.empty();
      }
    }

  }

}
