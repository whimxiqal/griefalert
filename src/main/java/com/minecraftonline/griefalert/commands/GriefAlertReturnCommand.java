package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import java.util.Optional;
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
        GriefAlert.Permission.GRIEFALERT_COMMAND_CHECK,
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
      Optional<EntitySnapshot> optionalEntitySnapshot =
          GriefAlert.getInstance().getGriefEventCache().getSnapshot(player);
      if (optionalEntitySnapshot.isPresent()
          && optionalEntitySnapshot.get().getTransform().isPresent()) {
        if (player.setTransformSafely(optionalEntitySnapshot.get().getTransform().get())) {
          player.sendMessage(Text.of(
              TextColors.GREEN,
              "Sent to your location previous to your last grief check")
          );
        } else {
          player.sendMessage(Text.of(
              TextColors.YELLOW,
              "A safe location could not be determined")
          );
        }
      } else {
        player.sendMessage(Text.of(
            TextColors.YELLOW,
            "You don't have a saved location prior to any grief checks")
        );
      }
    }
    return CommandResult.success();
  }
}
