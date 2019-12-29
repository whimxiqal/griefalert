package com.minecraftonline.griefalert.alerts;

import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.util.DataQueries;
import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.profiles.GriefProfile;
import com.minecraftonline.griefalert.util.Prism;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

public abstract class PrismAlert extends Alert {

  private final PrismRecord prismRecord;
  private Transform<World> grieferTransform;

  PrismAlert(int cacheCode, GriefProfile griefProfile, PrismRecord prismRecord) {
    super(cacheCode, griefProfile);
    this.prismRecord = prismRecord;

    grieferTransform = null;
    prismRecord.getDataContainer().getString(DataQueries.Player).ifPresent(
        (s) ->
            grieferTransform = Sponge.getServer()
                .getPlayer(UUID.fromString(s))
                .map(Player::getTransform).orElse(null)
    );
  }

  public static Optional<PrismAlert> of(int cacheCode, GriefProfile griefProfile, PrismRecord prismRecord) {
    Optional<String> targetOptional = Prism.getTarget(prismRecord);
    if (!targetOptional.isPresent()) {
      return Optional.empty();
    }

    if (prismRecord.getEvent().equals(PrismEvents.BLOCK_BREAK.getName())) {
      if (targetOptional.get().contains("sign")) {
        return Optional.of(new SignBreakAlert(cacheCode, griefProfile, prismRecord));
      } else {
        return Optional.of(new BreakAlert(cacheCode, griefProfile, prismRecord));
      }
    } else if (prismRecord.getEvent().equals(PrismEvents.BLOCK_PLACE.getName())) {
      if (targetOptional.get().contains("sign")) {
        return Optional.of(new SignPlaceAlert(cacheCode, griefProfile, prismRecord));
      } else {
        return Optional.of(new PlaceAlert(cacheCode, griefProfile, prismRecord));
      }
    } else if (prismRecord.getEvent().equals(PrismEvents.ENTITY_DEATH.getName())) {
      return Optional.of(new DeathAlert(cacheCode, griefProfile, prismRecord));
    }

    return Optional.empty();
  }

  @Override
  public Optional<Transform<World>> getTransform() {
    return Optional.ofNullable(grieferTransform);
  }
}
