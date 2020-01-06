/* Created by PietElite */

package com.minecraftonline.griefalert.api.data;

public class Permission {

  private final String perm;

  private Permission(String perm) {
    this.perm = perm;
  }

  public static Permission of(String perm) {
    return new Permission(perm);
  }

  public String toString() {
    return perm;
  }

}