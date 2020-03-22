package com.minecraftonline.griefalert.api.configuration;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import javax.annotation.Nonnull;

public class Setting<T> {

  private final Class<T> type;
  private final String name;
  private final T defaultValue;
  private final String comment;
  private T value;

  private Setting(String name, T defaultValue, String comment, Class<T> type) {
    this.name = name;
    this.defaultValue = defaultValue;
    this.value = defaultValue;
    this.comment = comment;
    this.type = type;
  }

  public static <Z> Setting<Z> of(@Nonnull String name,
                                  @Nonnull Z defaultValue,
                                  @Nonnull String comment,
                                  @Nonnull Class<Z> type) {
    return new Setting<>(name, defaultValue, comment, type);
  }

  @Nonnull
  public String getName() {
    return name;
  }

  @Nonnull
  public Object getDefaultValue() {
    return defaultValue;
  }

  @Nonnull
  public String getComment() {
    return comment;
  }

  public void setValueFromConfig(@Nonnull ConfigurationNode node) throws ObjectMappingException {
    this.value = node.getNode(this.getName()).getValue(TypeToken.of(getType()));
  }

  @Nonnull
  public T getValue() {
    return value;
  }

  @Nonnull
  public Class<T> getType() {
    return type;
  }

}
