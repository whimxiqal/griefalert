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

package com.minecraftonline.griefalert.api.alerts;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.spongepowered.api.text.Text;

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

  @Nonnull
  Vector3d getGrieferPosition();

  @Nonnull
  Vector3d getGrieferRotation();

  @Nonnull
  Vector3i getGriefPosition();

  @Nonnull
  UUID getWorldUuid();

  /**
   * Get the <code>Date</code> this alert was created.
   * @return the <code>Date</code>
   */
  @Nonnull
  Date getCreated();

  /**
   * Construct the main message body for this <code>Alert</code>. This
   * is the main body of the text which is sent when the <code>Alert</code>
   * is checked.
   *
   * @return the main <code>Text</code> for this <code>Alert</code>
   */
  @Nonnull
  Text getMessage();

  /**
   * Get summary text for this <code>Alert</code>.
   *
   * @return Text representing a cohesive summary of the <code>Alert</code>
   */
  @Nonnull
  Text getSummary();

  /**
   * Returns whether this <code>Alert</code> is silent.
   *
   * @return true if silent and staff are not notified of the <code>Alert</code>
   */
  boolean isSilent();

  /**
   * Sets whether this Alert will be silent when run.
   *
   * @param silent true if <code>Alert</code> is to be silent. False if
   *               <code>Alert</code> is to not be silent.
   */
  void setSilent(boolean silent);

  /**
   * Get the <code>GriefEvent</code> associated with this <code>Alert</code>.
   * This is always the <code>GriefEvent</code> associated with the
   * {@link GriefProfile}.
   *
   * @return The GriefEvent
   */
  @Nonnull
  GriefEvent getGriefEvent();

  /**
   * Get the target of the alert. This is always the target associated with
   * the {@link GriefProfile}.
   *
   * @return The String ID of the target
   */
  @Nonnull
  String getTarget();

  boolean muteRepeatProfiles();

}
