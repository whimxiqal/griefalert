/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.minecraftonline.griefalert.api.alerts.GeneralAlert;
import com.minecraftonline.griefalert.api.records.GriefProfile;

import javax.annotation.Nonnull;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;

public abstract class SpongeAlert extends GeneralAlert {

  private final Event event;

  protected SpongeAlert(GriefProfile griefProfile, Event event) {
    super(griefProfile);
    this.event = event;
  }

  @SuppressWarnings("unused")
  protected Event getEvent() {
    return event;
  }

  @Nonnull
  @Override
  public Player getGriefer() {
    return (Player) event.getCause().root();
  }

}
