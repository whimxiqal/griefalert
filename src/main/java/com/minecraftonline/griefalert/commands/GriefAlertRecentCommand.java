package com.minecraftonline.griefalert.commands;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.GriefEvent;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.GriefAlertMessage;
import java.util.HashMap;
import java.util.List;

import com.minecraftonline.griefalert.util.General;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class GriefAlertRecentCommand extends AbstractCommand {

  GriefAlertRecentCommand() {
    super(
        GriefAlert.Permission.GRIEFALERT_COMMAND_RECENT,
        Text.of("Get information regarding recent cached grief alerts")
    );
    addAlias("recent");
    addAlias("n");
    HashMap<String, ?> filterMap = new HashMap<>();
    filterMap.put("player", null);
    addCommandElement(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))));
  }

  @Override
  @NonnullByDefault
  public CommandResult execute(@NonnullByDefault CommandSource src,
                               @NonnullByDefault CommandContext args) throws CommandException {
    if (args.getOne("player").isPresent()) {
      Player player = (Player) args.getOne("player").get();
      src.sendMessage(Text.of(TextColors.GRAY, "Alerts for " + player.getName()));
      List<GriefEvent> cache = GriefAlert.getInstance().getGriefEventCache().getListChronologically(true);
      if (cache.size() > 0) {
        GriefAlertMessage.Builder currentBuilder = getFresh(cache.get(0));
        for (int i = 1; i < cache.size(); i++) {
          if (cache.get(i).isSimilar(cache.get(i - 1))) {
            currentBuilder.addClickableCommand(
                String.valueOf(cache.get(i).getCacheCode()),
                "/griefalert check " + cache.get(i).getCacheCode(),
                Text.of("Teleport here.\n", cache.get(i).getSummary())
            );
          } else {
            src.sendMessage(currentBuilder.build().toText());
            currentBuilder = getFresh(cache.get(i));
          }
        }
        src.sendMessage(currentBuilder.build().toText());
      }
    }
    return CommandResult.success();
  }

  private GriefAlertMessage.Builder getFresh(GriefEvent event) {
    return GriefAlertMessage.builder(Text.of(
        TextColors.LIGHT_PURPLE, event.getGriefType().toPreteritVerb(),
        TextColors.RED, Grammar.correctIndefiniteArticles(" a " + event.getEvent().getGriefedName()),
        TextColors.GOLD, ":"
    ))
        .addClickableCommand(
            String.valueOf(event.getCacheCode()),
            "/griefalert check " + event.getCacheCode(),
            Text.of("Teleport here.\n", event.getSummary())
        );
  }
}
