package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.world.World;

import java.util.Optional;

public abstract class SpongeAlert extends Alert {

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
  public Optional<Transform<World>> getTransform() {
    return Optional.of(grieferTransform);
  }

  @Override
  public Player getGriefer() {
    return (Player) event.getCause().root();
  }

}
