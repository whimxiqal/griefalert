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

import com.minecraftonline.griefalert.SpongeGriefAlert;
import com.minecraftonline.griefalert.common.data.flags.Flag;
import com.minecraftonline.griefalert.common.data.query.QuerySession;
import com.minecraftonline.griefalert.common.data.query.Sort;
import com.minecraftonline.griefalert.common.data.records.Actionable;
import com.minecraftonline.griefalert.common.data.records.ActionableResult;
import com.minecraftonline.griefalert.common.data.records.Result;
import com.minecraftonline.griefalert.sponge.data.util.Format;
import com.minecraftonline.griefalert.sponge.data.util.WorldUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

/**
 * @author viveleroi
 */
// TODO remove
@Deprecated
public class ApplierCommand {

  private ApplierCommand() {
  }

  public static CommandSpec getCommand(Sort sort) {
    return CommandSpec.builder()
        .permission("prism.rollback")
        .arguments(GenericArguments.remainingJoinedStrings(Text.of("parameters")))
        .executor((source, args) -> {
          // Create a new query session
          final QuerySession session = new QuerySession(source);
          session.addFlag(Flag.NO_GROUP);
          try {
            source.sendMessage(Format.heading("Querying records..."));

            CompletableFuture<Void> future = session.newQueryFromArguments(args.<String>getOne("parameters").get());
            // Ignore user order flag, if used, for proper rollback/restore order to be used.
            session.setSortBy(sort);
            future.thenAccept((v) -> runApplier(session, sort));
          } catch (Exception e) {
            source.sendMessage(Format.error(Text.of(e.getMessage())));
            e.printStackTrace();
          }

          return CommandResult.success();
        })
        .build();
  }

  /**
   * Use a designated QuerySession
   *
   * @param session
   * @param sort
   */
  public static void runApplier(QuerySession session, Sort sort) {
    session.getQuery().setLimit(SpongeGriefAlert.getSpongeInstance().getConfig().getLimitCategory().getMaximumActionable());
    CommandSource source = session.getCommandSource();
    try {
      List<ActionableResult> actionResults = new ArrayList<>();
      // Iterate query results
      CompletableFuture<List<Result>> futureResults = SpongeGriefAlert.getSpongeInstance().getStorageAdapter().records().query(session, false);
      futureResults.thenAccept(results -> {
        if (results.isEmpty()) {
          source.sendMessage(Format.error("No results."));
        } else {
          try {
            // Iterate record results
            Task.builder().execute(() -> {
              results.forEach(result -> {
                try {
                  if (result instanceof Actionable) {

                    Actionable actionable = (Actionable) result;

                    if (sort.equals(Sort.NEWEST_FIRST)) {
                      actionResults.add(actionable.rollback());
                    } else {
                      actionResults.add(actionable.restore());
                    }
                  }
                } catch (Exception e) {
                  source.sendMessage(Format.error(Text.of(e.getMessage())));
                  e.printStackTrace();
                }
              });
              sendResults(source, actionResults);
            }).submit(SpongeGriefAlert.getSpongeInstance());
          } catch (Exception e) {
            e.printStackTrace();
          }

          if (source instanceof Player) {
            int changes = 0;

            if (session.hasFlag(Flag.CLEAN)) {
              changes += WorldUtil.removeIllegalBlocks(
                  ((Player) source).getLocation(), session.getRadius());
              changes += WorldUtil.removeItemEntitiesAroundLocation(((Player) source).getLocation(), session.getRadius());
            }

            if (session.hasFlag(Flag.DRAIN)) {
              changes += WorldUtil.removeLiquidsAroundLocation(
                  ((Player) source).getLocation(), session.getRadius());
            }

            if (changes > 0) {
              source.sendMessage(Format.bonus("Cleaning area..."));
            }
          }

        }
      });
    } catch (Exception e) {
      source.sendMessage(Format.error(Text.of(e.getMessage())));
      e.printStackTrace();
    }
  }

  private static void sendResults(CommandSource source, List<ActionableResult> actionResults) {
    int appliedCount = 0;
    int skippedCount = 0;
    for (ActionableResult result : actionResults) {
      if (result.applied()) {
        appliedCount++;
      } else {
        skippedCount++;
      }
    }

    Map<String, String> tokens = new HashMap<>();
    tokens.put("appliedCount", "" + appliedCount);
    tokens.put("skippedCount", "" + skippedCount);

    // TODO send the correct messages (Prism used a "Template" class to format the results)
    final String messageTemplate;
    if (skippedCount > 0) {
      messageTemplate = "rollback.success.withskipped";
    } else {
      messageTemplate = "rollback.success";
    }

    source.sendMessage(Format.heading("rollback.success.bonus"));

    if (source instanceof Player) {
      SpongeGriefAlert.getSpongeInstance().getLastActionResults().put(((Player) source).getUniqueId(), actionResults);
    }
  }
}