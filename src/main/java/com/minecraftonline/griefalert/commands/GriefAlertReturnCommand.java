/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Permissions;

import java.util.Optional;
import javax.annotation.Nonnull;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;


public class GriefAlertReturnCommand extends AbstractCommand {


  GriefAlertReturnCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_CHECK,
        Text.of("Return to your previous location prior to a grief alert check")
    );
    addAlias("return");
    addAlias("r");
  }

  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) {
    if (src instanceof Player) {

      Player player = (Player) src;

      Optional<Integer> revertsRemaining = GriefAlert.getInstance()
          .getAlertManager()
          .revertOfficerTransform(player);

      if (!revertsRemaining.isPresent()) {
        player.sendMessage(Format.info("You have no previous location."));
      } else {
        player.sendMessage(Format.success(
            "Returned to previous location. (",
            Format.bonus(revertsRemaining.get()),
            " available)"));
      }
    }
    return CommandResult.success();
  }
}
