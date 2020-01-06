/* Created by PietElite */

package com.minecraftonline.griefalert.events;

import com.minecraftonline.griefalert.api.alerts.Alert;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class PreRunAlertEvent extends AbstractEvent {

  private Alert alert;
  private Cause cause;

  public PreRunAlertEvent(Alert alert, Cause cause) {
    this.alert = alert;
    this.cause = cause;
  }

  public Alert getAlert() {
    return alert;
  }

  @Override
  public Cause getCause() {
    return cause;
  }

}
