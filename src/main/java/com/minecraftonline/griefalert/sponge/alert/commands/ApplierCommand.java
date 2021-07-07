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
import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.SpongeGriefAlert;
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
 * A command to rollback or restore changes using Prism.
 *
 * @author PietElite
 */
public final class ApplierCommand extends GeneralCommand {

  private final ApplyType applyType;

  ApplierCommand(Text description, ApplyType applyType, String... aliases) {
    super(Permissions.GRIEFALERT_COMMAND_ROLLBACK, description);
    this.applyType = applyType;
    Lists.newArrayList(aliases).forEach(this::addAlias);
    setCommandElement(GenericArguments.flags()
        .valueFlag(GenericArguments.string(CommandKeys.AFTER.get()), "a")
        .valueFlag(GenericArguments.string(CommandKeys.BEFORE.get()), "b")
        .valueFlag(GenericArguments.string(CommandKeys.PLAYER.get()), "p")
        .valueFlag(GenericArguments.string(CommandKeys.PRISM_TARGET.get()), "t")
        .valueFlag(GenericArguments.catalogedElement(CommandKeys.PRISM_EVENT.get(), PrismEvent.class), "e")
        .flag("f")
        .buildWith(GenericArguments.none()));
    addFlagDescription(FlagDescription.AFTER);
    addFlagDescription(FlagDescription.BEFORE);
    addFlagDescription(FlagDescription.PLAYER);
    addFlagDescription(FlagDescription.TARGET);
    addFlagDescription(FlagDescription.EVENT);
    addFlagDescription("f",
        Text.of(TextColors.AQUA, "Force", TextColors.RESET, " this action, even if there are signs in it"),
        false);
  }

  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) throws CommandException {

    if (src instanceof Player) {
      Player player = (Player) src;

      Task.builder().async().execute(() -> {

        DataRequest.Builder requestBuilder = DataRequest.builder();

        // Add location query with WE
        SpongeWorld spongeWorld = SpongeWorldEdit.inst().getWorld(player.getLocation().getExtent());
        Region selection;
        try {
          selection = SpongeWorldEdit.inst().getSession(player).getSelection(spongeWorld);
        } catch (IncompleteRegionException e) {
          player.sendMessage(Format.error("No region selected"));
          return;
        }

        // If there are signs, then gotta make sure that rollback is what we want
        if (!args.hasAny("force")
            && applyType.equals(ApplyType.ROLLBACK)
            && WorldEditUtil.containsSign(selection, spongeWorld)) {
          src.sendMessage(Format.info(
              "This region has signs in it! "
                  + "If you rollback, you will not be able to restore the signs' contents. "
                  + "Use the flag --force or -f if you're sure. "));
          return;
        }

        Vector3i minVector = WorldEditUtil.convertVector(selection.getMinimumPoint());
        Vector3i maxVector = WorldEditUtil.convertVector(selection.getMaximumPoint());
        requestBuilder.setxRange(minVector.getX(), maxVector.getX());
        requestBuilder.setyRange(minVector.getY(), maxVector.getY());
        requestBuilder.setzRange(minVector.getZ(), maxVector.getZ());


        // Parse the 'since' with the given date format, or just do a year ago
        Date since = args.<String>getOne(CommandKeys.AFTER.get()).flatMap(str -> {
          try {
            return Optional.of(DateUtil.parseAnyDate(str));
          } catch (IllegalArgumentException e) {
            player.sendMessage(Format.error(e.getMessage()));
            return Optional.empty();
          }
        }).orElseGet(() -> Date.from(Instant.now().minus(Duration.ofMinutes(15))));
        requestBuilder.setEarliest(since);

        args.<String>getOne(CommandKeys.BEFORE.get()).ifPresent(str ->
            requestBuilder.setLatest(DateUtil.parseAnyDate(str)));

        args.<String>getAll(CommandKeys.PRISM_TARGET.get()).forEach(requestBuilder::addTarget);

        for (String name : args.<String>getAll(CommandKeys.PLAYER.get())) {
          try {
            GameProfile gameProfile = Sponge.getServer()
                .getGameProfileManager()
                .get(name)
                .get();
            requestBuilder.addPlayerUuid(gameProfile.getUniqueId());
          } catch (ExecutionException e) {
            player.sendMessage(Format.error(String.format("Player %s not found", name)));
            return;
          } catch (Exception e) {
            player.sendMessage(Format.error("Error trying to access game profile for " + name));
            e.printStackTrace();
          }
        }

        args.<PrismEvent>getAll(CommandKeys.PRISM_EVENT.get()).forEach(requestBuilder::addEvent);

        DataRequest request = requestBuilder.build();
        player.sendMessage(Format.request(request));
        Optional<DataService> prism = Sponge.getServiceManager().provide(DataService.class);
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
      }).submit(SpongeGriefAlert.getSpongeInstance());

      return CommandResult.success();
    } else {
      throw Errors.playerOnlyException();
    }
  }

  /**
   * The application type, either restoration or rolling back.
   */
  public enum ApplyType {
    ROLLBACK,
    RESTORE
  }

}
