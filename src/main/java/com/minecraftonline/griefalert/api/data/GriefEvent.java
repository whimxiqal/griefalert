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

package com.minecraftonline.griefalert.api.data;

import com.helion3.prism.api.data.PrismEvent;

import java.io.Serializable;
import javax.annotation.Nonnull;

import com.minecraftonline.griefalert.util.General;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

/**
 * An object to describe a specific action. This is one of the factors which match
 * certain in-game events with {@link com.minecraftonline.griefalert.api.records.GriefProfile}s
 * so that {@link com.minecraftonline.griefalert.api.alerts.Alert}s can be triggered.
 *
 * @author PietElite
 */
@CatalogedBy(GriefEvents.class)
public final class GriefEvent implements CatalogType, Serializable {

  private final String id;
  private final String name;
  private final String preterit;
  private final String description;

  private GriefEvent(String id, String name, String preterit, String description) {
    this.id = id;
    this.name = name;
    this.preterit = preterit;
    this.description = description;
  }

  /**
   * Factory method for a <code>GriefEvent</code>.
   *
   * @param id          The identifier. Must be alphanumeric.
   * @param name        The human-readable name. Must be alphabetic.
   * @param preterit    The past tense version of this event. Must be alphabetic.
   * @param description General information about the use of this GriefEvent
   * @return The corresponding generated <code>GriefEvent</code>
   */
  public static GriefEvent of(@Nonnull String id,
                              @Nonnull String name,
                              @Nonnull String preterit,
                              @Nonnull String description) throws IllegalArgumentException {
    if (!id.matches("^[a-zA-Z0-9]*$")) {
      throw new IllegalArgumentException(
          "GriefEvent id must be alphanumeric. Invalid: " + id);
    }
    if (!name.matches("^[a-zA-Z ]*$")) {
      throw new IllegalArgumentException(
          "GriefEvent name must be alphabetic. Invalid: " + name);
    }
    if (!preterit.matches("^[a-z ]*$")) {
      throw new IllegalArgumentException(
          "GriefEvent preterit must be lowercase. Invalid: " + preterit);
    }
    return new GriefEvent(id, name, preterit, description);
  }

  /**
   * Factory method for a <code>GriefEvent</code> given a <code>PrismEvent</code>, which are what
   * <code>GriefEvent</code>s are loosely based on.
   *
   * @param prismEvent The similar <code>PrismEvent</code>
   * @return The corresponding generated <code>GriefEvent</code>
   */
  public static GriefEvent of(PrismEvent prismEvent, String description) {
    return new GriefEvent(
        prismEvent.getId(),
        prismEvent.getName(),
        prismEvent.getPastTense(),
        description);
  }

  @Override
  @Nonnull
  public String getId() {
    return id;
  }

  @Override
  @Nonnull
  public String getName() {
    return name;
  }

  @Nonnull
  public String getPreterit() {
    return preterit;
  }

  @Override
  public String toString() {
    return getId();
  }

  public String getDescription() {
    return this.description;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof GriefEvent)) {
      return false;
    }
    return this.getId().equals(((GriefEvent) obj).getId());
  }
}
