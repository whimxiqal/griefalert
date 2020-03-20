package com.minecraftonline.griefalert.util;

import com.flowpowered.math.vector.Vector3i;
import com.sk89q.worldedit.Vector;

public final class WorldEditUtil {

  /**
   * Ensure util class cannot be instantiated with private constructor.
   */
  private WorldEditUtil() {
  }

  public static Vector3i convertVector(Vector weVector) {
    return new Vector3i(weVector.getBlockX(), weVector.getBlockY(), weVector.getBlockZ());
  }

}