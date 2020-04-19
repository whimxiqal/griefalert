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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

public class Detail<P extends Serializable> implements Serializable {

  private String label;
  private String description;
  private Function<P, Optional<Text>> infoFunction;
  private boolean primary;
  private Text formatted = null;

  private Detail(@Nonnull String label,
                 @Nullable String description,
                 @Nonnull Function<P, Optional<Text>> infoFunction,
                 boolean primary) {
    this.label = label;
    this.description = description;
    this.infoFunction = infoFunction;
    this.primary = primary;
  }

  public static <S extends Serializable> Detail<S> of(@Nonnull String label,
                                                      @Nullable String description,
                                                      @Nonnull Function<S, Optional<Text>> infoSupplier) {
    return new Detail<>(label, description, infoSupplier, false);
  }

  public static <S extends Serializable> Detail<S> of(@Nonnull String label,
                                                      @Nullable String description,
                                                      @Nonnull Function<S, Optional<Text>> infoSupplier,
                                                      boolean primary) {
    return new Detail<>(label, description, infoSupplier, primary);
  }

  public static <S extends Serializable> Detail<S> of(@Nonnull String label,
                                                      @Nullable String description,
                                                      @Nullable Text info) {
    return new Detail<>(label, description, s -> Optional.ofNullable(info), false);
  }

  public static <S extends Serializable> Detail<S> of(@Nonnull String label,
                                                      @Nullable String description,
                                                      @Nullable Text info,
                                                      boolean primary) {
    return new Detail<>(label, description, s -> Optional.ofNullable(info), primary);
  }

  private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    label = in.readUTF();
    description = in.readUTF();
    primary = in.readBoolean();
    try {
      formatted = Sponge.getDataManager().deserialize(Text.class, DataFormats.JSON.read(in.readUTF())).orElse(null);
    } catch (IOException e) {
      // Shouldn't happen
      e.printStackTrace();
      formatted = null;
    }
    infoFunction = item -> Optional.empty();
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeUTF(label);
    out.writeUTF(description);
    out.writeBoolean(primary);
    try {
      out.writeUTF(DataFormats.JSON.write(Optional.ofNullable(formatted).orElse(Text.EMPTY).toContainer()));
    } catch (IOException e) {
      // Shouldn't happen
      e.printStackTrace();
    }
  }


  /**
   * Get the formatted {@link Text} version of this detail.
   *
   * @param item the object in which the detail is stored
   * @return the formatted {@link Text}
   */
  public Optional<Text> get(@Nonnull P item) {
    if (formatted != null) {
      return Optional.of(formatted);
    }
    formatted = infoFunction.apply(item)
        .map(info -> Text.joinWith(
            Text.of(TextColors.GRAY, ": "),
            Text.of(
                TextColors.DARK_AQUA,
                description == null
                        ? Text.of(label)
                        : Text.builder(label).onHover(TextActions.showText(Text.of(description))),
            Text.of(TextColors.GRAY, info))))
        .orElse(null);
    return Optional.ofNullable(formatted);
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

    private <S extends Serializable> SerializedDetail(Detail<S> detail, S item) {
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
     *
     * @param type The class of the type parameter of the original {@link Detail}
     * @param <S>  The type parameter of the original {@link Detail}
     * @return the deserialized object
     */
    public <S extends Serializable> Detail<S> deserialize(Class<S> type) {
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
