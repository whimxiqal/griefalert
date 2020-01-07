/* Created by PietElite */

package com.minecraftonline.griefalert.api.structures;

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

  private HashMap<K, Stack<V>> data = new HashMap<>();

  @Nonnull
  @Override
  public Optional<V> pop(@Nonnull K key) {
    ensurePresent(key);
    if (data.get(key).isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(data.get(key).pop());
  }

  @Nonnull
  @Override
  public Optional<V> peek(@Nonnull K key) {
    ensurePresent(key);
    if (data.get(key).isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(data.get(key).peek());
  }

  @Nonnull
  @Override
  public Integer size(@Nonnull K key) {
    ensurePresent(key);
    return data.get(key).size();
  }

  @Override
  public void push(@Nonnull K key, @Nonnull V value) {
    ensurePresent(key);
    data.get(key).push(value);
  }

  @Override
  public boolean isEmpty(@Nonnull K key) {
    ensurePresent(key);
    return data.get(key).isEmpty();
  }

  @Override
  public void clear(@Nonnull K key) {
    ensurePresent(key);
    data.get(key).clear();
  }

  private void ensurePresent(K key) {
    data.putIfAbsent(key, new Stack<>());
  }
}
