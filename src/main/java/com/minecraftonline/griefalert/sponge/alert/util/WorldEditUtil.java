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

package com.minecraftonline.griefalert.sponge.alert.util;

import com.flowpowered.math.vector.Vector3i;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.sponge.SpongeWorld;

/**
 * A utility class to handle WorldEdit behaviors.
 */
public final class WorldEditUtil {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private WorldEditUtil() {
  }

  public static Vector3i convertVector(Vector weVector) {
    return new Vector3i(weVector.getBlockX(), weVector.getBlockY(), weVector.getBlockZ());
  }

  /**
   * Returns whether this {@link Region} has an object with "sign" in its id.
   *
   * @param selection the selection
   * @param world     the world
   * @return true if a sign is in selection
   */
  public static boolean containsSign(Region selection, SpongeWorld world) {
    for (BlockVector blockVector : selection) {
      if (world.getWorld().getBlock(convertVector(blockVector)).getId().contains("sign")) {
        return true;
      }
    }
    return false;
  }

}