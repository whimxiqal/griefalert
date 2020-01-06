/* Created by PietElite */

package com.minecraftonline.griefalert.api.structures;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class RotatingStack<P> {

  private int capacity;
  private int cursor;
  private int size;
  private ArrayList<P> data;
  private boolean isFull = false;

  public RotatingStack(int capacity) {
    this.capacity = capacity;
    cursor = 0;
    size = 0;
    data = new ArrayList<>(capacity);
    for (int i = 0; i < capacity; i++) {
      data.add(null);
    }
  }

  private void incrementCursor() {
    cursor = (cursor + 1) % capacity;
  }

  private void incrementSize() {
    if (!isFull && size < capacity) {
      size++;
      if (size == capacity) {
        isFull = true;
      }
    }
  }

  public int capacity() {
    return capacity;
  }

  protected int cursor() {
    return cursor;
  }

  public int size() {
    return size;
  }

  public int push(P value) {
    data.set(cursor, value);
    int output = cursor;
    incrementCursor();
    incrementSize();
    return output;
  }

  public P get(int index) throws IndexOutOfBoundsException {
    if (data.get(index) == null) {
      throw new IndexOutOfBoundsException();
    }
    return data.get(index);
  }

  public List<P> getDataByTime() {
    List<P> output = new LinkedList<>();
    if (!isFull) {
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

  public List<P> getDataByIndex() {
    return data.subList(0, size);
  }

}
