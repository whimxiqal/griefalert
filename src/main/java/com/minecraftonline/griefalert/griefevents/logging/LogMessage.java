package com.minecraftonline.griefalert.griefevents.logging;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.tools.General;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class LogMessage implements Comparable<LogMessage> {

  HashMap<Factory.ComponentType, String> tokens = new HashMap<>();
  private Date date;

  public void putDetail(Factory.ComponentType type, String value) {
    tokens.put(type, value);
  }

  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * Return a non-null value from the token HashMap, which stores all
   * the information about this log entry. If there is no String value for
   * a log component of this entry, it returns "ERR".
   *
   * @param type The component type to get from this entry
   * @return The value associated with this component or "ERR" if none exists
   */
  private String getNonNull(Factory.ComponentType type) {
    String output = tokens.get(type);
    if (output == null) {
      return "ERR";
    } else {
      return output;
    }
  }

  private String getFormattedDate(Date date) {
    if (date == null) {
      return "NO TIME";
    }
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
    format.setTimeZone(TimeZone.getTimeZone("Europe/London"));
    return format.format(date);
  }

  public Text asText() {
    Text.Builder builder = Text.builder()
        .color(TextColors.GOLD)
        .append(Text.of(getFormattedDate(date)))
        .append(Text.of(" - "));
    Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(UUID.fromString(getNonNull(Factory.ComponentType.PLAYER_UUID)));
    if (optionalPlayer.isPresent()) {
      builder.append(General.formatPlayerName(optionalPlayer.get()));
    } else {
      builder.append(Text.of(getNonNull(Factory.ComponentType.PLAYER_UUID)));
    }
    builder
        .append(Text.of(" - "))
        .append(Text.of(GriefAlert.GriefType.from(getNonNull(Factory.ComponentType.GRIEF_TYPE)).toPreteritVerb()))
        .append(Text.of(" - "))
        .append(Text.of(getNonNull(Factory.ComponentType.GRIEFED_ID).replaceAll("minecraft:", "")))
        .append(Text.of("\n"))
        .append(Text.of(
            TextColors.GRAY,
            getNonNull(Factory.ComponentType.GRIEFED_TRANSFORM_X),
            ", ",
            getNonNull(Factory.ComponentType.GRIEFED_TRANSFORM_Y),
            ", ",
            getNonNull(Factory.ComponentType.GRIEFED_TRANSFORM_Z),
            "; ",
            getNonNull(Factory.ComponentType.DIMENSION)));
    if (!getNonNull(Factory.ComponentType.SPECIALTY).isEmpty()) {
      builder.append(Text.of("\n", TextColors.LIGHT_PURPLE, getNonNull(Factory.ComponentType.SPECIALTY)));
    }
    return builder.build();
  }

  @Override
  public int compareTo(LogMessage other) {
    return this.date.compareTo(other.date);
  }
}
