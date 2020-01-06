/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.minecraftonline.griefalert.alerts.AbstractAlert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import java.util.Optional;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.world.World;

public abstract class SpongeAlert extends AbstractAlert {

  private final Event event;
  private final Transform<World> grieferTransform;

  protected SpongeAlert(GriefProfile griefProfile, Event event) {
    super(griefProfile);
    this.event = event;
    grieferTransform = ((Player) event.getCause().root()).getTransform();
  }

  protected Event getEvent() {
    return event;
  }

  @Override
  public Transform<World> getGrieferTransform() {
    return grieferTransform;
  }

  @Override
  public Player getGriefer() {
    return (Player) event.getCause().root();
  }

}
