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

package com.minecraftonline.griefalert.api.alerts;

import com.minecraftonline.griefalert.util.Format;

import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Detail<P> {

  private final String label;
  private final String description;
  private final Function<P, Optional<Text>> infoFunction;
  private final boolean primary;

  private Detail(@Nonnull String label,
                 @Nullable String description,
                 @Nonnull Function<P, Optional<Text>> infoFunction,
                 boolean primary) {
    this.label = label;
    this.description = description;
    this.infoFunction = infoFunction;
    this.primary = primary;
  }

  public static <S> Detail<S> of(@Nonnull String label,
                                 @Nullable String description,
                                 @Nonnull Function<S, Optional<Text>> infoSupplier) {
    return new Detail<>(label, description, infoSupplier, false);
  }

  public static <S> Detail<S> of(@Nonnull String label,
                                 @Nullable String description,
                                 @Nonnull Function<S, Optional<Text>> infoSupplier,
                                 boolean primary) {
    return new Detail<>(label, description, infoSupplier, primary);
  }

  public static <S> Detail<S> of(@Nonnull String label,
                                 @Nullable String description,
                                 @Nullable Text info) {
    return new Detail<>(label, description, s -> Optional.ofNullable(info), false);
  }

  public static <S> Detail<S> of(@Nonnull String label,
                                 @Nullable String description,
                                 @Nullable Text info,
                                 boolean primary) {
    return new Detail<>(label, description, s -> Optional.ofNullable(info), primary);
  }

  /**
   * Get the formatted {@link Text} version of this detail.
   *
   * @param item the object in which the detail is stored
   * @return the formatted {@link Text}
   */
  public Optional<Text> get(@Nonnull P item) {
    return infoFunction.apply(item)
        .map(info -> Text.joinWith(
            Format.bonus(": "),
            Text.of(
                TextColors.DARK_AQUA,
                description == null ? Text.of(label) : Format.hover(label, description)),
            Format.bonus(info)));
  }

  public boolean isPrimary() {
    return primary;
  }

  public SerializedDetail serialize(P item) {
    return new SerializedDetail(this, item);
  }

  public static final class SerializedDetail implements Serializable {

    private final String label;
    private final String description;
    private final String info;
    private final boolean primary;

    private <S> SerializedDetail(Detail<S> detail, S item) {
      this.label = detail.label;
      this.description = detail.description;
      this.info = detail.infoFunction.apply(item).map(Text::toContainer).map(data -> {
        try {
          return DataFormats.JSON.write(data);
        } catch (IOException e) {
          // Shouldn't happen
          e.printStackTrace();
          return null;
        }
      }).orElse(null);
      this.primary = detail.primary;
    }

    /**
     * Convert this serialized object back to a {@link Detail}.
     * @param type The class of the type parameter of the original {@link Detail}
     * @param <S> The type parameter of the original {@link Detail}
     * @return the deserialized object
     */
    public <S> Detail<S> deserialize(Class<S> type) {
      return Detail.of(
          label,
          description,
          o -> Optional.ofNullable(info).flatMap(info -> {
            try {
              return Sponge.getDataManager().deserialize(Text.class, DataFormats.JSON.read(info));
            } catch (IOException e) {
              // Shouldn't happen
              e.printStackTrace();
              return Optional.of(Text.EMPTY);
            }
          }),
          primary);
    }

  }
}
