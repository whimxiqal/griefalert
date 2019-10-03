package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.profiles.EventWrapper;
import com.minecraftonline.griefalert.griefevents.profiles.GriefProfile;
import com.minecraftonline.griefalert.tools.General;

import java.io.IOException;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.DimensionTypes;

public class GriefAlertBuilderCommand extends AbstractCommand {

  GriefAlertBuilderCommand(GriefAlert plugin) {
    super(plugin, GriefAlert.Permission.GRIEFALERT_COMMAND_BUILD, Text.of("Build a grief profile"));
    addAlias("build");

    // Add 'Save'
    addChild(new AbstractCommand(
        plugin,
        GriefAlert.Permission.GRIEFALERT_COMMAND_BUILD,
        Text.of("Save your alert profile"), "save") {
      @Override
      @NonnullByDefault
      public CommandResult execute(@NonnullByDefault CommandSource src,
                                   @NonnullByDefault CommandContext args) throws CommandException {
        if (src instanceof Player) {
          Player player = (Player) src;
          if (plugin.getMuseum().getProfileBuilder(player).isPresent()) {
            try {
              if (plugin.getMuseum().getProfileBuilder(player).get().solidify(plugin)) {
                player.sendMessage(Text.of(TextColors.GREEN, "Your profile has been added"));
              } else {
                player.sendMessage(Text.of(
                    TextColors.RED,
                    "Your profile was not added. "
                        + "There seems to be a profile similar to this one already saved.")
                );
              }
            } catch (IOException e) {
              player.sendMessage(Text.of(
                  TextColors.RED,
                  "Your profile could not be saved to storage")
              );
              e.printStackTrace();
            }
          } else {
            player.sendMessage(Text.of(
                TextColors.RED,
                "You must be in builder mode to save a profile.")
            );
          }
        }
        return CommandResult.success();
      }
    });
    // Add 'Toggle'
    addChild(new AbstractCommand(
        plugin,
        GriefAlert.Permission.GRIEFALERT_COMMAND_BUILD,
        Text.of("Toogle build mode"), "toggle") {
      @Override
      @NonnullByDefault
      public CommandResult execute(@NonnullByDefault CommandSource src,
                                   @NonnullByDefault CommandContext args) throws CommandException {
        if (src instanceof Player) {
          Player player = (Player) src;
          plugin.getMuseum().setBuildingState(
              player,
              !plugin.getMuseum().getProfileBuilder(player).isPresent()
          );
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
        .valueFlag(GenericArguments.string(Text.of("color")), "c")
        .buildWith(GenericArguments.none())
    );
  }

  @Override
  @NonnullByDefault
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;
      if (plugin.getMuseum().setBuildingState(player, true)) {
        player.sendMessage(Text.of(TextColors.GREEN, "You are now in Add Profile mode."));
      }
      if (plugin.getMuseum().getProfileBuilder(player).isPresent()) {
        GriefProfileBuilder builder = plugin.getMuseum().getProfileBuilder(player).get();
        if (args.hasAny("d") || args.<String>getOne("deny").isPresent()) {
          builder.setDenied(!builder.denied);
        }
        if (args.hasAny("s") || args.<String>getOne("stealthy").isPresent()) {
          builder.setStealthy(!builder.stealthy);
        }
        if (args.<String>getOne("color").isPresent()) {
          try {
            builder.setAlertColor(General.stringToColor(args.<String>getOne("color").get()));
          } catch (General.IllegalColorCodeException e) {
            player.sendMessage(Text.of(TextColors.RED, args.<String>getOne("color").get()
                + " is not a valid color"));
          }
        }
        if (args.<String>getOne("ignore-overworld").isPresent()) {
          builder.getDimensionParameterArray().toggleIgnored(DimensionTypes.OVERWORLD);
        }
        if (args.<String>getOne("ignore-nether").isPresent()) {
          builder.getDimensionParameterArray().toggleIgnored(DimensionTypes.NETHER);
        }
        if (args.<String>getOne("ignore-the-end").isPresent()) {
          builder.getDimensionParameterArray().toggleIgnored(DimensionTypes.THE_END);
        }
        player.sendMessage(builder.print());
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
    private GriefProfile.DimensionParameterArray dimensionParameterArray =
        new GriefProfile.DimensionParameterArray();

    void setAlertColor(TextColor alertColor) {
      this.alertColor = alertColor;
    }

    void setDenied(boolean denied) {
      this.denied = denied;
    }

    GriefProfile.DimensionParameterArray getDimensionParameterArray() {
      return this.dimensionParameterArray;
    }

    public void setGriefedId(String griefedId) {
      this.griefedId = griefedId;
    }

    void setStealthy(boolean stealthy) {
      this.stealthy = stealthy;
    }

    public void setType(GriefAlert.GriefType type) {
      this.type = type;
    }

    boolean solidify(GriefAlert plugin) throws IOException {
      GriefProfile candidate = new GriefProfile(
          type,
          griefedId,
          alertColor,
          denied,
          stealthy,
          dimensionParameterArray
      );
      if (!plugin.getMuseum().containsSimilar(candidate)) {
        plugin.getMuseum().add(candidate);
        plugin.getMuseum().store(candidate);
        return true;
      } else {
        return false;
      }
    }

    Text print() {
      return Text.of(
          TextColors.GOLD, TextStyles.ITALIC, "Grief Profile Builder",
          TextColors.AQUA, "\nGrief Type: ", TextColors.WHITE, type.getName(),
          TextColors.AQUA, "\nObject: ", TextColors.WHITE, griefedId.replaceAll("[a-zA-Z]*:", ""),
          TextColors.AQUA, "\nAlert Color: ", TextColors.WHITE, alertColor.getName(),
          TextColors.AQUA, "\nIgnored Dimensions: ", TextColors.WHITE, String.join(
              ", ",
              dimensionParameterArray.getIgnoredList()
          ),
          TextColors.AQUA, "\nIs Denied? ", TextColors.WHITE, denied,
          TextColors.AQUA, "\nIs Stealthy?", TextColors.WHITE, stealthy
      );
    }

    /**
     * Use all useful information from the Event Wrapper to build a more
     * accurate Grief Profile.
     *
     * @param eventWrapper The wrapper for the Sponge event
     * @param player       The player building a Grief Profile
     */
    public void incorporate(EventWrapper eventWrapper, Player player) {
      boolean changed = false;
      if (!this.type.equals(eventWrapper.getType())) {
        this.type = eventWrapper.getType();
        changed = true;
      }
      if (!this.griefedId.equals(eventWrapper.getGriefedId())) {
        this.griefedId = eventWrapper.getGriefedId();
        changed = true;
      }
      if (changed) {
        player.sendMessage(print());
      }
    }
  }
}
