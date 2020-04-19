/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.flowpowered.math.vector.Vector3d;
import com.minecraftonline.griefalert.alerts.GeneralAlert;
import com.minecraftonline.griefalert.api.records.GriefProfile;

import javax.annotation.Nonnull;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.world.World;

import java.util.UUID;

public abstract class SpongeAlert extends GeneralAlert {

  private final UUID grieferUuid;
  private final Vector3d grieferPosition;
  private final Vector3d grieferRotation;
  private final UUID worldUuid;

  protected SpongeAlert(GriefProfile griefProfile, Event event) {
    super(griefProfile);
    Player player = event.getCause().first(Player.class).orElseThrow(() ->
        new RuntimeException("SpongeAlert couldn't find a player in the cause stack"));
    this.grieferUuid = player.getUniqueId();
    Transform<World> transform = player.getTransform();
    grieferPosition = transform.getPosition();
    grieferRotation = transform.getRotation();
    worldUuid = transform.getExtent().getUniqueId();
  }

  @Nonnull
  @Override
  public UUID getGrieferUuid() {
    return grieferUuid;
  }

  @Nonnull
  @Override
  public Vector3d getGrieferPosition() {
    return grieferPosition;
  }

  @Nonnull
  @Override
  public Vector3d getGrieferRotation() {
    return grieferRotation;
  }

  @Nonnull
  @Override
  public UUID getWorldUuid() {
    return worldUuid;
  }
}
