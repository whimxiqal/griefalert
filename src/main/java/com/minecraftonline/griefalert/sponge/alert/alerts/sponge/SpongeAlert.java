/* Created by PietElite */

package com.minecraftonline.griefalert.sponge.alert.alerts.sponge;

import com.flowpowered.math.vector.Vector3d;
import com.minecraftonline.griefalert.sponge.alert.alerts.GeneralAlert;
import com.minecraftonline.griefalert.common.alert.records.GriefProfile;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;

/**
 * An abstract alert caused by Sponge, as opposed by Prism.
 */
public abstract class SpongeAlert extends GeneralAlert {

  private final UUID grieferUuid;
  private final Vector3d grieferPosition;
  private final Vector3d grieferRotation;
  private final UUID worldUuid;

  protected SpongeAlert(GriefProfile griefProfile, Event event) {
    this(griefProfile, () -> event.getCause().first(Player.class).orElseThrow(() ->
        new RuntimeException("SpongeAlert couldn't find a player in the cause stack")));
  }

  protected SpongeAlert(GriefProfile griefProfile, Supplier<Player> supplier) {
    super(griefProfile);
    Player player = supplier.get();
    this.grieferUuid = player.getUniqueId();
    grieferPosition = player.getTransform().getPosition();
    grieferRotation = player.getTransform().getRotation();
    worldUuid = player.getTransform().getExtent().getUniqueId();
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
