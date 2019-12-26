package com.minecraftonline.griefalert.profiles;

import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.api.profiles.GriefProfile;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class GriefProfileBuilderManager {

  // --------
  // OUTDATED
  // --------

  private final Map<UUID, MutablePair<Boolean, GriefProfile.Builder>> profileBuilderMap = new HashMap<>();

  /**
   * Dictate whether a player is in Profile Build mode. This is used only with the
   * build command.
   *
   * @param playerUuid The player's unique UUID
   * @param state  True if the player will be put into build mode, false if the player
   *               will not be in build mode.
   * @return True if the player changed states. False if the player did not change states.
   */
  public boolean setBuildingState(final UUID playerUuid, final boolean state) {
    Optional<MutablePair<Boolean, GriefProfile.Builder>> optionalPair = getBuilderPair(playerUuid);
    if (optionalPair.isPresent()) {
      if (optionalPair.get().getLeft() && !state) {
        optionalPair.get().setLeft(false);
        return true;
      } else if (!optionalPair.get().getLeft() && state) {
        optionalPair.get().setLeft(true);
        return true;
      }
    } else if (state) {
      profileBuilderMap.put(
          playerUuid,
          MutablePair.of(
              true,
              GriefProfile.builder(GriefEvents.BREAK, "minecraft:air")
          )
      );
      return true;
    }
    return false;
  }

  public Optional<GriefProfile.Builder> getBuilder(UUID playerUuid) {
    Optional<MutablePair<Boolean, GriefProfile.Builder>> optionalPair = getBuilderPair(playerUuid);
    if (optionalPair.isPresent() && optionalPair.get().getLeft()) {
      return Optional.ofNullable(optionalPair.get().getRight());
    }
    return Optional.empty();
  }

  public Optional<MutablePair<Boolean, GriefProfile.Builder>> getBuilderPair(UUID playerUuid) {
    return Optional.ofNullable(profileBuilderMap.get(playerUuid));
  }

  private void removeProfileBuilder(UUID playerUuid) {
    profileBuilderMap.remove(playerUuid);
  }


}
