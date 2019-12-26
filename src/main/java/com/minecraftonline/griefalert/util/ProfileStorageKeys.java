package com.minecraftonline.griefalert.util;

import com.minecraftonline.griefalert.api.profiles.StorageKey;

public final class ProfileStorageKeys {

  private ProfileStorageKeys() {
  }

  public static final StorageKey EVENT = StorageKey.of("event");
  public static final StorageKey TARGET = StorageKey.of("target");
  public static final StorageKey IGNORED_DIMENSIONS = StorageKey.of("ignoredDimensions");
  public static final StorageKey COLOR = StorageKey.of("color");
  public static final StorageKey EVENT_COLOR = StorageKey.of("event_color");
  public static final StorageKey TARGET_COLOR = StorageKey.of("target_color");
  public static final StorageKey DIMENSION_COLOR = StorageKey.of("dimension_color");

}
