/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.enums.GriefEvents;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import com.minecraftonline.griefalert.util.enums.Permissions;

import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

public class GriefAlertProfileCommand extends AbstractCommand {

  GriefAlertProfileCommand() {
    super(Permissions.GRIEFALERT_COMMAND_PROFILE,
        Text.of("Perform alterations to the profiles used for flagging alerts. Event types: ",
    Text.joinWith(
        Format.bonus(", "),
        GriefEvents.REGISTRY_MODULE.getAll()
            .stream()
            .map(griefEvent ->
                Format.hover(griefEvent.getId(), griefEvent.getDescription()))
            .collect(Collectors.toList()))));
    addAlias("profile");
    addAlias("p");
    addChild(new AddCommand());
    addChild(new RemoveCommand());
    addChild(new CountCommand());
    addChild(new ListCommand());
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
    sendHelp(src);
    return CommandResult.success();
  }

  public static class AddCommand extends AbstractCommand {

    AddCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("Add a profile to the database."));
      addAlias("add");
      addAlias("a");
      setCommandElement(GenericArguments.seq(
          GenericArguments.catalogedElement(Text.of("event"), GriefEvent.class),
          GenericArguments.string(Text.of("target")),
          GenericArguments.flags()
              .valueFlag(GenericArguments.dimension(
                  Text.of("dimension")),
                  "-ignore", "i")
              .valueFlag(GenericArguments.catalogedElement(
                  Text.of("event_color"), TextColor.class),
                  "-event_color")
              .valueFlag(GenericArguments.catalogedElement(
                  Text.of("target_color"), TextColor.class),
                  "-target_color")
              .valueFlag(GenericArguments.catalogedElement(
                  Text.of("dimension_color"), TextColor.class),
                  "-dimension_color")
              .buildWith(GenericArguments.none())));
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {

      GriefEvent event = ((GriefEvent) args.getOne("event").get());
      String target = args.<String>getOne("target").map(General::ensureIdFormat).get();
      GriefProfile profile = GriefProfile.of(event, target);

      args.<TextColor>getOne("event_color")
          .ifPresent(color -> profile.putColored(GriefProfile.Colored.EVENT, color));

      args.<TextColor>getOne("target_color")
          .ifPresent(color -> profile.putColored(GriefProfile.Colored.TARGET, color));

      args.<TextColor>getOne("dimension_color")
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

  public static class RemoveCommand extends AbstractCommand {

    RemoveCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("Remove a profile from the database"));
      addAlias("remove");
      addAlias("r");
      setCommandElement(GenericArguments.seq(
          GenericArguments.catalogedElement(Text.of("event"), GriefEvent.class),
          GenericArguments.string(Text.of("target"))));
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {

      GriefEvent event = (GriefEvent) args.getOne("event").get();
      String target = args.getOne("target").map((s) -> General.ensureIdFormat((String) s)).get();

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

  public static class CountCommand extends AbstractCommand {

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

  public static class ListCommand extends AbstractCommand {

    ListCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("List every profile in use"));
      addAlias("list");
      addAlias("l");
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) {
      if (src instanceof ConsoleSource) {

        ConsoleSource console = (ConsoleSource) src;
        console.sendMessage(Format.heading("=== Grief Profiles ==="));
        for (GriefProfile profile : GriefAlert.getInstance().getProfileCache().getProfiles()) {
          console.sendMessage(profile.print());
        }
      } else {
        PaginationList.builder()
            .header(Text.of(TextColors.YELLOW, "Grief Profiles"))
            .padding(Text.of(TextColors.GRAY, "="))
            .contents(GriefAlert.getInstance()
                .getProfileCache()
                .getProfiles()
                .stream()
                .map(GriefProfile::print)
                .collect(Collectors.toList()))
            .footer(Text.of(TextColors.YELLOW, "Formatting for list command is in progress"))
            .build()
            .sendTo(src);
      }
      return CommandResult.success();
    }
  }

}
