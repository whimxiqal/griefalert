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

  /**
   * Set this setting's value based on the input {@link ConfigurationNode}
   * @param node The configuration node
   * @throws RuntimeException if the node does not have this setting
   * @throws ObjectMappingException if the node contains the wrong type of value
   */
  public void setValueFromConfig(@Nonnull ConfigurationNode node)
      throws RuntimeException, ObjectMappingException {
    if (node.getNode(this.getName()).isVirtual()) {
      throw new RuntimeException("The setting " + name + " doesn't exist in the configuration. Using default value.");
    }
    this.value = node.getNode(this.getName()).getValue(TypeToken.of(getType()));
    verify();
  }

  /**
   * Ensure that this setting has a valid value.
   */
  public void verify() throws IllegalStateException {
    if (!verification.test(value)) {
      value = defaultValue;
      throw new IllegalStateException(String.format("%s Using default value: %s", errorMessage, defaultValue.toString()));
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
