/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.prism;

import com.helion3.prism.util.DataQueries;
import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.Prism;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

/**
 * <code>Alert</code> for all which derive from Prism.
 */
public abstract class PrismAlert extends Alert {

  private final PrismRecordArchived prismRecord;
  private Transform<World> grieferTransform;

  PrismAlert(GriefProfile griefProfile, PrismRecordArchived prismRecord) {
    super(griefProfile);
    this.prismRecord = prismRecord;

    // Immediately set the transform of the griefer upon triggering the Alert
    grieferTransform = null;
    prismRecord.getDataContainer().getString(DataQueries.Player).ifPresent(
        (s) ->
            grieferTransform = Sponge.getServer()
                .getPlayer(UUID.fromString(s))
                .map(Player::getTransform).orElse(null)
    );
  }

  PrismRecordArchived getPrismRecord() {
    return prismRecord;
  }

  @Override
  public Optional<Transform<World>> getTransform() {
    return Optional.ofNullable(grieferTransform);
  }

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

  /**
   * Generator for the appropriate <code>Alert</code>> corresponding with a Prism-caused
   * event. The returned <code>PrismAlert</code> will be empty if the given parameters do not allow
   * for a valid <code>PrismAlert</code>.
   *
   * @param griefProfile The <code>GriefProfile</code> flagging this event
   * @param prismRecord  The prism record
   * @return An optional prism alert
   */
  public static Optional<PrismAlert> of(
      GriefProfile griefProfile,
      PrismRecordArchived prismRecord) {
    Optional<String> targetOptional = Prism.getTarget(prismRecord);
    if (!targetOptional.isPresent()) {
      return Optional.empty();
    }

    if (prismRecord.getEvent().equals(PrismEvents.BLOCK_BREAK.getId())) {

      if (targetOptional.get().contains("sign")) {

        // -----------------------------
        // Condition for a SignBreakAlert
        // -----------------------------

        return Optional.of(new SignBreakAlert(griefProfile, prismRecord));
      } else {

        // -----------------------------
        // Condition for a BreakAlert
        // -----------------------------

        return Optional.of(new BreakAlert(griefProfile, prismRecord));
      }
    } else if (prismRecord.getEvent().equals(PrismEvents.BLOCK_PLACE.getId())) {
      if (targetOptional.get().contains("sign")) {

        // -----------------------------
        // Condition for a SignPlaceAlert
        // -----------------------------

        return Optional.of(new SignPlaceAlert(griefProfile, prismRecord));
      } else {

        // -----------------------------
        // Condition for a PlaceAlert
        // -----------------------------

        return Optional.of(new PlaceAlert(griefProfile, prismRecord));
      }
    } else if (prismRecord.getEvent().equals(PrismEvents.ENTITY_DEATH.getId())) {

      // -----------------------------
      // Condition for a DeathAlert
      // -----------------------------

      return Optional.of(new DeathAlert(griefProfile, prismRecord));
    }

    return Optional.empty();
  }

}
