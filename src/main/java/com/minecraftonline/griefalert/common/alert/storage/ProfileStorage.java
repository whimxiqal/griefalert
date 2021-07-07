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

package com.minecraftonline.griefalert.common.alert.storage;

import com.minecraftonline.griefalert.common.alert.struct.GriefEvent;
import com.minecraftonline.griefalert.common.alert.records.GriefProfile;
import java.util.Collection;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An interface for persistent storage of {@link GriefProfile}s.
 *
 * @author PietElite
 */
public interface ProfileStorage {

  /**
   * Write a {@link GriefProfile} into persistent storage.
   *
   * @param profile The GriefProfile to add
   * @return false if a grief profile already exists too similar to the input grief profile
   * @throws Exception if error
   */
  boolean write(@Nonnull final GriefProfile profile) throws Exception;

  /**
   * Remove the {@link GriefProfile} from persistent storage.
   *
   * @param griefEvent The {@link GriefEvent} of this profile to remove
   * @param target     The target id of this profile to remove
   * @return false if a {@link GriefProfile} was not found
   * @throws Exception if error
   */
  boolean remove(@Nonnull final GriefEvent griefEvent, @Nonnull final String target)
      throws Exception;

  /**
   * Gets a copy of the grief profile.
   *
   * @param griefEvent The {@link GriefEvent} of this profile to see
   * @param target     The target id of this profile to see
   * @return the profile, or null if none exists
   * @throws Exception if error
   */
  @Nullable
  GriefProfile get(@Nonnull final GriefEvent griefEvent, @Nonnull final String target) throws Exception;

  /**
   * Get all {@link GriefProfile}s saved in persistent storage.
   *
   * @return a collection of {@link GriefProfile}s
   * @throws Exception if error
   */
  @Nonnull
  Collection<GriefProfile> retrieve() throws Exception;

}
