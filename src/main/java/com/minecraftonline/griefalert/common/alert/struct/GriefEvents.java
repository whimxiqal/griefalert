/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert.common.alert.struct;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.sponge.data.util.PrismEvents;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.registry.CatalogRegistryModule;

/**
 * A utility class to enumerate all possible {@link GriefEvent}s.
 */
public final class GriefEvents {

  public static final GriefEvent ATTACK = GriefEvent
      .of("attack", "Entity Attack", "attacked",
          "Primarily interact with a target entity with main hand");
  public static final GriefEvent BREAK = GriefEvent.of(
      PrismEvents.BLOCK_BREAK,
      "Break a block");
  public static final GriefEvent DEATH = GriefEvent.of(
      PrismEvents.ENTITY_DEATH,
      "Directly cause the death of an entity");
  public static final GriefEvent EDIT = GriefEvent
      .of("edit", "Edit", "edited",
          "Edit the contents or internal state of an object");
  public static final GriefEvent INTERACT = GriefEvent
      .of("interact", "Interact", "interacted with",
          "Secondarily interact with a target object in the world with main hand");
  public static final GriefEvent ITEM_APPLY = GriefEvent
      .of("apply", "Item Apply", "applied",
          "Secondarily interact with a block while holding the target item in main hand.");
  public static final GriefEvent ITEM_USE = GriefEvent
      .of("use", "Item Use", "used",
          "Secondarily interact with the target item in main hand");
  public static final GriefEvent PLACE = GriefEvent.of(
      PrismEvents.BLOCK_PLACE,
      "Place a block");
  public static final GriefEvent TRANSFORM = GriefEvent
      .of("transform", "Transform", "transformed",
          "Transformed a block from one non-air target block to a different non-air block");
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

        @NotNull
        @Override
        public Collection<GriefEvent> getAll() {
          return Lists.newArrayList(
              BREAK,
              PLACE,
              TRANSFORM,
              DEATH,
              EDIT,
              ITEM_USE,
              ITEM_APPLY,
              INTERACT,
              ATTACK
          );
        }
      };

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private GriefEvents() {
  }


}
