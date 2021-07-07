/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert.sponge.alert.commands;

import com.google.common.collect.ImmutableMap;
import com.minecraftonline.griefalert.SpongeGriefAlert;
import com.minecraftonline.griefalert.common.alert.alerts.Alert;
import com.minecraftonline.griefalert.common.alert.alerts.Fixable;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvents;
import com.minecraftonline.griefalert.common.alert.templates.Arg;
import com.minecraftonline.griefalert.common.alert.templates.Templates;
import com.minecraftonline.griefalert.sponge.alert.commands.common.GeneralCommand;
import com.minecraftonline.griefalert.sponge.alert.util.Alerts;
import com.minecraftonline.griefalert.sponge.alert.util.Communication;
import com.minecraftonline.griefalert.sponge.alert.util.Errors;
import com.minecraftonline.griefalert.sponge.alert.util.Format;
import com.minecraftonline.griefalert.sponge.alert.util.Grammar;
import com.minecraftonline.griefalert.sponge.alert.util.enums.AlertTags;
import com.minecraftonline.griefalert.sponge.alert.util.enums.CommandKeys;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Permissions;
import java.time.Instant;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextElement;

/**
 * A command to "fix" an alert by undoing the changes associated with the alert.
 *
 * @author PietElite
 */
public class FixCommand extends GeneralCommand {

  private static final int FIX_TIMEOUT_SECONDS = 1800;  // 30 minutes

  FixCommand() {
    super(
        Permissions.GRIEFALERT_COMMAND_FIX,
        Text.of("Use an alert id to restore the event triggering this alert")
    );
    addAlias("fix");
    addAlias("f");
    setCommandElement(GenericArguments.flags()
        .flag("c")
        .buildWith(
            GenericArguments.optional(GenericArguments.integer(CommandKeys.ALERT_INDEX.get()))));
    addFlagDescription("c",
        Text.of("Collects the item from the griefer's inventory. \n"
            + "The griefer may or may not have picked it up!"),
        false);
  }


  @Override
  @Nonnull
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) throws CommandException {

    int index;
    try {
      index = args.requireOne(CommandKeys.ALERT_INDEX.get());
    } catch (NoSuchElementException e) {
      sendHelp(src);
      return CommandResult.success();
    }

    // Find PrismAlert to manipulate
    try {
      Alert alert = SpongeGriefAlert.getSpongeInstance().getAlertService().getAlert(index);
      if (alert.getCreated().before(Date.from(Instant.now().minusSeconds(FIX_TIMEOUT_SECONDS)))
          && Permissions.has(src, Permissions.GRIEFALERT_COMMAND_ROLLBACK)) {
        throw new CommandException(Format.error(String.format(
            "You can only do that %d minutes after the alert",
            Math.floorDiv(FIX_TIMEOUT_SECONDS, 60))));
      }
      if (alert instanceof Fixable) {

        if (((Fixable) alert).fixed()) {
          throw new CommandException(Format.error("This alert was already fixed!"));
        }

        if ((src instanceof Player)
            && ((Player) src).getUniqueId().equals(alert.getGrieferUuid())
            && (!Permissions.has(src, Permissions.SELF_FIX))) {
          throw new CommandException(Format.error("You can't undo your own actions"));
        }

        Text officerName = src instanceof Player
            ? Format.userName((Player) src)
            : Text.of(src.getName());

        if (((Fixable) alert).fix(src)) {
          Text message = Templates.FIX.getTextTemplate().apply(
              ImmutableMap.<String, TextElement>builder()
                  .put(Arg.PREFIX.name(), Format.prefix())
                  .put(Arg.OFFICER.name(), officerName)
                  .put(Arg.TARGET.name(), Grammar.addIndefiniteArticle(Format.item(alert.getTarget())))
                  .put(Arg.GRIEFER.name(), Format.userName(Alerts.getGriefer(alert)))
                  .put(Arg.EVENT.name(), Format.action(alert.getGriefEvent()))
                  .put(Arg.X.name(), Text.of(alert.getGriefPosition().getX()))
                  .put(Arg.Y.name(), Text.of(alert.getGriefPosition().getY()))
                  .put(Arg.Z.name(), Text.of(alert.getGriefPosition().getZ()))
                  .put(Arg.WORLD.name(), Text.of(Alerts.getWorld(alert).getName()))
                  .put(Arg.SUFFIX.name(), AlertTags.getTagInfo(index))
                  .build())
              .build();
          Communication.getStaffBroadcastChannel().send(message);
          if (args.hasAny("c")) {
            if (!alert.getGriefEvent().equals(GriefEvents.BREAK)) {
              src.sendMessage(Format.error(
                  "You may only collect an item from someone if they ",
                  Format.action(GriefEvents.BREAK),
                  " it"));
            } else {
              if (removeItem(Alerts.getGriefer(alert), alert.getTarget())) {
                Communication.getStaffBroadcastChannel().send(Format.success(
                    officerName,
                    " collected ",
                    Grammar.addIndefiniteArticle(Format.item(alert.getTarget())),
                    " from ",
                    Format.userName(Alerts.getGriefer(alert)),
                    "'s inventory"));
              } else {
                src.sendMessage(Format.error(
                    "Could not remove ",
                    Grammar.addIndefiniteArticle(Format.item(alert.getTarget())),
                    " from ",
                    Format.userName(Alerts.getGriefer(alert)),
                    "'s inventory"));
              }
            }
          }
          return CommandResult.success();
        } else {
          throw new CommandException(Format.error("Operation failed."));
        }
      } else {
        throw new CommandException(Format.error("This event cannot be automatically fixed"));
      }
    } catch (IllegalArgumentException e) {
      throw Errors.noAlertException();
    }

  }

  private boolean removeItem(User griefer, String target) {
    Optional<ItemType> itemTypeOptional = Sponge.getRegistry().getType(ItemType.class, target);
    if (!itemTypeOptional.isPresent()) {
      SpongeGriefAlert.getSpongeInstance().getLogger().debug("Could not get ItemType from target id: " + target);
      return false;
    }
    ItemType itemType = itemTypeOptional.get();

    Inventory inventory = griefer.getInventory();
    if (!inventory.contains(itemType)) {
      return false;
    }

    for (Inventory slot : inventory.slots()) {
      if (slot.peek().map(stack -> stack.getType().equals(itemType)).orElse(false)) {
        slot.poll(1);
        return true;
      }
    }

    return false;
  }

}
