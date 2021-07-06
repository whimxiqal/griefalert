/*
 * MIT License
 *
 * Copyright (c) 2021 Pieter Svenson
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

import com.minecraftonline.griefalert.SpongeGriefAlert;
import com.minecraftonline.griefalert.sponge.alert.commands.common.GeneralCommand;
import com.minecraftonline.griefalert.sponge.alert.util.Errors;
import com.minecraftonline.griefalert.sponge.alert.util.Format;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Permissions;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * A command to get a GriefAlert tool.
 */
public class ToolCommand extends GeneralCommand {

  ToolCommand() {
    super(
        Permissions.GRIEFALERT_TOOL,
        Text.of("Get a tool to immediately fix grief")
    );
    setCommandElement(GenericArguments.string(Text.of("player")));
    addAlias("tool");
    addAlias("t");
  }

  @Nonnull
  @Override
  public CommandResult execute(@Nonnull CommandSource src,
                               @Nonnull CommandContext args) throws CommandException {
    if (src instanceof Player) {

      if (!SpongeGriefAlert.getSpongeInstance().getToolHandler().isToolEnabled()) {
        src.sendMessage(Format.error("The GriefAlert tool is currently disabled"));
        return CommandResult.empty();
      }

      Player player = (Player) src;

      Optional<String> playerName = args.getOne(Text.of("player"));

      if (!playerName.isPresent()) {
        player.sendMessage(Format.error("Specify a player to target"));
        return CommandResult.empty();
      }

      Sponge.getScheduler().createTaskBuilder().async().execute(() -> {
        try {
          Sponge.getServer().getGameProfileManager().get(playerName.get())
              .whenComplete((profile, exception) -> {
                if (profile == null || !profile.getName().isPresent()) {
                  player.sendMessage(Format.error("That player could not be found"));
                  return;
                }
                if (player.getUniqueId().equals(profile.getUniqueId())) {
                  player.sendMessage(Format.error("You may not create a tool for yourself!"));
                  return;
                }
                ItemStack tool = SpongeGriefAlert.getSpongeInstance()
                    .getToolHandler()
                    .tool(profile.getName().get(), profile.getUniqueId());

                Inventory inv = player.getInventory()
                    .query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class));
                if (!inv.canFit(tool)) {
                  player.sendMessage(Format.error("You have no room in your inventory! ",
                      "Make way for the tool and try again"));
                  return;
                }
                inv.offer(tool);
                player.sendMessage(Text.of(Format.prefix(), TextColors.GOLD, "Left Click ",
                    TextColors.WHITE, "to fix addition grief"));
                player.sendMessage(Text.of(Format.prefix(), TextColors.LIGHT_PURPLE, "Right Click ",
                    TextColors.WHITE, "to fix breakage grief"));
              }).get();
        } catch (InterruptedException | ExecutionException e) {
          // ignore
        }
      }).submit(SpongeGriefAlert.getSpongeInstance());
    } else {
      throw Errors.playerOnlyException();
    }
    return CommandResult.success();
  }

}
