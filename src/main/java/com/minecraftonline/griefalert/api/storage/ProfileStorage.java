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

package com.minecraftonline.griefalert.api.storage;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * An interface for persistent storage of {@link GriefProfile}s.
 *
 * @author PietElite
 */
public interface ProfileStorage {

  /**
   * Write a GriefProfile into persistent storage.
   *
   * @param profile The GriefProfile to add
   * @return false if a <code>GriefProfile</code> already exists too similar to the
   *         input <code>GriefProfile</code>
   * @throws Exception if error
   */
  boolean write(@Nonnull final GriefProfile profile) throws Exception;

  /**
   * Remove the {@link GriefProfile} from persistent storage.
   *
   * @param griefEvent The <code>GriefEvent</code> of this profile to remove
   * @param target     The target id of this profile to remove
   * @return false if a <code>GriefProfile</code> was not found
   * @throws Exception if error
   */
  boolean remove(@Nonnull final GriefEvent griefEvent, @Nonnull final String target)
      throws Exception;

  /**
   * Get all <code>GriefProfile</code>s saved in persistent storage.
   *
   * @return a list of <code>GriefProfile</code>s
   * @throws Exception if error
   */
  @Nonnull
  List<GriefProfile> retrieve() throws Exception;

}
