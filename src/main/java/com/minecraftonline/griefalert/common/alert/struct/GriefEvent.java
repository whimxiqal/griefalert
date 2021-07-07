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

import com.minecraftonline.griefalert.common.alert.alerts.Alert;
import com.minecraftonline.griefalert.common.alert.records.GriefProfile;
import com.minecraftonline.griefalert.common.bridge.EnumRegistry;
import com.minecraftonline.griefalert.common.data.struct.PrismEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * An object to describe a specific action. This is one of the factors which match
 * certain in-game events with {@link GriefProfile}s
 * so that {@link Alert}s can be triggered.
 *
 * @author PietElite
 */
public enum GriefEvent {

  ATTACK("attack", "Entity Attack", "attacked",
      "Primarily interact with a target entity with main hand"),
  BREAK(PrismEvent.BLOCK_BREAK,
      "Break a block"),
  DEATH(PrismEvent.ENTITY_DEATH,
      "Directly cause the death of an entity"),
  EDIT("edit", "Edit", "edited",
      "Edit the contents or internal state of an object"),
  INTERACT("interact", "Interact", "interacted with",
      "Secondarily interact with a target object in the world with main hand"),
  ITEM_APPLY("apply", "Item Apply", "applied",
      "Secondarily interact with a block while holding the target item in main hand."),
  ITEM_USE("use", "Item Use", "used",
      "Secondarily interact with the target item in main hand"),
  PLACE(PrismEvent.BLOCK_PLACE,
      "Place a block"),
  TRANSFORM("transform", "Transform", "transformed",
      "Transformed a block from one non-air target block to a different non-air block");

  @Getter
  @NotNull
  private static final EnumRegistry<GriefEvent> registry = new EnumRegistry<>(
      event -> event.id,
      GriefEvent.class);

  @Getter
  @NotNull
  private final String id;
  @Getter
  @NotNull
  private final String name;
  @Getter
  @NotNull
  private final String preterit;
  @Getter
  @NotNull
  private final String description;

  GriefEvent(@NotNull String id,
             @NotNull String name,
             @NotNull String preterit,
             @NotNull String description) {
    this.id = id;
    this.name = name;
    this.preterit = preterit;
    this.description = description;
  }

  GriefEvent(@NotNull PrismEvent prismEvent,
             @NotNull String description) {
    this(prismEvent.getId(),
        prismEvent.getName(),
        prismEvent.getPastTense(),
        description);
  }

  @Override
  public String toString() {
    return getId();
  }

}
