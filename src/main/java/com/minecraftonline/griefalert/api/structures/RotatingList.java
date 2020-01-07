/* Created by PietElite */

package com.minecraftonline.griefalert.api.structures;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * An interface for a data structure for managing a stack of items with a fixed capacity.
 * Elements of type <code>P</code> can be added to the list and if the capacity
 * is reached, then the location at which the element is added is wrapped around to
 * the beginning. The previous element at that index is removed and future elements are
 * added sequentially, replacing previous elements in the list.
 *
 * @param <P> The type to store in the data structure
 * @author PietElite
 */
public interface RotatingList<P> {

  /**
   * Get capacity.
   *
   * @return the capacity
   */
  @SuppressWarnings("unused")
  int capacity();

  /**
   * Get the quantity of elements in storage.
   *
   * @return The quantity
   */
  int size();

  /**
   * Push a value onto the list.
   *
   * @param value the value
   * @return the index at which the value is stored
   */
  int push(@Nonnull final P value);

  /**
   * Get the current location of the cursor. This is the location where
   * the next added element will go.
   *
   * @return the index where the next element will be assigned
   */
  @SuppressWarnings("unused")
  int cursor();

  /**
   * Retrieve the value at a specific index.
   *
   * @param index the index at which to search
   * @return the value at the given index
   * @throws IndexOutOfBoundsException if queried index is not valid
   */
  @Nonnull
  P get(int index) throws IndexOutOfBoundsException;

  /**
   * Get the stored values in chronological order, with the
   * oldest first.
   *
   * @return A <code>List</code> of all stored values
   */
  @Nonnull
  List<P> getDataByTime();

  /**
   * Get the stored values in index order, with the
   * lowest index first.
   *
   * @return A <code>List</code> of all stored values
   */
  @Nonnull
  List<P> getDataByIndex();

  /**
   * Return whether the structure has as many items as
   * its capacity.
   *
   * @return true if the size is equal to the capacity
   */
  boolean isFull();
}
