/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.prism;

import com.helion3.prism.util.DataQueries;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.AbstractAlert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.Prism;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


/**
 * <code>Alert</code> for all which derive from Prism.
 */
public abstract class PrismAlert extends AbstractAlert {

  private final PrismRecordArchived prismRecord;
  private final Transform<World> grieferTransform;

  PrismAlert(GriefProfile griefProfile, PrismRecordArchived prismRecord) {
    super(griefProfile);
    this.prismRecord = prismRecord;

    // Immediately set the transform of the griefer upon triggering the Alert
    grieferTransform = prismRecord.getDataContainer().getString(DataQueries.Player).flatMap(
        (s) ->
            Sponge.getServer()
                .getPlayer(UUID.fromString(s))
                .map(Player::getTransform)).orElseThrow(RuntimeException::new);
  }

  PrismRecordArchived getPrismRecord() {
    return prismRecord;
  }

  @Nonnull
  @Override
  public Transform<World> getGrieferTransform() {
    return grieferTransform;
  }

  @Nonnull
  @Override
  public Player getGriefer() {
    Optional<String> uuidOptional = prismRecord.getDataContainer().getString(DataQueries.Player);
    if (!uuidOptional.isPresent()) {
      GriefAlert.getInstance().getLogger().error("Could not get griefer UUID "
          + "from PrismRecord in a PrismAlert");
      GriefAlert.getInstance().getLogger().error(prismRecord.getDataContainer().toString());
      throw new NoSuchElementException();
    }
    Optional<Player> playerOptional = Sponge
        .getServer()
        .getPlayer(UUID.fromString(uuidOptional.get()));
    if (!playerOptional.isPresent()) {
      GriefAlert.getInstance()
          .getLogger()
          .error("Could not find player using UUID: " + uuidOptional.get());
      throw new NoSuchElementException();
    }

    return playerOptional.get();
  }

  @Nonnull
  @Override
  public Location<World> getGriefLocation() {
    return Prism.getLocation(getPrismRecord())
        .map(location -> location.add(0.5, 0.2, 0.5))
        .orElseThrow(() ->
            new RuntimeException("Couldn't find the location in a PrismAlert"));
  }
}
