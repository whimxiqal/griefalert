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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.plugin.PluginContainer;

/**
 * An event posted before an {@link Alert} has been broadcast to staff. At the point of
 * this event being thrown, the <code>Alert</code> will have been formed and pushed to the
 * {@link RotatingList}
 *
 * @author PietElite
 */
public final class PreBroadcastAlertEvent extends AbstractEvent {

  private Alert alert;
  private Cause cause;

  /**
   * Post a new {@link PreBroadcastAlertEvent}.
   *  @param alert  the alert which is about to be broadcast
   * @param cause  the player who triggered the alert in the first place
   * @param plugin the container representing the handling plugin
   */
  public static void post(@Nonnull final Alert alert,
                          @Nonnull final User cause,
                          @Nonnull final PluginContainer plugin) {
    Sponge.getEventManager().post(new PreBroadcastAlertEvent(
        alert,
        Cause.builder()
            .append(cause)
            .build(EventContext.builder()
                .add(EventContextKeys.PLUGIN, plugin)
                .build())));
  }

  private PreBroadcastAlertEvent(@Nonnull final Alert alert,
                                 @Nonnull final Cause cause) {
    this.alert = alert;
    this.cause = cause;
  }

  @Nonnull
  public Alert getAlert() {
    return alert;
  }

  @Nonnull
  @Override
  public Cause getCause() {
    return cause;
  }

}
