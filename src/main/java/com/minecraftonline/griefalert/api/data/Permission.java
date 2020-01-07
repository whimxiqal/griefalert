/* Created by PietElite */

package com.minecraftonline.griefalert.api.data;

/**
 * An object to describe a permission to track which commands and actions can
 * be completed by people with certain levels of privilege.
 *
 * @author PietElite
 */
public class Permission {

  private final String perm;

  private Permission(String perm) {
    this.perm = perm;
  }

  /**
   * Factory method of a <code>Permission</code>.
   * @param perm The string representation of the <code>Permission</code>
   * @return The generated <code>Permission</code>
   */
  public static Permission of(String perm) {
    return new Permission(perm);
  }

  @Override
  public String toString() {
    return perm;
  }

}