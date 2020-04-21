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

package com.minecraftonline.griefalert.util;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.sponge.SpongeWorld;
import org.spongepowered.api.world.World;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class WorldEditUtil {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private WorldEditUtil() {
  }

  public static Vector3i convertVector(Vector weVector) {
    return new Vector3i(weVector.getBlockX(), weVector.getBlockY(), weVector.getBlockZ());
  }

  public static boolean containsSign(Region selection, SpongeWorld world) {
    Iterator<BlockVector> iterator = selection.iterator();
    while (iterator.hasNext()) {
      if (world.getWorld().getBlock(convertVector(iterator.next())).getId().contains("sign")) {
        return true;
      }
    }
    return false;
  }

}