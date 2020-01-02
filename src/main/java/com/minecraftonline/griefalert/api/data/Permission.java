package com.minecraftonline.griefalert.api.data;

public class Permission {

  private final String permissionString;

  public Permission(String permissionString) {
    this.permissionString = permissionString;
  }

  public String toString() {
    return permissionString;
  }

}