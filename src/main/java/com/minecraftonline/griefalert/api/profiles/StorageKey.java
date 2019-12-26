package com.minecraftonline.griefalert.api.profiles;

public class StorageKey {

  private final String key;

  private StorageKey(String key) {
    this.key = key;
  }

  public static StorageKey of(String key) {
    return new StorageKey(key);
  }

  public String getKey() {
    return key;
  }
}
