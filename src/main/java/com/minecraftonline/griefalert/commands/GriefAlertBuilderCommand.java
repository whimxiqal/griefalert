package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.profiles.GriefProfile;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;

import java.io.IOException;

public class GriefAlertBuilderCommand extends AbstractCommand {


  public GriefAlertBuilderCommand(GriefAlert plugin) {
    super(plugin, GriefAlert.Permission.GRIEFALERT_COMMAND_BUILD, Text.of("Build a grief profile"));
    addAlias("build");

    // Add 'Save'
    addChild(new AbstractCommand(plugin, GriefAlert.Permission.GRIEFALERT_COMMAND_BUILD, Text.of("Save your alert profile"), "save") {
      @Override
      public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
          Player player = (Player) src;
          if (plugin.getMuseum().getProfileBuilder(player).isPresent()) {
            try {
              if (plugin.getMuseum().getProfileBuilder(player).get().solidify(plugin)) {
                player.sendMessage(Text.of(TextColors.GREEN, "Your profile has been added"));
              } else {
                player.sendMessage(Text.of(TextColors.RED, "Your profile was not added. There seems to be a profile similar to this one already saved."));
              }
            } catch (IOException e) {
              player.sendMessage(Text.of(TextColors.RED, "Your profile could not be saved to storage"));
              e.printStackTrace();
            }
          } else {
            player.sendMessage(Text.of(TextColors.RED, "You must be in builder mode to save a profile."));
          }
        }
        return CommandResult.success();
      }
    });
    // Add 'Toggle'
    addChild(new AbstractCommand(plugin, GriefAlert.Permission.GRIEFALERT_COMMAND_BUILD, Text.of("Toogle build mode"), "toggle") {
      @Override
      public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
          Player player = (Player) src;
          plugin.getMuseum().setBuildingState(player, !plugin.getMuseum().getProfileBuilder(player).isPresent());
          if (plugin.getMuseum().getProfileBuilder(player).isPresent()) {
            player.sendMessage(Text.of(TextColors.GREEN, "You are now in Add Profile mode."));
          } else {
            player.sendMessage(Text.of(TextColors.GREEN, "You are no longer in Add Profile mode."));
          }
        }
        return CommandResult.success();
      }
    });
    addCommandElement(GenericArguments.flags()
        .flag("d").flag("-deny")
        .flag("s").flag("-stealthy")
        .flag("-ignore-overworld")
        .flag("-ignore-nether")
        .flag("-ignore-the-end")
        .buildWith(GenericArguments.none())
    );
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;
      if ((args.hasAny("d")) || (args.hasAny("-deny"))) {
        if (plugin.getMuseum().setBuildingState(player, true)) {
          player.sendMessage(Text.of(TextColors.GREEN, "You are now in Add Profile mode."));
          if (plugin.getMuseum().getProfileBuilder(player).isPresent()) {
            plugin.getMuseum().getProfileBuilder(player).get().setDenied(true);
            player.sendMessage(plugin.getMuseum().getProfileBuilder(player).get().print());
          }
        }
      }
      if ((args.hasAny("s")) || (args.hasAny("-stealthy"))) {
        if (plugin.getMuseum().setBuildingState(player, true)) {
          player.sendMessage(Text.of(TextColors.GREEN, "You are now in Add Profile mode."));
        }
        if (plugin.getMuseum().getProfileBuilder(player).isPresent()) {
          plugin.getMuseum().getProfileBuilder(player).get().setStealthy(true);
          player.sendMessage(plugin.getMuseum().getProfileBuilder(player).get().print());
        }
      }
      if (args.hasAny("-ignore-overworld")) {
        if (plugin.getMuseum().setBuildingState(player, true)) {
          player.sendMessage(Text.of(TextColors.GREEN, "You are now in Add Profile mode."));
        }
        if (plugin.getMuseum().getProfileBuilder(player).isPresent()) {
          plugin.getMuseum().getProfileBuilder(player).get().getDimensionParameterArray().setIgnored(DimensionTypes.OVERWORLD, true);
          player.sendMessage(plugin.getMuseum().getProfileBuilder(player).get().print());
        }
      }
      if (args.hasAny("-ignore-nether")) {
        if (plugin.getMuseum().setBuildingState(player, true)) {
          player.sendMessage(Text.of(TextColors.GREEN, "You are now in Add Profile mode."));
        }
        if (plugin.getMuseum().getProfileBuilder(player).isPresent()) {
          plugin.getMuseum().getProfileBuilder(player).get().getDimensionParameterArray().setIgnored(DimensionTypes.NETHER, true);
          player.sendMessage(plugin.getMuseum().getProfileBuilder(player).get().print());
        }
      }
      if (args.hasAny("-ignore-the-end")) {
        if (plugin.getMuseum().setBuildingState(player, true)) {
          player.sendMessage(Text.of(TextColors.GREEN, "You are now in Add Profile mode."));
        }
        if (plugin.getMuseum().getProfileBuilder(player).isPresent()) {
          plugin.getMuseum().getProfileBuilder(player).get().getDimensionParameterArray().setIgnored(DimensionTypes.THE_END, true);
          player.sendMessage(plugin.getMuseum().getProfileBuilder(player).get().print());
        }
      }
    }
    return CommandResult.success();
  }

  public static class GriefProfileBuilder {
    /**
     * String representation of the griefed object.
     */
    private String griefedId = "";
    /**
     * TextColor representation of the color in which an alert will appear.
     */
    private TextColor alertColor = TextColors.RED;
    /**
     * Denotes whether this action will be cancelled upon triggering.
     */
    private boolean denied = false;
    /**
     * Denotes whether this is muted from alerting.
     */
    private boolean stealthy = false;
    /**
     * The type of grief.
     */
    private GriefAlert.GriefType type = GriefAlert.GriefType.DESTROY;

    /**
     * The wrapper for information regarding whether a dimension is marked for this grief event.
     */
    private GriefProfile.DimensionParameterArray dimensionParameterArray = new GriefProfile.DimensionParameterArray();

    public void setAlertColor(TextColor alertColor) {
      this.alertColor = alertColor;
    }

    public void setDenied(boolean denied) {
      this.denied = denied;
    }

    public GriefProfile.DimensionParameterArray getDimensionParameterArray() {
      return this.dimensionParameterArray;
    }

    public void setGriefedId(String griefedId) {
      this.griefedId = griefedId;
    }

    public void setStealthy(boolean stealthy) {
      this.stealthy = stealthy;
    }

    public void setType(GriefAlert.GriefType type) {
      this.type = type;
    }

    public boolean solidify(GriefAlert plugin) throws IOException {
      GriefProfile candidate = new GriefProfile(
          type,
          griefedId,
          alertColor,
          denied,
          stealthy,
          dimensionParameterArray
      );
      if (!plugin.getMuseum().contains(candidate)) {
        plugin.getMuseum().add(candidate);
        plugin.getMuseum().store(candidate);
        return true;
      } else {
        return false;
      }
    }

    public Text print() {
      return Text.of(
          TextColors.YELLOW, "Grief Profile Builder",
          TextColors.AQUA, "\nGrief Type: ", TextColors.WHITE, type.getName(),
          TextColors.AQUA, "\nObject: ", TextColors.WHITE, griefedId,
          TextColors.AQUA, "\nAlert Color: ", TextColors.WHITE, alertColor.getName(),
          TextColors.AQUA, "\nIgnored Dimensions: ", TextColors.WHITE, String.join(", ", dimensionParameterArray.getIgnoredList()),
          TextColors.AQUA, "\nIs Denied? ", TextColors.WHITE, denied,
          TextColors.AQUA, "\nIs Stealthy?", TextColors.WHITE, stealthy
      );
    }

  }
}
