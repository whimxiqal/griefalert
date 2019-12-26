//package com.minecraftonline.griefalert.commands;
//
//import com.minecraftonline.griefalert.GriefAlert;
//import com.minecraftonline.griefalert.api.profiles.GriefProfile;
//import com.minecraftonline.griefalert.profiles.GriefProfileBuilderManager;
//import com.minecraftonline.griefalert.util.General;
//
//import java.io.IOException;
//
//import com.minecraftonline.griefalert.util.Permissions;
//import org.spongepowered.api.command.CommandException;
//import org.spongepowered.api.command.CommandResult;
//import org.spongepowered.api.command.CommandSource;
//import org.spongepowered.api.command.args.CommandContext;
//import org.spongepowered.api.command.args.GenericArguments;
//import org.spongepowered.api.entity.living.player.Player;
//import org.spongepowered.api.text.Text;
//import org.spongepowered.api.text.format.TextColors;
//import org.spongepowered.api.util.annotation.NonnullByDefault;
//import org.spongepowered.api.world.DimensionTypes;
//
//public class GriefAlertBuilderCommand extends AbstractCommand {
//
//  private GriefProfileBuilderManager builderManager;
//
//  GriefAlertBuilderCommand() {
//    super(Permissions.GRIEFALERT_COMMAND_BUILD, Text.of("Build a grief profile"));
//
//    builderManager = new GriefProfileBuilderManager();
//
//    addAlias("build");
//
//    // Add 'Save'
//    addChild(new AbstractCommand(
//        Permissions.GRIEFALERT_COMMAND_BUILD,
//        Text.of("Save your alert profile"), "save") {
//      @Override
//      @NonnullByDefault
//      public CommandResult execute(@NonnullByDefault CommandSource src,
//                                   @NonnullByDefault CommandContext args) throws CommandException {
//        if (src instanceof Player) {
//          Player player = (Player) src;
//          if (builderManager.getBuilderPair(player.getUniqueId()).isPresent()) {
//            try {
//              GriefProfile griefProfile = builderManager.getBuilderPair(player.getUniqueId()).get().build();
//              if (GriefAlert.getInstance().getMuseum().add(griefProfile)) {
//                player.sendMessage(Text.of(TextColors.GREEN, "Your profile has been added"));
//                GriefAlert.getInstance().getMuseum().store(griefProfile);
//              } else {
//                player.sendMessage(Text.of(
//                    TextColors.RED,
//                    "Your profile was not added. "
//                        + "There seems to be a profile similar to this one already saved.")
//                );
//              }
//            } catch (IOException e) {
//              player.sendMessage(Text.of(
//                  TextColors.RED,
//                  "Your profile could not be saved to storage")
//              );
//              e.printStackTrace();
//            }
//          } else {
//            player.sendMessage(Text.of(
//                TextColors.RED,
//                "You must be in builder mode to save a profile.")
//            );
//          }
//        }
//        return CommandResult.success();
//      }
//    });
//    // Add 'Toggle'
//    addChild(new AbstractCommand(
//        Permissions.GRIEFALERT_COMMAND_BUILD,
//        Text.of("Toogle build mode"), "toggle") {
//      @Override
//      @NonnullByDefault
//      public CommandResult execute(@NonnullByDefault CommandSource src,
//                                   @NonnullByDefault CommandContext args) throws CommandException {
//        if (src instanceof Player) {
//          Player player = (Player) src;
//          builderManager.setBuildingState(
//              player,
//              !builderManager.getBuilderPair(player).isPresent()
//          );
//          if (builderManager.getBuilderPair(player).isPresent()) {
//            player.sendMessage(Text.of(TextColors.GREEN, "You are now in Add Profile mode."));
//          } else {
//            player.sendMessage(Text.of(TextColors.GREEN, "You are no longer in Add Profile mode."));
//          }
//        }
//        return CommandResult.success();
//      }
//    });
//    addCommandElement(GenericArguments.flags()
//        .flag("s").flag("-stealthy")
//        .flag("-ignore-overworld")
//        .flag("-ignore-nether")
//        .flag("-ignore-the-end")
//        .valueFlag(GenericArguments.string(Text.of("color")), "c")
//        .buildWith(GenericArguments.none())
//    );
//  }
//
//  @Override
//  @NonnullByDefault
//  public CommandResult execute(@NonnullByDefault CommandSource src,
//                               @NonnullByDefault CommandContext args) throws CommandException {
//    if (src instanceof Player) {
//      Player player = (Player) src;
//      if (builderManager.setBuildingState(player, true)) {
//        player.sendMessage(Text.of(TextColors.GREEN, "You are now in Add Profile mode."));
//      }
//      if (builderManager.getBuilderPair(player).isPresent()) {
//        GriefProfile.Builder builder = builderManager.getBuilderPair(player).get();
//        if (args.hasAny("s") || args.<String>getOne("stealthy").isPresent()) {
//          builder.toggleStealthy();
//        }
//        if (args.<String>getOne("color").isPresent()) {
//          try {
//            builder.setAlertColor(General.stringToColor(args.<String>getOne("color").get()));
//          } catch (General.IllegalColorCodeException e) {
//            player.sendMessage(Text.of(TextColors.RED, args.<String>getOne("color").get()
//                + " is not a valid color"));
//          }
//        }
//        if (args.<String>getOne("ignore-overworld").isPresent()) {
//          builder.ignoreDimensionToggle(DimensionTypes.OVERWORLD);
//        }
//        if (args.<String>getOne("ignore-nether").isPresent()) {
//          builder.ignoreDimensionToggle(DimensionTypes.NETHER);
//        }
//        if (args.<String>getOne("ignore-the-end").isPresent()) {
//          builder.ignoreDimensionToggle(DimensionTypes.THE_END);
//        }
//        player.sendMessage(builder.print());
//      }
//    }
//    return CommandResult.success();
//  }
//}
