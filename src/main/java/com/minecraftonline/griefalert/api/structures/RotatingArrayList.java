/* Created by PietElite */

package com.minecraftonline.griefalert.api.structures;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * An abstract implementation of <code>RotatingList</code>.
 *
 * @param <P> The type to store in the data structure
 * @author PietElite
 */
public abstract class RotatingArrayList<P> implements RotatingList<P> {

  private final int capacity;
  private int cursor;
  private int size;
  private ArrayList<P> data;

  /**
   * The default constructor.
   *
   * @param capacity The capacity of this structure
   */
  public RotatingArrayList(int capacity) {
    this.capacity = capacity;
    cursor = 0;
    size = 0;
    data = new ArrayList<>(capacity);
    for (int i = 0; i < capacity; i++) {
      data.add(null);
    }
  }

  @Override
  public int capacity() {
    return capacity;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public int push(@Nonnull final P value) {
    data.set(cursor, value);
    int output = cursor;
    incrementCursor();
    incrementSize();
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
    List<P> output = new LinkedList<>();
    if (!isFull()) {
      output.addAll(data.subList(0, size));
    } else {
      int localCursor = cursor;
      for (int i = 0; i < capacity; i++) {
        output.add(data.get(localCursor));
        localCursor = (localCursor + 1) % capacity;
      }
    }
    return output;
  }

  @Nonnull
  @Override
  public List<P> getDataByIndex() {
    return data.subList(0, size);
  }

  @Override
  public boolean isFull() {
    return size == capacity;
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

  private void incrementSize() {
    size = Math.min(size + 1, capacity);
  }

}
