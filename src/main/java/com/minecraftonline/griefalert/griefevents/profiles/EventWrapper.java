package com.minecraftonline.griefalert.griefevents.profiles;

import com.minecraftonline.griefalert.GriefAlert;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class EventWrapper {

  private final Event event;

  private final GriefAlert.GriefType type;

  private final EntitySnapshot grieferSnapshot;

  private final Player griefer;

  private final String griefedId;

  public final String griefedName;

  private Location<World> griefedLocation = null;

  public EventWrapper(Event event, GriefAlert.GriefType type, EntitySnapshot grieferSnapshot, Player griefer, String griefedId, String griefedName) {
    this.event = event;
    this.type = type;
    this.grieferSnapshot = grieferSnapshot;
    this.griefer = griefer;
    this.griefedId = griefedId;
    this.griefedName = griefedName;
  }

  public Event getEvent() {
    return event;
  }

  public GriefAlert.GriefType getType() {
    return type;
  }

  public EntitySnapshot getGrieferSnapshot() {
    return grieferSnapshot;
  }

  public Player getGriefer() {
    return griefer;
  }

  public String getGriefedId() {
    return griefedId;
  }

  public void setGriefedLocation(Location<World> griefedLocation) {
    this.griefedLocation = griefedLocation;
  }

  public Optional<Location<World>> getGriefedLocation() {
    return Optional.ofNullable(griefedLocation);
  }

  public String getGriefedName() {
    return this.griefedName;
  }
}
