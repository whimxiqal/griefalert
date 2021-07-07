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

package com.minecraftonline.griefalert.sponge.alert.commands;

import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.griefalert.SpongeGriefAlert;
import com.minecraftonline.griefalert.common.data.flags.Flag;
import com.minecraftonline.griefalert.common.data.services.DataRequest;
import com.minecraftonline.griefalert.common.data.services.DataService;
import com.minecraftonline.griefalert.common.data.struct.PrismEvent;
import com.minecraftonline.griefalert.sponge.alert.commands.common.GeneralCommand;
import com.minecraftonline.griefalert.sponge.alert.util.DateUtil;
import com.minecraftonline.griefalert.sponge.alert.util.Errors;
import com.minecraftonline.griefalert.sponge.alert.util.Format;
import com.minecraftonline.griefalert.sponge.alert.util.WorldEditUtil;
import com.minecraftonline.griefalert.sponge.alert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Permissions;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.sponge.SpongeWorld;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
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
import org.spongepowered.api.text.format.TextColors;

/**
 * Command to query the logs from Prism throughout a WorldEdit region.
 *
 * @author PietElite
 */
public class LogsCommand extends GeneralCommand {

  LogsCommand() {
    super(Permissions.GRIEFALERT_COMMAND_LOGS,
        Text.of("Query the logs from Prism in a WorldEdit region"));
    addAlias("logs");
    addAlias("l");
    addChild(new LogsInspectorCommand());
    setCommandElement(GenericArguments.flags()
        .valueFlag(GenericArguments.string(CommandKeys.AFTER.get()), "s")
        .valueFlag(GenericArguments.string(CommandKeys.BEFORE.get()), "b")
        .valueFlag(GenericArguments.string(CommandKeys.PLAYER.get()), "p")
        .valueFlag(GenericArguments.string(CommandKeys.PRISM_TARGET.get()), "t")
        .valueFlag(GenericArguments
                .catalogedElement(
                    CommandKeys.PRISM_EVENT.get(),
                    PrismEvent.class),
            "e")
        .flag("g")
        .buildWith(GenericArguments.none()));
    addFlagDescription(FlagDescription.AFTER);
    addFlagDescription(FlagDescription.BEFORE);
    addFlagDescription(FlagDescription.PLAYER);
    addFlagDescription(FlagDescription.TARGET);
    addFlagDescription(FlagDescription.EVENT);
    addFlagDescription("g",
        Text.of(TextColors.AQUA, "Group", TextColors.RESET, " identical logs"),
        false);
  }

  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;

      Task.builder().async().execute(() -> {

        DataRequest.Builder builder = DataRequest.builder();

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
        builder.setxRange(minVector.getX(), maxVector.getX());
        builder.setyRange(minVector.getY(), maxVector.getY());
        builder.setzRange(minVector.getZ(), maxVector.getZ());


        // Parse the 'since' with the given date format, or just do a year ago
        Date since = args.<String>getOne(CommandKeys.AFTER.get()).flatMap(str -> {
          try {
            return Optional.of(DateUtil.parseAnyDate(str));
          } catch (IllegalArgumentException e) {
            player.sendMessage(Format.error(e.getMessage()));
            return Optional.empty();
          }
        }).orElseGet(() -> Date.from(Instant.now().minus(Duration.ofHours(1))));
        builder.setEarliest(since);

        args.<String>getOne(CommandKeys.BEFORE.get()).ifPresent(str ->
            builder.setLatest(DateUtil.parseAnyDate(str)));

        args.<String>getAll(CommandKeys.PRISM_TARGET.get()).forEach(builder::addTarget);

        args.<String>getAll(CommandKeys.PLAYER.get()).forEach(name -> {
          try {
            GameProfile gameProfile = Sponge.getServer()
                .getGameProfileManager()
                .get(name)
                .get();
            builder.addPlayerUuid(gameProfile.getUniqueId());
          } catch (ExecutionException e) {
            player.sendMessage(Format.error(String.format("Player %s not found", name)));
          } catch (Exception e) {
            player.sendMessage(Format.error("Error trying to access game profile for " + name));
            e.printStackTrace();
          }
        });

        args.<PrismEvent>getAll(CommandKeys.PRISM_EVENT.get()).forEach(builder::addEvent);

        if (!args.hasAny("group")) {
          builder.addFlag(Flag.NO_GROUP);
        }

        DataRequest request = builder.build();

        player.sendMessage(Format.request(request));
        Optional<DataService> dataService = Sponge.getServiceManager().provide(DataService.class);
        try {
          if (dataService.isPresent()) {
            dataService.get().lookup(player, request);
          } else {
            throw new RuntimeException("Could not get PrismService from Sponge Service Manager");
          }
        } catch (Exception e) {
          player.sendMessage(Format.error("An error occurred communicating with Prism"));
          e.printStackTrace();
        }
      }).submit(SpongeGriefAlert.getSpongeInstance());

      return CommandResult.success();
    } else {
      throw Errors.playerOnlyException();
    }
  }

  /**
   * A command to enable the Prism inspection tool.
   */
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
