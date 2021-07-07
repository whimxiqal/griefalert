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

package com.minecraftonline.griefalert.common.alert.services;

import com.minecraftonline.griefalert.common.alert.alerts.Alert;
import com.minecraftonline.griefalert.common.alert.alerts.inspections.AlertInspection;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.channel.MessageReceiver;

/**
 * A general service to handle generated {@link Alert}s.
 * An implementation for this is provided by <code>GriefAlert</code>.
 *
 * @author PietElite
 */
public interface AlertService {

  /**
   * Save this alert in a local cache and broadcast it to the necessary in-game players.
   *
   * @param alert the alert to submit
   * @return the index used to retrieve the alert, using @{@link #getAlert(int)}
   */
  int submit(@Nonnull Alert alert);

  /**
   * Retrieve an {@link Alert} by the code which was returned when it was submitted
   * with {@link #submit(Alert)}.
   *
   * @param index the retrieval code
   * @return the corresponding {@link Alert}
   * @throws IllegalArgumentException if the given index is invalid
   */
  @Nonnull
  Alert getAlert(int index) throws IllegalArgumentException;

  /**
   * Retrieve the most recent {@link Alert} that this officer inspected.
   *
   * @param officer the officer
   * @return the inspected alert
   */
  @Nonnull
  Optional<AlertInspection> getLastInspection(Player officer);

  /**
   * Send the given {@link Player} to the location of the {@link Alert} found
   * with the given retrieval code. The {@link Player} will receive the necessary
   * information and tools to respond to an {@link Alert}.
   *
   * @param index   the {@link Alert} retrieval code
   * @param officer the inspecting player
   * @param force   true if the officer should teleport to the location, even if it's unsafe
   * @param block   true if the officer should teleport the block location,
   *                false if the officer should teleport to the griefer's location
   *                at the time of grief
   * @return true if the inspection succeeded
   * @throws IllegalArgumentException if the given index is invalid
   */
  boolean inspect(int index,
                  @Nonnull Player officer,
                  boolean force,
                  boolean block) throws IllegalArgumentException;

  /**
   * Return an officer to their previous known location before their last grief check.
   *
   * @param officer The officer to teleport
   */
  boolean uninspect(@Nonnull Player officer);

  /**
   * Undo the inspection done by the officer by returning them
   * back to the location prior to an inspection.
   *
   * @param officer the inspecting player
   * @param index   the alert index
   * @return true if the player was returned
   */
  boolean uninspect(@Nonnull Player officer, int index);

  /**
   * Open up a helpful panel to officers to more easily perform their tasks.
   *
   * @param officer the officer
   * @return true if it successfully opened
   */
  boolean openPanel(@Nonnull Player officer);

  /**
   * Clear all information held in the {@link AlertService}.
   */
  void reset();

  /**
   * Give all the receivers all {@link Alert}s which match the filters given in the
   * {@link AlertRequest}.
   *
   * @param receivers the receivers of all the information
   * @param filters   the holder for all filters
   * @param sort      how the {@link Alert}s will be sorted for presentation
   * @param spread    true will send all {@link Alert}s individually. False will collapse similar
   *                  {@link Alert}s into singular lines.
   */
  void lookup(@Nonnull Collection<MessageReceiver> receivers,
              @Nonnull AlertRequest filters,
              @Nonnull Sort sort,
              boolean spread);

  /**
   * Generic sorting options for a sequence of {@link Alert}s.
   */
  enum Sort {
    /**
     * Sorts earliest first.
     */
    CHRONOLOGICAL,
    /**
     * Sorts latest first.
     */
    REVERSE_CHRONOLOGICAL,
    /**
     * Sorts by {@link Alert} retrieval code. Lowest first.
     */
    INDEX,
    /**
     * Sorts by {@link Alert} retrieval code. Highest first.
     */
    REVERSE_INDEX
  }

}
