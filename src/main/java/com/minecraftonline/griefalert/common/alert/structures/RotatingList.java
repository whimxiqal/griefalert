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

package com.minecraftonline.griefalert.common.alert.structures;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;

/**
 * An interface for a data structure for managing a stack of items with a fixed capacity.
 * Elements of type {@link P} can be added to the list and if the capacity
 * is reached, then the location at which the element is added is wrapped around to
 * the beginning. The previous element at that index is removed and future elements are
 * added sequentially, replacing previous elements in the list.
 *
 * @param <P> The type to store in the data structure
 * @author PietElite
 */
public interface RotatingList<P> extends Serializable {

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
   * oldest first. Does not contain null objects.
   *
   * @return A <code>List</code> of all stored values
   */
  @Nonnull
  List<P> getDataByTime();

  /**
   * Get the stored values in index order, with the
   * lowest index first. Does not contain null objects.
   *
   * @return A <code>List</code> of all stored values
   */
  @Nonnull
  List<P> getDataByIndex();

  /**
   * Map a rotating list to store a different type of data.
   *
   * @param converter The converting Function
   * @param <S>       The new generic type
   * @return The new RotatingList
   */
  @Nonnull
  <S> RotatingList<S> map(Function<P, S> converter);

  /**
   * Return whether the structure has as many items as
   * its capacity.
   *
   * @return true if the size is equal to the capacity
   */
  boolean isFull();

  /**
   * Remove all objects and reset cursor.
   */
  void clear();

}
