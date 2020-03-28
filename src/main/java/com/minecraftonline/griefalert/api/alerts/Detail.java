package com.minecraftonline.griefalert.api.alerts;

import com.minecraftonline.griefalert.util.Format;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class Detail {

  private final @Nonnull String label;
  private final @Nullable String description;
  private final @Nonnull Function<Alert, Optional<Text>> infoSupplier;

  private Detail(@Nonnull String label,
                 @Nullable String description,
                 @Nonnull Function<Alert, Optional<Text>> infoSupplier) {
    this.label = label;
    this.description = description;
    this.infoSupplier = infoSupplier;
  }

  public static Detail of(@Nonnull String label,
                          @Nullable String description,
                          @Nonnull Function<Alert, Optional<Text>> infoSupplier) {
    return new Detail(label, description, infoSupplier);
  }

  public static Detail of(@Nonnull String label,
                          @Nullable String description,
                          @Nullable Text info) {
    return new Detail(label, description, alert -> Optional.ofNullable(info));
  }

  public Optional<Text> get(@Nonnull Alert alert) {
    return infoSupplier.apply(alert)
        .map(info -> Text.joinWith(
            Format.bonus(": "),
            Text.of(
                TextColors.DARK_AQUA,
                description == null ? Text.of(label) : Format.hover(label, description)),
            Format.bonus(info)));
  }
}
