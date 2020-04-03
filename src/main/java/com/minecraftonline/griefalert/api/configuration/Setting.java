/* Created by PietElite */

package com.minecraftonline.griefalert.api.configuration;

import com.google.common.reflect.TypeToken;
import com.minecraftonline.griefalert.GriefAlert;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;


public class Setting<T> {

  private final Class<T> type;
  private final String name;
  private final T defaultValue;
  private final String comment;
  private final Predicate<T> verification;
  private final String errorMessage;
  private T value;

  private Setting(String name,
                  T defaultValue,
                  String comment,
                  Predicate<T> verification,
                  String errorMessage,
                  Class<T> type) {
    this.name = name;
    this.defaultValue = defaultValue;
    this.value = defaultValue;
    this.comment = comment;
    this.verification = verification;
    this.errorMessage = errorMessage;
    this.type = type;
  }

  public static <Z> Setting<Z> of(@Nonnull String name,
                                  @Nonnull Z defaultValue,
                                  @Nonnull String comment,
                                  @Nonnull Predicate<Z> verification,
                                  @Nonnull String errorMessage,
                                  @Nonnull Class<Z> type) {
    return new Setting<>(name, defaultValue, comment, verification, errorMessage, type);
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
    verify();
  }

  /**
   * Ensure that this setting has a valid value.
   */
  public void verify() {
    if (!verification.test(value)) {
      GriefAlert.getInstance().getLogger().error(
          errorMessage
              + "Using default value: "
              + defaultValue.toString());
      value = defaultValue;
    }
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
