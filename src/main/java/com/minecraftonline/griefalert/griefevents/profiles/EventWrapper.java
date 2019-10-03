package com.minecraftonline.griefalert.griefevents.profiles;

import com.minecraftonline.griefalert.GriefAlert;

import java.util.Optional;

import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class EventWrapper {

  private final Event event;

  private final GriefAlert.GriefType type;

  private final EntitySnapshot grieferSnapshot;

  private final Player griefer;

  private final String griefedId;

  private final String griefedName;

  private Location<World> griefedLocation = null;

  /**
   * A wrapper for a Sponge event.
   *
   * @param event           The Sponge event
   * @param type            The type of grief
   * @param grieferSnapshot A snapshot of the griefer at the time of the event
   * @param griefer         The griefer
   * @param griefedId       The id of the griefed object
   * @param griefedName     The readable name of the griefed object
   */
  public EventWrapper(
      Event event,
      GriefAlert.GriefType type,
      EntitySnapshot grieferSnapshot,
      Player griefer,
      String griefedId,
      String griefedName) {
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

  /**
   * Cancel the event saved in this wrapper.
   */
  public void cancelEvent() {
    try {
      ((Cancellable) event).setCancelled(true);
    } catch (Exception e) {
      // ignore
    }
  }
}
