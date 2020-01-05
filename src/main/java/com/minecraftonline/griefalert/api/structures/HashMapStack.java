/* Created by PietElite */

package com.minecraftonline.griefalert.api.structures;

import java.util.HashMap;
import java.util.Optional;
import java.util.Stack;

public class HashMapStack<K, V> implements MapStack<K, V> {

  private HashMap<K, Stack<V>> data = new HashMap<>();

  @Override
  public Optional<V> pop(K key) {
    ensurePresent(key);
    if (data.get(key).isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(data.get(key).pop());
  }

  @Override
  public Optional<V> peek(K key) {
    ensurePresent(key);
    if (data.get(key).isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(data.get(key).peek());
  }

  @Override
  public Integer size(K key) {
    ensurePresent(key);
    return data.get(key).size();
  }

  @Override
  public void push(K key, V value) {
    ensurePresent(key);
    data.get(key).push(value);
  }

  @Override
  public boolean isEmpty(K key) {
    ensurePresent(key);
    return data.get(key).isEmpty();
  }

  @Override
  public void clear(K key) {
    ensurePresent(key);
    data.get(key).clear();
  }

  private void ensurePresent(K key) {
    data.putIfAbsent(key, new Stack<>());
  }
}
