/* Created by PietElite */

package com.minecraftonline.griefalert.api.events;

import com.minecraftonline.griefalert.api.alerts.Alert;

import javax.annotation.Nonnull;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

/**
 * An event posted before an {@link Alert} has been run/executed. At the point of
 * this event being thrown, the <code>Alert</code> will have been formed, but
 * will not have been added to the
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
