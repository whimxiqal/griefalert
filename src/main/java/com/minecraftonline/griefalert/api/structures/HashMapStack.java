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

package com.minecraftonline.griefalert.api.structures;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Optional;
import java.util.Stack;
import javax.annotation.Nonnull;

/**
 * An implementation of a <code>MapStack</code> using a {@link HashMap} as a data
 * structure to hold the mapping between the key and the {@link Stack} of values.
 *
 * @param <K> The key type
 * @param <V> The value type
 * @author PietElite
 */
public class HashMapStack<K, V> implements MapStack<K, V> {

  private final HashMap<K, Stack<V>> data = Maps.newHashMap();

  @Nonnull
  @Override
  public Optional<V> pop(@Nonnull final K key) {
    ensurePresent(key);
    if (data.get(key).isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(data.get(key).pop());
  }

  @Nonnull
  @Override
  public Optional<V> peek(@Nonnull final K key) {
    ensurePresent(key);
    if (data.get(key).isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(data.get(key).peek());
  }

  @Nonnull
  @Override
  public Integer size(@Nonnull final K key) {
    ensurePresent(key);
    return data.get(key).size();
  }

  @Override
  public void push(@Nonnull final K key, @Nonnull final V value) {
    ensurePresent(key);
    data.get(key).push(value);
  }

  @Override
  public boolean isEmpty(@Nonnull final K key) {
    ensurePresent(key);
    return data.get(key).isEmpty();
  }

  @Override
  public void clear(@Nonnull final K key) {
    ensurePresent(key);
    data.get(key).clear();
  }

  @Override
  public void clearAll() {
    data.clear();
  }

  private void ensurePresent(@Nonnull final K key) {
    data.putIfAbsent(key, new Stack<>());
  }
}
