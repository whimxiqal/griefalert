/*
 * This file is part of Prism, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.minecraftonline.griefalert.sponge.data.commands;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.common.data.records.ActionableResult;
import com.minecraftonline.griefalert.sponge.data.util.Format;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.BlockChangeFlags;

// TODO remove
public class UndoCommand {
  private UndoCommand() {
  }

  public static CommandSpec getCommand() {
    return CommandSpec.builder()
        .permission("prism.undo")
        .executor((source, args) -> {
          if (!(source instanceof Player)) {
            throw new CommandException(Format.error("You must be a player to use this command."));
          }

          // TODO get actionable results correctly
          List<ActionableResult> results = Lists.newArrayList(); //Prism.getInstance().getLastActionResults().get(((Player) source).getUniqueId());
          if (results == null || results.isEmpty()) {
            throw new CommandException(Format.error("You have no valid actions to undo."));
          }
          // Reverse the order of the list to undo last action.
          results = Lists.reverse(results);

          int applied = 0;
          int skipped = 0;

          for (ActionableResult result : results) {
            if (result.getTransaction().isPresent()) {
              Object rawOriginal = result.getTransaction().get().getOriginal();
              Object rawFinal = result.getTransaction().get().getFinal();

              if (rawOriginal instanceof BlockSnapshot) {
                if (((BlockSnapshot) rawOriginal).restore(true, BlockChangeFlags.NONE)) {
                  applied++;
                } else {
                  skipped++;
                }
              }

              if (rawFinal instanceof Entity) {
                Entity entity = (Entity) rawFinal;
                if (!entity.isRemoved()) {
                  entity.remove();
                  applied++;
                } else {
                  skipped++;
                }
              }
            } else {
              skipped++;
            }
          }

          Map<String, String> tokens = new HashMap<>();
          tokens.put("appliedCount", "" + applied);
          tokens.put("skippedCount", "" + skipped);

          final String messageTemplate;
          if (skipped > 0) {
            messageTemplate = "rollback.success.withskipped";
          } else {
            messageTemplate = "rollback.success";
          }

          source.sendMessage(Text.of(messageTemplate));

          return CommandResult.success();
        })
        .build();
  }
}
