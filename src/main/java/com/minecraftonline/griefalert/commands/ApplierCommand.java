/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert.commands;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.helion3.prism.api.data.PrismEvent;
import com.helion3.prism.api.services.PrismService;
import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.prism.PrismAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.commands.common.GeneralCommand;
import com.minecraftonline.griefalert.api.structures.MapList;
import com.minecraftonline.griefalert.util.Communication;
import com.minecraftonline.griefalert.util.DateUtil;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.WorldEditUtil;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.util.enums.Permissions;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.sponge.SpongeWorld;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;


public final class ApplierCommand extends GeneralCommand {

  public enum ApplyType {
    ROLLBACK,
    RESTORE
  }

  public ApplierCommand(Text description, ApplyType applyType, String... aliases) {
    super(Permissions.GRIEFALERT_COMMAND_ROLLBACK, description);
    if (applyType.equals(ApplyType.ROLLBACK)) {
      addChild(new AlertCommand(applyType));
    }
    addChild(new RegionCommand(applyType));
    Lists.newArrayList(aliases).forEach(this::addAlias);
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
    sendHelp(src);
    return CommandResult.success();
  }

  public static class AlertCommand extends GeneralCommand {

    private final ApplyType applyType;

    AlertCommand(ApplyType applyType) {
      super(
          Permissions.GRIEFALERT_COMMAND_ROLLBACK,
          Text.of("Use an alert id to make the desired change")
      );
      this.applyType = applyType;
      addAlias("alert");
      addAlias("a");
      setCommandElement(GenericArguments.onlyOne(
          GenericArguments.integer(CommandKeys.ALERT_INDEX.get())));
    }


    @Override
    @Nonnull
    public CommandResult execute(@Nonnull CommandSource src,
                                 @Nonnull CommandContext args) throws CommandException {

      // Find PrismAlert to manipulate
      PrismAlert alert;
      int index;
      try {
        index = args.<Integer>requireOne(CommandKeys.ALERT_INDEX.get());
      } catch (NoSuchElementException e) {
        sendHelp(src);
        return CommandResult.success();
      }

      try {
        Alert alertGeneral = GriefAlert.getInstance().getAlertService().get(index);
        if (alertGeneral instanceof PrismAlert) {
          alert = (PrismAlert) alertGeneral;
        } else {
          throw new CommandException(Format.error("This alert cannot be manipulated"));
        }
      } catch (IllegalArgumentException e) {
        throw Errors.noAlertException();
      }

      if (alert.rollback(src)) {
        src.sendMessage(Format.success("Rollback successful!"));
        Communication.getStaffBroadcastChannelWithout(src).send(
            Format.info(
                (src instanceof Player) ? Format.userName((Player) src) : src.getName(),
                Format.space(),
                "just rolled back alert number",
                Format.space(),
                Format.bonus(index),
                ".",
                Format.space(),
                Format.getTagInfo(index)
            )
        );
        return CommandResult.success();
      } else {
        throw new CommandException(Format.error("Rollback failed"));
      }
    }

  }

  public static class RegionCommand extends GeneralCommand {

    private final ApplyType applyType;

    RegionCommand(ApplyType applyType) {
      super(
          Permissions.GRIEFALERT_COMMAND_ROLLBACK,
          Text.of("Use a region to rollback everything inside. "
              + "Use full target id for all except entities.")
      );
      this.applyType = applyType;
      addAlias("region");
      addAlias("r");
      setCommandElement(GenericArguments.flags()
          .valueFlag(GenericArguments.string(CommandKeys.SINCE.get()), "s")
          .valueFlag(GenericArguments.string(CommandKeys.BEFORE.get()), "b")
          .valueFlag(GenericArguments.string(CommandKeys.PLAYER.get()), "p")
          .valueFlag(GenericArguments.string(CommandKeys.PRISM_TARGET.get()), "t")
          .valueFlag(GenericArguments.catalogedElement(CommandKeys.PRISM_EVENT.get(), PrismEvent.class), "e")
          .buildWith(GenericArguments.none()));
    }

    @Override
    @Nonnull
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {

      MapList<Text, Text> flags = new MapList<>(Maps.newHashMap());

      if (src instanceof Player) {
        Player player = (Player) src;

        Task.builder().async().execute(() -> {

          PrismService.Request.Builder requestBuilder = PrismService.requestBuilder();

          // Add location query with WE
          SpongeWorld spongeWorld = SpongeWorldEdit.inst().getWorld(player.getLocation().getExtent());
          Region selection;
          try {
            selection = SpongeWorldEdit.inst().getSession(player).getSelection(spongeWorld);
          } catch (IncompleteRegionException e) {
            player.sendMessage(Format.error("No region selected"));
            return;
          }
          Vector3i minVector = WorldEditUtil.convertVector(selection.getMinimumPoint());
          Vector3i maxVector = WorldEditUtil.convertVector(selection.getMaximumPoint());
          requestBuilder.setxRange(minVector.getX(), maxVector.getX());
          requestBuilder.setyRange(minVector.getY(), maxVector.getY());
          requestBuilder.setzRange(minVector.getZ(), maxVector.getZ());


          // Parse the 'since' with the given date format, or just do a year ago
          Date since = args.<String>getOne(CommandKeys.SINCE.get()).flatMap(str -> {
            try {
              return Optional.of(DateUtil.parseAnyDate(str));
            } catch (IllegalArgumentException e) {
              player.sendMessage(Format.error(e.getMessage()));
              return Optional.empty();
            }
          }).orElseGet(() -> Date.from(Instant.now().minus(Duration.ofHours(1))));
          flags.add(CommandKeys.SINCE.get(), Text.of(Format.date(since)));
          requestBuilder.setEarliest(since);

          args.<String>getOne(CommandKeys.BEFORE.get()).ifPresent(str -> {
            Date before = DateUtil.parseAnyDate(str);
            flags.add(CommandKeys.BEFORE.get(), Text.of(Format.date(before)));
            requestBuilder.setLatest(before);
          });

          args.<String>getAll(CommandKeys.PRISM_TARGET.get()).forEach(str -> {
            flags.add(CommandKeys.PRISM_TARGET.get(), Text.of(str));
            requestBuilder.addTarget(str);
          });

          for (String name : args.<String>getAll(CommandKeys.PLAYER.get())) {
            try {
              GameProfile gameProfile = Sponge.getServer()
                  .getGameProfileManager()
                  .get(name)
                  .get();
              flags.add(CommandKeys.PLAYER.get(), Text.of(name));
              requestBuilder.addPlayerUuid(gameProfile.getUniqueId());
            } catch (ExecutionException e) {
              player.sendMessage(Format.error(String.format("Player %s not found", name)));
              return;
            } catch (Exception e) {
              player.sendMessage(Format.error("Error trying to access game profile for " + name));
              e.printStackTrace();
            }
          }

          Collection<PrismEvent> events = args.getAll(CommandKeys.PRISM_EVENT.get());
          if (events.isEmpty()) {
            flags.add(CommandKeys.PRISM_EVENT.get(), Text.of(PrismEvents.BLOCK_BREAK.getId()));
            requestBuilder.addEvent(PrismEvents.BLOCK_BREAK);
          } else {
            events.forEach(event -> {
              flags.add(CommandKeys.PRISM_EVENT.get(), Text.of(event.getId()));
              requestBuilder.addEvent(event);
            });
          }

          PrismService.Request request = requestBuilder.build();
          player.sendMessage(Format.info(
              "Using parameters: ",
              Text.joinWith(
                  Format.bonus(", "),
                  flags.getMap().entrySet()
                      .stream()
                      .map(entry ->
                          Format.bonus(
                              "{",
                              entry.getKey(),
                              ": ",
                              Text.joinWith(
                                  Text.of(","),
                                  entry.getValue()),
                              "}"))
                      .collect(Collectors.toList()))));
          Optional<PrismService> prism = Sponge.getServiceManager().provide(PrismService.class);
          try {
            if (prism.isPresent()) {
              if (applyType.equals(ApplyType.ROLLBACK)) {
                prism.get().rollback(player, request);
              } else if (applyType.equals(ApplyType.RESTORE)) {
                prism.get().restore(player, request);
              } else {
                throw new RuntimeException("Applier command tried an invalid ApplyType: " + applyType.name());
              }
            } else {
              throw new RuntimeException("Could not get PrismService from Sponge Service Manager");
            }
          } catch (Exception e) {
            player.sendMessage(Format.error("An error occurred communicating with Prism"));
            e.printStackTrace();
          }
        }).submit(GriefAlert.getInstance());

        return CommandResult.success();
      } else {
        throw Errors.playerOnlyException();
      }
    }
  }

}
