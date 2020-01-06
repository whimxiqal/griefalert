/* Created by PietElite */

package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import java.util.Optional;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.commands.AbstractCommand;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Permissions;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

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
  @NonnullByDefault
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    if (src instanceof Player) {

      Player player = (Player) src;

      Optional<Integer> revertsRemaining = GriefAlert.getInstance().getAlertQueue().revertOfficerTransform(player);

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
