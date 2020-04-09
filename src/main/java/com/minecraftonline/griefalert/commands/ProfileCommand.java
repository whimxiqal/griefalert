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
import com.minecraftonline.griefalert.api.commands.GeneralCommand;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.util.enums.GriefEvents;
import com.minecraftonline.griefalert.util.enums.Permissions;

import java.util.Collection;
import java.util.List;
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
import org.spongepowered.api.world.DimensionType;

public class ProfileCommand extends GeneralCommand {

  ProfileCommand() {
    super(Permissions.GRIEFALERT_COMMAND_PROFILE,
        Text.of("Alter the list of Profiles, which flag Alerts"));
    addAlias("profile");
    addAlias("p");
    addChild(new AddCommand());
    addChild(new RemoveCommand());
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
              .valueFlag(GenericArguments.dimension(
                  CommandKeys.DIMENSION.get()),
                  "-ignore", "i")
              .valueFlag(GenericArguments.catalogedElement(
                  CommandKeys.PROFILE_COLOR_EVENT.get(), TextColor.class),
                  "-event_color")
              .valueFlag(GenericArguments.catalogedElement(
                  CommandKeys.PROFILE_COLOR_TARGET.get(), TextColor.class),
                  "-target_color")
              .valueFlag(GenericArguments.catalogedElement(
                  CommandKeys.DIMENSION.get(), TextColor.class),
                  "-dimension_color")
              .buildWith(GenericArguments.none())));
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {

      GriefEvent event = args.<GriefEvent>getOne(CommandKeys.GA_EVENT.get())
          .orElseThrow(() -> new RuntimeException("Requires argument"));
      String target = args.<String>getOne(CommandKeys.GA_TARGET.get())
          .map(General::ensureIdFormat)
          .orElseThrow(() -> new RuntimeException("Requires argument"));
      GriefProfile profile = GriefProfile.of(event, target);

      args.<DimensionType>getAll(CommandKeys.DIMENSION.get())
          .forEach(profile::addIgnored);

      args.<TextColor>getOne(CommandKeys.PROFILE_COLOR_EVENT.get())
          .ifPresent(color -> profile.putColored(GriefProfile.Colored.EVENT, color));

      args.<TextColor>getOne(CommandKeys.PROFILE_COLOR_TARGET.get())
          .ifPresent(color -> profile.putColored(GriefProfile.Colored.TARGET, color));

      args.<TextColor>getOne(CommandKeys.PROFILE_COLOR_DIMENSION.get())
          .ifPresent(color -> profile.putColored(GriefProfile.Colored.DIMENSION, color));

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
            + profile.print().toPlain());
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
              GenericArguments.dimension(CommandKeys.DIMENSION.get()),
              "d")
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
          .filter(profile -> args.<GriefEvent>getOne("event")
              .map(event -> profile.getGriefEvent()
                  .getId().toLowerCase().contains(event.getId().toLowerCase()))
              .orElse(true))
          .filter(profile -> args.<String>getOne("target")
              .map(target -> profile.getTarget()
                  .toLowerCase().contains(target.toLowerCase()))
              .orElse(true))
          .filter(profile -> {
            Collection<DimensionType> dimensions = args.getAll("dimension");
            return dimensions.isEmpty()
                || dimensions.stream().anyMatch(dimension -> !profile.isIgnoredIn(dimension));
          })
          .filter(profile -> !args.hasAny("colored") || profile.isColored())
          .map(profile -> Text.of(
              Format.GRIEF_ALERT_THEME,
              " - ",
              TextColors.RESET, profile.print()))
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
