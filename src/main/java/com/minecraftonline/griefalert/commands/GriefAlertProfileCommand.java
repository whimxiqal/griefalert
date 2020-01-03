package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.DimensionType;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public class GriefAlertProfileCommand extends AbstractCommand {

  // TODO: implement

  public GriefAlertProfileCommand() {
    super(Permissions.GRIEFALERT_COMMAND_PROFILE,
        Text.of("Perform alterations to the profiles used for flagging alerts"));
    addAlias("profile");
    addAlias("p");
    addChild(new AddCommand());
    addChild(new RemoveCommand());
    addChild(new CountCommand());
    addChild(new ListCommand());
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    sendHelp(src);
    return CommandResult.success();
  }

  public static class AddCommand extends AbstractCommand {

    AddCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("Add a profile to the database"));
      addAlias("add");
      addAlias("a");
      addCommandElement(GenericArguments.flags()
          .valueFlag(GenericArguments.string(Text.of("event")), "-event", "e")
          .valueFlag(GenericArguments.string(Text.of("target")), "-target", "t")
          .valueFlag(GenericArguments.string(Text.of("dimension")), "-ignore", "i")
          .valueFlag(GenericArguments.string(Text.of("event_color")), "-event_color")
          .valueFlag(GenericArguments.string(Text.of("target_color")), "-target_color")
          .valueFlag(GenericArguments.string(Text.of("dimension_color")), "-dimension_color")
          .buildWith(GenericArguments.none()));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      DataContainer dataContainer = DataContainer.createNew();

      try {
        addStringData(args, dataContainer, GriefProfileDataQueries.EVENT, "event",
            (s) -> Registry.lookupGriefEvent(s).isPresent());
        addStringData(args, dataContainer, GriefProfileDataQueries.TARGET, "target",
            (s) -> true);
        addStringData(args, dataContainer, GriefProfileDataQueries.EVENT_COLOR, "event_color",
            (s) -> Registry.lookupTextColor(s).isPresent());
        addStringData(args, dataContainer, GriefProfileDataQueries.TARGET_COLOR, "target_color",
            (s) -> Registry.lookupTextColor(s).isPresent());
        addStringData(args, dataContainer, GriefProfileDataQueries.DIMENSION_COLOR, "dimension_color",
            (s) -> Registry.lookupTextColor(s).isPresent());
      } catch (Exception e) {
        src.sendMessage(Format.error(e.getMessage()));
        return CommandResult.empty();
      }

      Collection<Optional<String>> optionalCollection = args.getAll("dimension");

      for (Optional<String> optional : optionalCollection) {
        if (optional.isPresent()) {
          Optional<DimensionType> dimensionTypeOptional = Registry.lookupDimensionType(optional.get());
          if (dimensionTypeOptional.isPresent()) {
            dataContainer.set(DataQuery.of("ignore_" + dimensionTypeOptional.get().getId()), optional.get());
          } else {
            src.sendMessage(Format.error("Invalid dimension: " + optional.get()));
            return CommandResult.empty();
          }
        }
      }

      GriefProfile toAdd = GriefProfile.of(dataContainer);
      if (!toAdd.isValid()) {
        src.sendMessage(Format.error("It looks like you're missing some necessary components!"));
        return CommandResult.empty();
      }

      try {
        GriefAlert.getInstance().getProfileStorage().connect();
        if (GriefAlert.getInstance().getProfileStorage().write(toAdd)) {
          src.sendMessage(Format.success("GriefProfile added"));
          GriefAlert.getInstance().getProfileCabinet().reload();
          GriefAlert.getInstance().getProfileStorage().close();
          return CommandResult.success();
        } else {
          src.sendMessage(Format.error("GriefProfile addition failed. Maybe this format already exists?"));
          GriefAlert.getInstance().getProfileStorage().close();
          return CommandResult.empty();
        }
      } catch (SQLException e) {
        GriefAlert.getInstance().getLogger().error("SQLException thrown when trying to add a profile: " + toAdd.printData());
        General.printStackTraceToDebugLogger(e);
        return CommandResult.empty();
      }

    }

    private void addStringData(
        CommandContext args,
        DataContainer dataContainer,
        DataQuery query,
        String key,
        Function<String, Boolean> verifier) throws Exception {

      Optional<String> optional = args.getOne(key);

      if (optional.isPresent()) {
        if (verifier.apply(optional.get())) {
          dataContainer.set(query, optional.get());
        } else {
          throw new Exception("Invalid " + key + ": " + optional.get());
        }
      }

    }

  }

  public static class RemoveCommand extends AbstractCommand {

    RemoveCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("Remove a profile from the database"));
      addAlias("remove");
      addAlias("r");
      addCommandElement(GenericArguments.seq(
          GenericArguments.string(Text.of("event")),
          GenericArguments.string(Text.of("target"))));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      Optional<String> eventOptional = args.getOne("event");
      Optional<String> targetOptional = args.getOne("target");

      if (!eventOptional.isPresent() || !targetOptional.isPresent()) {
        src.sendMessage(Format.error("Insufficient arguments"));
      }

      Optional<GriefEvent> griefEventOptional = Registry.lookupGriefEvent(eventOptional.get());

      if (!griefEventOptional.isPresent()) {
        src.sendMessage(Format.error("Invalid event: " + eventOptional.get()));
        return CommandResult.empty();
      }

      try {
        GriefAlert.getInstance().getProfileStorage().connect();
        if (GriefAlert.getInstance().getProfileStorage().remove(griefEventOptional.get(), targetOptional.get())) {
          GriefAlert.getInstance().getProfileCabinet().reload();
          src.sendMessage(Format.success("Removed a Grief Profile"));
          GriefAlert.getInstance().getProfileStorage().close();
          return CommandResult.success();
        } else {
          src.sendMessage(Format.error("No Grief Profile was found with those parameters"));
          GriefAlert.getInstance().getProfileStorage().close();
          return CommandResult.empty();
        }
      } catch (SQLException e) {
        GriefAlert.getInstance().getLogger().error("SQLException thrown when trying to remove a profile: "
            + griefEventOptional.get().getId() + ", "
            + targetOptional.get());
        General.printStackTraceToDebugLogger(e);
        return CommandResult.empty();
      }

    }

  }

  public static class CountCommand extends AbstractCommand {

    CountCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("Count how many profiles there are in use"));
      addAlias("count");
      addAlias("c");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      src.sendMessage(Format.success(String.format(
          "There are %s Grief Profiles in use",
          GriefAlert.getInstance().getProfileCabinet().getProfiles().size())));
      return CommandResult.success();

    }

  }

  public static class ListCommand extends AbstractCommand {

    ListCommand() {
      super(Permissions.GRIEFALERT_COMMAND_PROFILE, Text.of("List every profile in use"));
      addAlias("list");
      addAlias("l");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      if (!(src instanceof ConsoleSource)) {
        src.sendMessage(Format.error("Only the console can use this command"));
        return CommandResult.empty();
      }

      ConsoleSource console = (ConsoleSource) src;

      console.sendMessage(Format.heading("=== Grief Profiles ==="));
      for (GriefProfile profile : GriefAlert.getInstance().getProfileCabinet().getProfiles()) {
        console.sendMessage(Format.bonus(profile.printData()));
      }
      return CommandResult.success();
    }

  }

}
