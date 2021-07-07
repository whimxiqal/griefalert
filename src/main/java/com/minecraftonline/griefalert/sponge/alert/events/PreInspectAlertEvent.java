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

package com.minecraftonline.griefalert.sponge.alert.events;

import com.minecraftonline.griefalert.common.alert.alerts.Alert;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.plugin.PluginContainer;


/**
 * An event posted directly before an {@link Alert} has been checked by an officer.
 *
 * @author PietElite
 */
public final class PreInspectAlertEvent extends AbstractEvent {

  private final Alert alert;
  private final Cause cause;
  private final UUID officerUuid;

  private PreInspectAlertEvent(@Nonnull final Alert alert,
                               @Nonnull final Cause cause,
                               @Nonnull final UUID officerUuid) {
    this.alert = alert;
    this.cause = cause;
    this.officerUuid = officerUuid;
  }

  /**
   * Post a new {@link PreInspectAlertEvent} to Sponge's event manager.
   *
   * @param alert  the alert being inspected
   * @param source the generator of this inspection
   * @param plugin the container representing the handling plugin
   */
  public static void post(@Nonnull final Alert alert,
                          @Nonnull final Player source,
                          @Nonnull final PluginContainer plugin) {
    Sponge.getEventManager().post(new PreInspectAlertEvent(
        alert,
        Cause.builder()
            .append(source)
            .build(EventContext.builder()
                .add(EventContextKeys.PLUGIN, plugin)
                .build()),
        source.getUniqueId()));
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

  @Nonnull
  @SuppressWarnings("unused")
  public UUID getOfficer() {
    return officerUuid;
  }

}
