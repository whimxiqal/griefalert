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

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.storage.ProfileStorage;
import com.minecraftonline.griefalert.commands.common.CommandKey;
import com.minecraftonline.griefalert.commands.common.GeneralCommand;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.api.data.GriefEvents;
import com.minecraftonline.griefalert.util.enums.Permissions;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

public class ProfileCommand extends GeneralCommand {

  ProfileCommand() {
    super(Permissions.GRIEFALERT_COMMAND_PROFILE,
        Text.of("Alter the list of Profiles, which flag Alerts"));
    addAlias("profile");
    addAlias("pr");
    addChild(new AddCommand());
    addChild(new RemoveCommand());
    addChild(new EditCommand());
    addChild(new CountCommand());
    addChild(new ListCommand());
    addChild(new EventsCommand());
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
    sendHelp(src);
    return CommandResult.success();
  }

  public static class AddCommand extends GeneralCommand {

    AddCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("Add a profile to the database."));
      addAlias("add");
      addAlias("a");
      setCommandElement(GenericArguments.seq(
          GenericArguments.catalogedElement(CommandKeys.GA_EVENT.get(), GriefEvent.class),
          GenericArguments.string(CommandKeys.GA_TARGET.get()),
          GenericArguments.flags()
              .valueFlag(GenericArguments.world(
                  CommandKeys.WORLD.get()),
                  "-ignore", "i")
              .flag("t")
              .valueFlag(GenericArguments.catalogedElement(
                  CommandKeys.PROFILE_COLOR_EVENT.get(), TextColor.class),
                  "-event_color")
              .valueFlag(GenericArguments.catalogedElement(
                  CommandKeys.PROFILE_COLOR_TARGET.get(), TextColor.class),
                  "-target_color")
              .valueFlag(GenericArguments.catalogedElement(
                  CommandKeys.PROFILE_COLOR_WORLD.get(), TextColor.class),
                  "-world_color")
              .buildWith(GenericArguments.none())));
      addFlagDescription("i",
          Text.of(TextColors.AQUA, "Ignore", TextColors.RESET, " the world with the given world name"),
          true);
      addFlagDescription("t",
          Text.of("Set this profile as ", TextColors.AQUA, "translucent ", TextColors.RESET,
              "which silences alerts if GriefAlert internally thinks it was not grief"),
          false);
      addFlagDescription("-event_color",
          Text.of("Use this color on the event of the alerts with this profile"),
          true);
      addFlagDescription("-target_color",
          Text.of("Use this color on the target of the alerts with this profile"),
          true);
      addFlagDescription("-world_color",
          Text.of("Use this color on the world of the alerts with this profile"),
          true);
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {

      GriefEvent event;
      String target;
      try {
        event = args.requireOne(CommandKeys.GA_EVENT.get());
        target = General.ensureIdFormat(args.requireOne(CommandKeys.GA_TARGET.get()));
      } catch (NoSuchElementException e) {
        sendHelp(src);
        return CommandResult.success();
      }
      GriefProfile.Builder profileBuilder = GriefProfile.builder(event, target);
      args.<WorldProperties>getAll(CommandKeys.WORLD.get())
          .forEach(profileBuilder::addIgnored);
      if (args.hasAny("t")) {
        profileBuilder.setTranslucent(true);
      }
      args.<TextColor>getOne(CommandKeys.PROFILE_COLOR_EVENT.get())
          .ifPresent(color -> profileBuilder.putColored(GriefProfile.Colorable.EVENT, color));
      args.<TextColor>getOne(CommandKeys.PROFILE_COLOR_TARGET.get())
          .ifPresent(color -> profileBuilder.putColored(GriefProfile.Colorable.TARGET, color));
      args.<TextColor>getOne(CommandKeys.PROFILE_COLOR_WORLD.get())
          .ifPresent(color -> profileBuilder.putColored(GriefProfile.Colorable.WORLD, color));
      GriefProfile profile = profileBuilder.build();
      try {
        if (GriefAlert.getInstance().getProfileStorage().write(profile)) {
          src.sendMessage(Format.success("GriefProfile added"));
          GriefAlert.getInstance().getProfileCache().reload();
          return CommandResult.success();
        } else {
          src.sendMessage(Format.error("GriefProfile addition failed. "
              + "Maybe this format already exists?"));
          return CommandResult.empty();
        }
      } catch (Exception e) {
        GriefAlert.getInstance().getLogger().error("An Exception thrown when trying to "
            + "add a profile: "
            + Format.profile(profile).toPlain());
        General.printStackTraceToDebugLogger(e);
        return CommandResult.empty();
      }

    }

  }

  public static class EditCommand extends GeneralCommand {

    EditCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("Edit a profile in the database."));
      addAlias("edit");
      addAlias("e");
      setCommandElement(GenericArguments.seq(
          GenericArguments.catalogedElement(CommandKeys.GA_EVENT.get(), GriefEvent.class),
          GenericArguments.string(CommandKeys.GA_TARGET.get()),
          GenericArguments.flags()
              .valueFlag(GenericArguments.world(
                  CommandKeys.WORLD.get()),
                  "-ignore", "i")
              .flag("t")
              .valueFlag(GenericArguments.catalogedElement(
                  CommandKeys.PROFILE_COLOR_EVENT.get(), TextColor.class),
                  "-event_color")
              .valueFlag(GenericArguments.catalogedElement(
                  CommandKeys.PROFILE_COLOR_TARGET.get(), TextColor.class),
                  "-target_color")
              .valueFlag(GenericArguments.catalogedElement(
                  CommandKeys.PROFILE_COLOR_WORLD.get(), TextColor.class),
                  "-world_color")
              .buildWith(GenericArguments.none())));
      addFlagDescription("i",
          Text.of(TextColors.AQUA, "Ignore", TextColors.RESET, " the world with the given world name"),
          true);
      addFlagDescription("t",
          Text.of("Set this profile as ", TextColors.AQUA, "translucent ", TextColors.RESET,
              "which silences alerts if GriefAlert internally thinks it was not grief"),
          false);
      addFlagDescription("-event_color",
          Text.of("Use this color on the event of the alerts with this profile"),
          true);
      addFlagDescription("-target_color",
          Text.of("Use this color on the target of the alerts with this profile"),
          true);
      addFlagDescription("-world_color",
          Text.of("Use this color on the world of the alerts with this profile"),
          true);
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {

      GriefEvent event;
      String target;
      try {
        event = args.requireOne(CommandKeys.GA_EVENT.get());
        target = General.ensureIdFormat(args.requireOne(CommandKeys.GA_TARGET.get()));
      } catch (NoSuchElementException e) {
        sendHelp(src);
        return CommandResult.success();
      }
      GriefProfile oldProfile;
      try {
        oldProfile = GriefAlert.getInstance().getProfileStorage().get(event, target);
      } catch (Exception e) {
        e.printStackTrace();
        src.sendMessage(Format.error("An error occurred!"));
        return CommandResult.empty();
      }
      if (oldProfile == null) {
        src.sendMessage(Format.error("That profile does not exist yet."));
        return CommandResult.empty();
      }

      GriefProfile.Builder profileBuilder = GriefProfile.builder(event, target);

      // Toggle worlds based on old ignored worlds
      Set<WorldProperties> specifiedWorlds = new HashSet<>(args.getAll(CommandKeys.WORLD.get()));
      oldProfile.getIgnored().stream().map(World::getProperties).forEach(specifiedWorlds::remove);
      specifiedWorlds.forEach(profileBuilder::addIgnored);

      // Toggle translucence
      profileBuilder.setTranslucent(oldProfile.isTranslucent() ^ args.hasAny("t"));

      // Set new colors
      oldProfile.getAllColored().forEach(profileBuilder::putColored);
      args.<TextColor>getOne(CommandKeys.PROFILE_COLOR_EVENT.get())
          .ifPresent(color -> profileBuilder.putColored(GriefProfile.Colorable.EVENT, color));
      args.<TextColor>getOne(CommandKeys.PROFILE_COLOR_TARGET.get())
          .ifPresent(color -> profileBuilder.putColored(GriefProfile.Colorable.TARGET, color));
      args.<TextColor>getOne(CommandKeys.PROFILE_COLOR_WORLD.get())
          .ifPresent(color -> profileBuilder.putColored(GriefProfile.Colorable.WORLD, color));
      GriefProfile profile = profileBuilder.build();

      ProfileStorage profileStorage = GriefAlert.getInstance().getProfileStorage();
      try {
        if (!profileStorage.remove(profile.getGriefEvent(), profile.getTarget())) {
          src.sendMessage(Format.error("Could not remove the old version"));
          return CommandResult.empty();
        }
        if (profileStorage.write(profile)) {
          src.sendMessage(Format.success("GriefProfile edited"));
          GriefAlert.getInstance().getProfileCache().reload();
          return CommandResult.success();
        } else {
          src.sendMessage(Format.error("GriefProfile editing failed"));
          // Add back the old one because this one failed
          if (!profileStorage.write(oldProfile)) {
            src.sendMessage(Format.error("Also, the old version could not be reestablished! Please recreate this profile."));
          }
          return CommandResult.empty();
        }
      } catch (Exception e) {
        GriefAlert.getInstance().getLogger().error("An Exception thrown when trying to "
            + "add a profile: "
            + Format.profile(profile).toPlain());
        General.printStackTraceToDebugLogger(e);
        return CommandResult.empty();
      }

    }

  }


  public static class RemoveCommand extends GeneralCommand {

    RemoveCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("Remove a profile from the database"));
      addAlias("remove");
      addAlias("r");
      setCommandElement(GenericArguments.seq(
          GenericArguments.catalogedElement(CommandKeys.GA_EVENT.get(), GriefEvent.class),
          GenericArguments.string(CommandKeys.GA_TARGET.get())));
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {

      GriefEvent event = args.<GriefEvent>getOne(CommandKeys.GA_EVENT.get())
          .orElseThrow(() -> new RuntimeException("Requires argument"));
      String target = args.<String>getOne(CommandKeys.GA_TARGET.get())
          .map(General::ensureIdFormat)
          .orElseThrow(() -> new RuntimeException("Requires argument"));

      try {
        if (GriefAlert.getInstance().getProfileStorage().remove(event, target)) {
          GriefAlert.getInstance().getProfileCache().reload();
          src.sendMessage(Format.success("Removed a Grief Profile"));
          return CommandResult.success();
        } else {
          src.sendMessage(Format.error("No Grief Profile was found with those parameters"));
          return CommandResult.empty();
        }
      } catch (Exception e) {
        GriefAlert.getInstance().getLogger().error("An Exception was thrown when trying to "
            + "remove a profile: "
            + event.getId() + ", "
            + target);
        General.printStackTraceToDebugLogger(e);
        return CommandResult.empty();
      }

    }

  }

  public static class CountCommand extends GeneralCommand {

    CountCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of(
          "Count how many profiles there are in use"));
      addAlias("count");
      addAlias("c");
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {

      src.sendMessage(Format.success(String.format(
          "There are %s Grief Profiles in use",
          GriefAlert.getInstance().getProfileCache().getProfiles().size())));
      return CommandResult.success();

    }

  }

  public static class ListCommand extends GeneralCommand {

    ListCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("List every profile in use"));
      addAlias("list");
      addAlias("l");
      setCommandElement(GenericArguments.flags()
          .valueFlag(
              GenericArguments.catalogedElement(CommandKeys.GA_EVENT.get(), GriefEvent.class),
              "e")
          .valueFlag(
              GenericArguments.string(CommandKeys.TARGET.get()),
              "t")
          .valueFlag(
              GenericArguments.world(CommandKeys.WORLD.get()),
              "w")
          .flag("-colored", "c")
          .buildWith(
              GenericArguments.none()));
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {

      List<Text> elements = GriefAlert.getInstance()
          .getProfileCache()
          .getProfiles()
          .stream()
          .filter(profile -> args.<GriefEvent>getOne(CommandKeys.GA_EVENT.get())
              .map(event -> profile.getGriefEvent()
                  .getId().toLowerCase().contains(event.getId().toLowerCase()))
              .orElse(true))
          .filter(profile -> args.<String>getOne(CommandKeys.TARGET.get())
              .map(target -> profile.getTarget()
                  .toLowerCase().contains(target.toLowerCase()))
              .orElse(true))
          .filter(profile -> {
            Collection<WorldProperties> worlds = args.getAll(CommandKeys.WORLD.get());
            return worlds.isEmpty()
                || worlds.stream().anyMatch(world -> !profile.isIgnoredIn(world));
          })
          .filter(profile -> !args.hasAny("colored") || profile.isColored())
          .map(profile -> Text.of(
              Format.GRIEF_ALERT_THEME,
              " - ",
              TextColors.RESET, Format.profile(profile)))
          .collect(Collectors.toList());

      if (elements.isEmpty()) {
        src.sendMessage(Format.info("No profiles found with those parameters"));
      } else if (src instanceof ConsoleSource) {
        ConsoleSource console = (ConsoleSource) src;
        console.sendMessage(Format.heading("=== Grief Profiles ==="));
        elements.forEach(console::sendMessage);
      } else {
        PaginationList.builder()
            .title(Text.of(TextColors.YELLOW, "Grief Profiles"))
            .padding(Text.of(TextColors.DARK_GRAY, "="))
            .contents(elements)
            .build()
            .sendTo(src);
      }
      return CommandResult.success();
    }
  }

  public static class EventsCommand extends GeneralCommand {

    EventsCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("List all events used by GriefAlert"));
      addAlias("events");
    }

    @Nonnull
    @Override
    public CommandResult execute(CommandSource src, @Nonnull CommandContext args) {
      src.sendMessage(Format.info(
          "GriefAlert Events: ",
          Text.joinWith(
              Format.bonus(", "),
              GriefEvents.REGISTRY_MODULE.getAll()
                  .stream()
                  .map(griefEvent ->
                      Format.hover(griefEvent.getId(), griefEvent.getDescription()))
                  .collect(Collectors.toList()))));
      return CommandResult.success();
    }
  }

}
