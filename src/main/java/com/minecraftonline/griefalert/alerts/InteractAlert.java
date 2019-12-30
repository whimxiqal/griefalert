package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class InteractAlert extends Alert {

  private InteractBlockEvent.Secondary blockEvent;
  private Transform<World> grieferTransform;

  public InteractAlert(int cacheCode, GriefProfile griefProfile, InteractBlockEvent.Secondary event) {
    super(cacheCode, griefProfile);
    blockEvent = event;
    grieferTransform = ((Player) event.getCause().root()).getTransform();
  }

  @Override
  public Text getMessageText() {
    return null;
  }

  @Override
  public Optional<Transform<World>> getTransform() {
    return Optional.of(grieferTransform);
  }

  @Override
  public Player getGriefer() {
    return (Player) blockEvent.getCause().root();
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.INTERACT;
  }
}
