package com.minecraftonline.griefalert.api.alerts;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

public class PreCheckAlertEvent extends AbstractEvent {

  private final Alert alert;
  private final Cause cause;
  private final Player officer;

  public PreCheckAlertEvent(Alert alert, Cause cause, Player officer) {
    this.alert = alert;
    this.cause = cause;
    this.officer = officer;
  }

  public Alert getAlert() {
    return alert;
  }

  @Override
  public Cause getCause() {
    return cause;
  }

  public Player getOfficer() {
    return officer;
  }

}
