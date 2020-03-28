/* Created by PietElite */

package com.minecraftonline.griefalert.api.structures;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * An implementation of <code>RotatingList</code>.
 *
 * @param <P> The type to store in the data structure
 * @author PietElite
 */
public class RotatingArrayList<P> implements RotatingList<P> {

  private final int capacity;
  private int cursor;
  private ArrayList<P> data;

  /**
   * The default constructor.
   *
   * @param capacity The capacity of this structure
   */
  public RotatingArrayList(int capacity) {
    this.capacity = capacity;
    cursor = 0;
    initializeDataArray();
  }

  /**
   * Constructor to convert an array directly into a rotating array list.
   *
   * @param inputData The input array
   * @param capacity  The capacity of the desired array
   */
  public RotatingArrayList(ArrayList<P> inputData, int capacity, int cursor) {
    this.capacity = capacity;
    this.cursor = cursor;
    initializeDataArray();
    for (int i = 0; i < Math.min(capacity, inputData.size()); i++) {
      data.set(i, inputData.get(i));
    }
  }

  @Override
  public int capacity() {
    return capacity;
  }

  @Override
  public int size() {
    return data.size();
  }

  @Override
  public int push(@Nonnull final P value) {
    data.set(cursor, value);
    int output = cursor;
    incrementCursor();
    return output;
  }

  @Nonnull
  @Override
  public P get(int index) throws IndexOutOfBoundsException {
    if (data.get(index) == null) {
      throw new IndexOutOfBoundsException();
    }
    return data.get(index);
  }

  @Nonnull
  @Override
  public List<P> getDataByTime() {
    ArrayList<P> output = new ArrayList<>();
    if (!isFull()) {
      output.addAll(data);
    } else {
      int localCursor = cursor;
      for (int i = 0; i < capacity; i++) {
        output.add(data.get(localCursor));
        localCursor = (localCursor + 1) % capacity;
      }
    }
    output.removeIf(Objects::isNull);
    output.trimToSize();
    return output;
  }

  @Nonnull
  @Override
  public List<P> getDataByIndex() {
    ArrayList<P> output = new ArrayList<>(capacity);
    output.addAll(data);
    output.removeIf(Objects::isNull);
    output.trimToSize();
    return output;
  }

  @Nonnull
  @Override
  public <S> RotatingList<S> map(Function<P, S> converter) {
    RotatingArrayList<S> output = new RotatingArrayList<>(this.capacity);
    output.cursor = this.cursor;
    output.data = this.data
        .stream()
        .map(item -> {
          if (item == null) {
            return null;
          } else {
            return converter.apply(item);
          }
        })
        .collect(Collectors.toCollection(Lists::newArrayList));
    return output;
  }

  @Override
  public boolean isFull() {
    return data.size() == capacity;
  }

  @Override
  public void clear() {
    this.data.clear();
    initializeDataArray();
    this.cursor = 0;
  }

  /**
   * Get the cursor index location.
   *
   * @return The cursor index location
   */
  public int cursor() {
    return cursor;
  }

  private void incrementCursor() {
    cursor = (cursor + 1) % capacity;
  }

  private void initializeDataArray() {
    data = new ArrayList<>(capacity);
    for (int i = 0; i < capacity; i++) {
      data.add(null);
    }
  }

  @Nonnull
  @Override
  public <Z extends java.io.Serializable>
      RotatingArrayList.Serializable<Z> serialize(Function<P, Z> converter) {
    return new Serializable<>(this, converter);
  }

  public static class Serializable<S extends java.io.Serializable> implements java.io.Serializable {
    private final int capacity;
    private final int cursor;
    private final ArrayList<S> data;

    <Z> Serializable(RotatingArrayList<Z> alertList, Function<Z, S> converter) {
      this.capacity = alertList.capacity();
      this.cursor = alertList.cursor();
      this.data = alertList.getDataByIndex()
          .stream()
          .filter(Objects::nonNull)
          .map(converter)
          .collect(Collectors.toCollection(Lists::newArrayList));
    }

    public RotatingArrayList<S> deserialize() {
      return new RotatingArrayList<>(data, capacity, cursor);
    }
  }

}
