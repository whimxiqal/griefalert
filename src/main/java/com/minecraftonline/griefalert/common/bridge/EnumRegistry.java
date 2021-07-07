/*
 * MIT License
 *
 * Copyright (c) 2021 Pieter Svenson
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

package com.minecraftonline.griefalert.common.bridge;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * A registry for enums to store them by a certain key rather
 * than just by their enum name.
 *
 * @param <E> the enum type
 * @author PietElite
 */
public class EnumRegistry<E extends Enum<E>> {

  private final Map<String, E> registry = new HashMap<>();
  private final Function<E, String> keyExtractor;
  private final Class<E> clazz;
  private boolean initialized = false;

  public EnumRegistry(Function<E, String> keyExtractor, Class<E> clazz) {
    this.keyExtractor = keyExtractor;
    this.clazz = clazz;
  }

  /**
   * Initialize the registry. Must be run at the start of the server.
   */
  public void initialize() {
    String key;
    for (E value : clazz.getEnumConstants()) {
      key = keyExtractor.apply(value);
      if (registry.containsKey(key)) {
        throw new IllegalStateException("Enum registry for " + clazz.getName()
            + " could not be initialized because there are multiple enums with the same key");
      }
      registry.put(keyExtractor.apply(value), value);
    }
    initialized = true;
  }

  /**
   * Get the enum from its key, or null if it doesn't exist.
   * The registry must be initialized first.
   *
   * @param key the key
   * @return the enum, or null if it doesn't exist
   */
  public E get(String key) {
    if (!initialized) {
      throw new IllegalStateException("Attempted to get enum of type " + clazz.getName()
          + " from its registry but it is not initialized yet");
    }
    return registry.get(key);
  }

  /**
   * Get the enum as an optional from its key.
   * The registry must be initialized first.
   *
   * @param key the key
   * @return the enum, or null if it doesn't exist
   */
  public Optional<E> getOptional(String key) {
    return Optional.ofNullable(get(key));
  }

  /**
   * Get the enum from its key and fail if it doesn't exist.
   * The registry must be initialized first.
   *
   * @param key the key
   * @return the enum
   * @throws IllegalArgumentException if the enum key could not be found
   */
  @NotNull
  public E require(String key) {
    E value = get(key);
    if (value == null) {
      throw new IllegalArgumentException("Could not find enum of type " + clazz.getName()
          + " with key " + key);
    }
    return value;
  }

  public Map<String, E> toMap() {
    return ImmutableMap.copyOf(registry);
  }

  public boolean contains(String key) {
    return registry.containsKey(key);
  }
}
