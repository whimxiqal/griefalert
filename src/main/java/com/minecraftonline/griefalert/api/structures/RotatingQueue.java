package com.minecraftonline.griefalert.api.structures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class RotatingQueue<P> {

  private int capacity;
  private int cursor;
  private ArrayList<P> data;
  boolean isFull = false;

  public RotatingQueue(int capacity) {
    this.capacity = capacity;
    cursor = 0;
    data = new ArrayList<>(capacity);
  }

  private void incrementCursor() {
    if (cursor == capacity - 1) {
      isFull = true;
    }
    cursor = (cursor + 1) % capacity;
  }

  public int getCapacity() {
    return capacity;
  }

  public int getCursor() {
    return cursor;
  }

  public int push(P value) {
    data.set(cursor, value);
    int output = cursor;
    incrementCursor();
    return output;
  }

  public P get(int index) throws IndexOutOfBoundsException {
    return data.get(index);
  }

  public List<P> getChronologicalData() {
    List<P> output = new LinkedList<>();
    if (!isFull) {
      Collections.copy(output, data);
    } else {
      int localCursor = cursor;
      for (int i = 0; i < capacity; i++) {
        output.add(data.get(localCursor));
        localCursor = (localCursor + 1) % capacity;
      }
    }
    return output;
  }

  public List<P> getAntiChronologicalData() {
    List<P> output = new LinkedList<>();
    if (!isFull) {
      Collections.copy(output, data);
      Collections.reverse(output);
    } else {
      int localCursor = cursor;
      for (int i = 0; i < capacity; i++) {
        localCursor = (localCursor - 1) % capacity;
        output.add(data.get(localCursor));
      }
    }
    return output;
  }

}
