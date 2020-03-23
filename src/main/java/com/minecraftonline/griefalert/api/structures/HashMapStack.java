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
