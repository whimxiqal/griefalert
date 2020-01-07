/* Created by PietElite */

package com.minecraftonline.griefalert.api.events;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.caches.AlertStack;

import javax.annotation.Nonnull;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

/**
 * An event posted before an {@link Alert} has been broadcast to staff. At the point of
 * this event being thrown, the <code>Alert</code> will have been formed and pushed to the
 * {@link AlertStack}
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
