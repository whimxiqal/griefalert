/* Created by PietElite */

package com.minecraftonline.griefalert.util;

import com.google.common.collect.Lists;
import com.helion3.prism.api.data.PrismEvent;
import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

public final class GriefEvents {

  public static final CatalogRegistryModule<GriefEvent> REGISTRY_MODULE = new CatalogRegistryModule<GriefEvent>() {
    @Override
    public Optional<GriefEvent> getById(String id) {
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
  private GriefEvents() {
  }


  public static final GriefEvent BREAK = GriefEvent.of(PrismEvents.BLOCK_BREAK);
  public static final GriefEvent PLACE = GriefEvent.of(PrismEvents.BLOCK_PLACE);
  public static final GriefEvent DEATH = GriefEvent.of(PrismEvents.ENTITY_DEATH);
  public static final GriefEvent ITEM_USE = GriefEvent.of("use", "Item Use", "used");
  public static final GriefEvent INTERACT = GriefEvent.of("interact", "Interact", "interacted with");
  public static final GriefEvent ATTACK = GriefEvent.of("attack", "Attack", "attacked");
  public static final GriefEvent REPLACE = GriefEvent.of("replace", "Replace", "replaced");

}
