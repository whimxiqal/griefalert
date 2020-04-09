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

import javax.annotation.Nonnull;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

/**
 * An event posted directly before an {@link Alert} has been checked by an officer.
 *
 * @author PietElite
 */
public class PreCheckAlertEvent extends AbstractEvent {

  private final Alert alert;
  private final Cause cause;
  private final Player officer;

  /**
   * The primary constructor for a <code>PreCheckAlertEvent</code>. This is made
   * to send information about an <code>Alert</code> which will be imminently checked,
   * and not made for any of the information within this event to be changed.
   *
   * @param alert   The <code>Alert</code> to be checked
   * @param cause   The cause of the <code>Alert</code>
   * @param officer The officer checking the <code>Alert</code>
   */
  public PreCheckAlertEvent(final Alert alert, final Cause cause, final Player officer) {
    this.alert = alert;
    this.cause = cause;
    this.officer = officer;
  }

  public Alert getAlert() {
    return alert;
  }

  @Nonnull
  @Override
  public Cause getCause() {
    return cause;
  }

  @SuppressWarnings("unused")
  public Player getOfficer() {
    return officer;
  }

}
