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

package com.minecraftonline.griefalert.common.alert.alerts;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvent;
import com.minecraftonline.griefalert.common.alert.records.GriefProfile;
import com.minecraftonline.griefalert.common.alert.services.AlertService;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.text.Text;

/**
 * An {@link Alert} is the primary object stored and handled in GriefAlert.
 * Submit an {@link Alert} to be sent out to staff by submitting it to an
 * {@link AlertService}.
 *
 * @author PietElite
 */
public interface Alert extends Serializable {

  /**
   * Get the <code>GriefProfile</code> which was used to flag this <code>Alert</code>.
   *
   * @return The GriefProfile
   */
  @Nonnull
  GriefProfile getGriefProfile();

  /**
   * Get the <code>User</code> responsible for triggering the <code>Alert</code>.
   *
   * @return The griefer
   */
  @Nonnull
  UUID getGrieferUuid();

  /**
   * The position of the griefer when they performed the grief.
   *
   * @return the vector representation of the griefer's position
   */
  @Nonnull
  Vector3d getGrieferPosition();

  /**
   * The rotation of the griefer when they performed the grief.
   *
   * @return the vector representation of the griefer's rotation
   */
  @Nonnull
  Vector3d getGrieferRotation();

  /**
   * The block position of the grief event. For block related events, this is
   * the position of the block.
   *
   * @return the vector representation of the grief position
   */
  @Nonnull
  Vector3i getGriefPosition();

  /**
   * The unique identifier for the world in which the grief occurred.
   *
   * @return the unique identifier
   */
  @Nonnull
  UUID getWorldUuid();

  /**
   * Get the {@link Date} this alert was created.
   *
   * @return the {@link Date}
   */
  @Nonnull
  Date getCreated();

  /**
   * Construct the main broadcast message body for this {@link Alert}.
   *
   * @return the message
   */
  @Nonnull
  Text getMessage();

  /**
   * Get summary for all important information about this {@link Alert}.
   *
   * @return the summary text
   */
  @Nonnull
  Text getSummary();

  /**
   * Returns whether this {@link Alert} is silent.
   * Silent alerts are not broadcast to staff.
   *
   * @return true if silent
   */
  boolean isSilent();

  /**
   * Sets whether this {@link Alert} will be silent when run.
   * Silent alerts are not broadcast to staff.
   *
   * @param silent true if silent
   */
  void setSilent(boolean silent);

  /**
   * Get the {@link GriefEvent} associated with this {@link Alert}
   * This is normally the {@link GriefEvent} associated with the
   * {@link GriefProfile}.
   *
   * @return The GriefEvent
   */
  @Nonnull
  GriefEvent getGriefEvent();

  /**
   * Get the target of the alert. This is usually the target associated with
   * the {@link GriefProfile}.
   *
   * @return The full string id of the target
   */
  @Nonnull
  String getTarget();

}
