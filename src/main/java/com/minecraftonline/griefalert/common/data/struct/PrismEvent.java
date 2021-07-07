/*
 * This file is part of Prism, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.minecraftonline.griefalert.common.data.struct;

import com.minecraftonline.griefalert.common.bridge.EnumRegistry;
import com.minecraftonline.griefalert.sponge.data.records.BlockResult;
import com.minecraftonline.griefalert.sponge.data.records.EntityResult;
import com.minecraftonline.griefalert.sponge.data.records.Result;
import com.minecraftonline.griefalert.sponge.data.records.ResultComplete;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * An event type that is logged in database.
 *
 * @author viveleroi
 * @author PietElite
 */
public enum PrismEvent {

  BLOCK_BREAK("break", "Block Break", "broke", BlockResult.class),
  BLOCK_DECAY("decay", "Block Decay", "decayed", BlockResult.class),
  BLOCK_GROW("grow", "Block Grow", "grew", BlockResult.class),
  BLOCK_PLACE("place", "Block Place", "placed", BlockResult.class),
  ENTITY_DEATH("death", "Entity Death", "killed", EntityResult.class),
  COMMAND_EXECUTE("command", "Command Execute", "executed", ResultComplete.class),
  INVENTORY_CLOSE("close", "Inventory Close", "closed", ResultComplete.class),
  INVENTORY_OPEN("open", "Inventory Open", "opened", ResultComplete.class),
  ITEM_DROP("drop", "Item Drop", "dropped", ResultComplete.class),
  ITEM_INSERT("insert", "Item Insert", "inserted", ResultComplete.class),
  ITEM_PICKUP("pickup", "Item Pickup", "picked up", ResultComplete.class),
  ITEM_REMOVE("remove", "Item Remove", "removed", ResultComplete.class),
  PLAYER_DISCONNECT("disconnect", "Player Disconnect", "left", ResultComplete.class),
  PLAYER_JOIN("join", "Player Join", "joined", ResultComplete.class);

  @Getter
  @NotNull
  private static final EnumRegistry<PrismEvent> registry = new EnumRegistry<>(event ->
      event.id, PrismEvent.class);
  @Getter
  @NotNull
  private final String id;
  @Getter
  @NotNull
  private final String name;
  @Getter
  @NotNull
  private final String pastTense;
  @Getter
  @NotNull
  private final Class<? extends Result> resultClass;

  PrismEvent(@NotNull String id,
             @NotNull String name,
             @NotNull String pastTense,
             @NotNull Class<? extends Result> resultClass) {
    this.id = id;
    this.name = name;
    this.pastTense = pastTense;
    this.resultClass = resultClass;
  }

}