/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import com.google.common.collect.Lists;
import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.api.data.GriefEvent;

import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;

import org.spongepowered.api.registry.CatalogRegistryModule;

public final class GriefEvents {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private GriefEvents() {
  }

  public static final GriefEvent BREAK = GriefEvent.of(PrismEvents.BLOCK_BREAK);

  public static final GriefEvent PLACE = GriefEvent.of(PrismEvents.BLOCK_PLACE);

  public static final GriefEvent DEATH = GriefEvent.of(PrismEvents.ENTITY_DEATH);

  public static final GriefEvent ITEM_USE = GriefEvent
      .of("use", "Item Use", "used");

  public static final GriefEvent INTERACT = GriefEvent
      .of("interact", "Interact", "interacted with");

  public static final GriefEvent ATTACK = GriefEvent
      .of("attack", "Entity Attack", "attacked");

  public static final GriefEvent REPLACE = GriefEvent
      .of("replace", "Block Replace", "replaced");

  public static final CatalogRegistryModule<GriefEvent> REGISTRY_MODULE = new
      CatalogRegistryModule<GriefEvent>() {
        @Nonnull
        @Override
        public Optional<GriefEvent> getById(@Nonnull String id) {
          for (GriefEvent griefEvent : getAll()) {
            if (griefEvent.getId().equalsIgnoreCase(id)) {
              return Optional.of(griefEvent);
            }
          }
          return Optional.empty();
        }

        @Override
        public Collection<GriefEvent> getAll() {
          return Lists.newArrayList(
              BREAK,
              PLACE,
              DEATH,
              ITEM_USE,
              INTERACT,
              ATTACK,
              REPLACE
          );
        }
      };


}
