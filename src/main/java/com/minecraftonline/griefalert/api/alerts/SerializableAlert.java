/* Created by PietElite */

package com.minecraftonline.griefalert.api.alerts;

import com.flowpowered.math.vector.Vector3d;
import com.minecraftonline.griefalert.api.caches.AlertManager;
import com.minecraftonline.griefalert.api.records.GriefProfile;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A helper class to store a serialized version of an {@link Alert} for storage.
 * The {@link AlertManager} is used to serialize and store all alerts.
 */
public class SerializableAlert implements Serializable {

  private final GriefProfile griefProfile;
  private final UUID grieferUuid;
  private final UUID grieferExtentUuid;
  private final Vector3d grieferPosition;
  private final Vector3d grieferRotation;
  private final Vector3d grieferScale;
  private final UUID griefExtentUuid;
  private final Vector3d griefPosition;
  private final Date created;
  private final boolean silent;
  private int cacheIndex;
  private final List<Detail.SerializedDetail> details = new LinkedList<>();

  private SerializableAlert(@Nonnull Alert alert) {
    this.griefProfile = alert.getGriefProfile();
    this.grieferUuid = alert.getGriefer().getUniqueId();
    this.grieferExtentUuid = alert.getGrieferTransform().getExtent().getUniqueId();
    this.grieferPosition = alert.getGrieferTransform().getPosition();
    this.grieferRotation = alert.getGrieferTransform().getRotation();
    this.grieferScale = alert.getGrieferTransform().getScale();
    this.griefExtentUuid = alert.getGriefLocation().getExtent().getUniqueId();
    this.griefPosition = alert.getGriefLocation().getPosition();
    this.created = alert.getCreated();
    this.silent = alert.isSilent();
    this.cacheIndex = alert.getCacheIndex();
    if (alert instanceof GeneralAlert) {
      GeneralAlert generalAlert = (GeneralAlert) alert;
      generalAlert.getDetails().forEach(detail -> details.add(detail.serialize(alert)));
    }
  }

  public static SerializableAlert of(Alert alert) {
    return new SerializableAlert(alert);
  }

  /**
   * Return a {@link SerializableAlert} back to an {@link Alert} with all its original information.
   *
   * @return an alert
   */
  public Alert deserialize() {
    User griefer = Sponge.getServiceManager()
        .provide(UserStorageService.class)
        .flatMap(userStorageService -> userStorageService.get(grieferUuid))
        .orElseThrow(IllegalArgumentException::new);

    GeneralAlert generalAlert = new GeneralAlert(griefProfile) {

      private boolean silent0 = silent;

      @Nonnull
      @Override
      public User getGriefer() {
        return griefer;
      }

      @Nonnull
      @Override
      public Transform<World> getGrieferTransform() {
        return new Transform<>(
            Sponge.getServer().getWorld(grieferExtentUuid).orElseThrow(() ->
                new IllegalArgumentException("SerializableAlert could not "
                    + "convert saved griefer's world UUID to a Sponge World")),
            grieferPosition,
            grieferRotation,
            grieferScale);
      }

      @Nonnull
      @Override
      public Location<World> getGriefLocation() {
        return new Location<>(
            Sponge.getServer().getWorld(griefExtentUuid).orElseThrow(() ->
                new IllegalArgumentException("SerializableAlert could not "
                    + "convert saved griefer's world UUID to a Sponge World")),
            griefPosition);
      }

      @Nonnull
      @Override
      public Date getCreated() {
        return created;
      }

      @Override
      public boolean isSilent() {
        return silent0;
      }

      @Override
      public void setSilent(boolean silent) {
        this.silent0 = silent;
      }

      @Override
      public int getCacheIndex() {
        return cacheIndex;
      }
    };

    generalAlert.getDetails().clear();
    details.forEach(detail -> {
      try {
        generalAlert.getDetails().add(detail.deserialize(Alert.class));
      } catch (ClassCastException e) {
        // This shouldn't happen
        e.printStackTrace();
      }
    });

    return generalAlert;
  }


}
