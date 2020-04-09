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
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;


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
  public static Optional<String> getItemFrameContent(EntitySnapshot entitySnapshot) {
    return entitySnapshot.toContainer().getView(DataQuery.of("UnsafeData"))
        .flatMap(unsafeView -> unsafeView.getView(DataQuery.of("Item")))
        .flatMap(itemView -> itemView.getString(DataQuery.of("id")));
  }

  /**
   * Get the contents of an armor stand.
   *
   * @param entitySnapshot the entity snapshot of type item frame
   * @return an optional of the list of content ids
   */
  public static Optional<List<String>> getArmorStandContent(EntitySnapshot entitySnapshot) {
    // TODO simplify optional logic
    DataContainer container = entitySnapshot.toContainer();

    Optional<DataView> unsafeOptional = container.getView(DataQuery.of("UnsafeData"));
    if (!unsafeOptional.isPresent()) {
      return Optional.empty();
    }

    Optional<List<DataView>> attributesOptional = unsafeOptional.get()
        .getViewList(DataQuery.of("ArmorItems"));
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

}
