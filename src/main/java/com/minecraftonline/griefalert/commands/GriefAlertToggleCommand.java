package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class GriefAlertToggleCommand extends AbstractCommand {


  public GriefAlertToggleCommand(GriefAlert plugin) {
    super(plugin, GriefAlert.Permission.GRIEFALERT_COMMAND_ADDPROFILE, Text.of("Toggle various administrative states"));
    addAlias("toggle");
    addChild(new GriefAlertToggleAddprofileCommand(plugin));
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    sendHelp(src);
    return CommandResult.success();
  }

  public static class GriefAlertToggleAddprofileCommand extends AbstractCommand {

    public GriefAlertToggleAddprofileCommand(GriefAlert plugin) {
      super(plugin, GriefAlert.Permission.GRIEFALERT_COMMAND_ADDPROFILE, Text.of("Toggle grief alert abilities"));
      addAlias("addprofile");
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        Player player = (Player) src;
        if (plugin.getProfileBuilder(player).isPresent()) {
          plugin.removeProfileBuilder(player);
          player.sendMessage(Text.of(TextColors.GREEN, "You are no longer in Add Profile mode."));
        } else {
          plugin.putProfileBuilder(player, new GriefAlertSaveprofileCommand.GriefProfileBuilder());
          player.sendMessage(Text.of(TextColors.GREEN, "You are now in Add Profile mode."));
        }
      }
      return CommandResult.success();
    }
  }

}
