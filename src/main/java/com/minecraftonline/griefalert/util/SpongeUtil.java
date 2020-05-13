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

package com.minecraftonline.griefalert.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;


public final class SpongeUtil {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private SpongeUtil() {
  }

  /**
   * Get the contents of an item frame.
   *
   * @param entitySnapshot the entity snapshot of type item frame
   * @return an optional of the content id
   */
  public static Optional<String> getItemFrameContent(DataContainer entitySnapshot) {
    return entitySnapshot.getString(DataQuery.of("UnsafeData").then("Item").then("id"));
  }

  /**
   * Get the contents of an armor stand.
   *
   * @param entitySnapshot the entity snapshot of type item frame
   * @return an optional of the list of content ids
   */
  public static Optional<List<String>> getArmorStandContent(DataContainer entitySnapshot) {

    Optional<List<DataView>> attributesOptional = entitySnapshot.getViewList(DataQuery.of("UnsafeData").then("ArmorItems"));
    if (!attributesOptional.isPresent()) {
      return Optional.empty();
    }

    List<String> output = new LinkedList<>();
    for (DataView view : attributesOptional.get()) {
      if (view.contains(DataQuery.of("id"))) {
        view.getString(DataQuery.of("id")).ifPresent(output::add);
      }
    }
    return Optional.of(output);
  }

  public static Optional<User> getUser(UUID userUuid) {
    return Sponge.getServiceManager().provide(UserStorageService.class).flatMap(users -> users.get(userUuid));
  }

  public static Optional<User> getUser(String username) {
    return Sponge.getServiceManager().provide(UserStorageService.class).flatMap(users -> users.get(username));
  }

  public static Optional<World> getWorld(UUID worldUuid) {
    return Sponge.getServer().getWorld(worldUuid);
  }

  public static Optional<WorldProperties> firstWorldWithDimension(DimensionType dimensionType) {
    for (WorldProperties world : Sponge.getServer().getUnloadedWorlds()) {
      if (world.getDimensionType().equals(dimensionType)) {
        return Optional.of(world);
      }
    }
    return Optional.empty();
  }

}
