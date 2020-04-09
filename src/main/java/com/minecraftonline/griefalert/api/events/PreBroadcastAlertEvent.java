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

package com.minecraftonline.griefalert.api.events;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.structures.RotatingList;

import javax.annotation.Nonnull;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

/**
 * An event posted before an {@link Alert} has been broadcast to staff. At the point of
 * this event being thrown, the <code>Alert</code> will have been formed and pushed to the
 * {@link RotatingList}
 *
 * @author PietElite
 */
public class PreBroadcastAlertEvent extends AbstractEvent {

  private Alert alert;
  private Cause cause;

  /**
   * The primary constructor. This is made to send information about an <code>Alert</code>
   * which is being processed and will be imminently broadcast to staff.
   *
   * @param alert The <code>Alert</code> being run
   * @param cause The cause of the <code>Alert</code>
   */
  public PreBroadcastAlertEvent(final Alert alert, final Cause cause) {
    this.alert = alert;
    this.cause = cause;
  }

  public Alert getAlert() {
    return alert;
  }

  @Nonnull
  @Override
  public Cause getCause() {
    return cause;
  }

}
