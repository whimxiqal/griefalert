package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class UseAlert extends Alert {

  private InteractItemEvent.Secondary itemEvent;
  private Transform<World> grieferTransform;

  private UseAlert(GriefProfile griefProfile, InteractItemEvent.Secondary event) {
    super(griefProfile);
    itemEvent = event;
    grieferTransform = ((Player) event.getCause().root()).getTransform();
  }

  public static UseAlert of(GriefProfile griefProfile, InteractItemEvent.Secondary event) {
    return new UseAlert(griefProfile, event);
  }

  @Override
  public Optional<Transform<World>> getTransform() {
    return Optional.of(grieferTransform);
  }

  @Override
  public Player getGriefer() {
    return (Player) itemEvent.getCause().root();
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.ITEM_USE;
  }

}
