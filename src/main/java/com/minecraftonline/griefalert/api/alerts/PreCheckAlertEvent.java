package com.minecraftonline.griefalert.api.alerts;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.impl.AbstractEvent;

public class PreCheckAlertEvent extends AbstractEvent {

  private final Alert alert;
  private final Cause cause;

  public PreCheckAlertEvent(Alert alert, Cause cause) {
    this.alert = alert;
    this.cause = cause;
  }

  @Override
  public Cause getCause() {
    return cause;
  }

  public Alert getAlert() {
    return alert;
  }
}
