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

package com.minecraftonline.griefalert.api.configuration;

import com.google.common.reflect.TypeToken;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

/**
 * A setting which is used in the Configuration.
 *
 * @param <T> the type this setting's value
 */
public final class Setting<T> {

  private final Class<T> type;
  private final String name;
  private final T defaultValue;
  private final String comment;
  private final Predicate<T> verification;
  private final String errorMessage;
  private T value;

  private Setting(@Nonnull String name,
                  @Nonnull T defaultValue,
                  @Nonnull String comment,
                  @Nonnull Predicate<T> verification,
                  @Nonnull String errorMessage,
                  @Nonnull Class<T> type) {
    this.name = name;
    this.defaultValue = defaultValue;
    this.value = defaultValue;
    this.comment = comment;
    this.verification = verification;
    this.errorMessage = errorMessage;
    this.type = type;
  }

  /**
   * The {@link Setting} factory.
   *
   * @param name         the readable name to represent this setting
   * @param defaultValue this value is the one which is generated by default
   *                     and which is used by default if a custom one is
   *                     invalid.
   * @param comment      the description of this setting to display next to the value
   * @param verification a test to see if a given custom setting is valid
   * @param errorMessage the error message to send if a custom setting is invalid
   * @param type         the class of the type of the value
   * @param <Z>          the type of the value
   * @return the generated {@link Setting}
   */
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
  public T getDefaultValue() {
    return defaultValue;
  }

  @Nonnull
  public String getComment() {
    return comment;
  }

  @Nonnull
  public T getValue() {
    return value;
  }

  /**
   * Set this setting's value based on the input {@link ConfigurationNode}.
   *
   * @param node The configuration node
   * @throws IllegalStateException  if the node does not have this setting
   * @throws ObjectMappingException if the node contains the wrong type of value
   */
  public void setValueFromConfig(@Nonnull ConfigurationNode node)
      throws IllegalStateException, ObjectMappingException {
    if (node.getNode(this.getName()).isVirtual()) {
      throw new IllegalStateException("The setting "
          + name
          + " doesn't exist in the configuration. Using default value.");
    }
    this.value = node.getNode(this.getName()).getValue(TypeToken.of(type));
    verify();
  }

  /**
   * Ensure that this setting has a valid value.
   */
  private void verify() throws IllegalStateException {
    if (!verification.test(value)) {
      value = defaultValue;
      throw new IllegalStateException(String.format(
          "%s Using default value: %s",
          errorMessage,
          defaultValue));
    }
  }

}