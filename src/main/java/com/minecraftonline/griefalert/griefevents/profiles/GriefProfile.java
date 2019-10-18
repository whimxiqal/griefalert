package com.minecraftonline.griefalert.griefevents.profiles;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.util.General;

import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;

public class GriefProfile {

  private final String griefedId;
  private final TextColor alertColor;
  private final boolean denied;
  protected boolean stealthy;
  private final GriefAlert.GriefType type;
  private final DimensionParameterArray dimensionParameterArray;

  /**
   * Constructor for a new Grief Profile.
   *
   * @param type                    The general type of grief
   * @param griefedId               The id of the griefed object (saved here as 1.12.2 id)
   * @param alertColor              The color of the alert (usually red)
   * @param denied                  The state of whether events based on this profile should be
   *                                entirely denied to all players
   * @param stealthy                The state of whether staff members are alerted of events
   *                                based on this profile
   * @param dimensionParameterArray The custom array object which houses information about whether
   *                                events based on this profile should be ignored entirely
   */
  public GriefProfile(
      GriefAlert.GriefType type,
      String griefedId,
      TextColor alertColor,
      boolean denied,
      boolean stealthy,
      DimensionParameterArray dimensionParameterArray) {
    this.type = type;
    this.griefedId = griefedId;
    this.alertColor = alertColor;
    this.denied = denied;
    this.stealthy = stealthy;
    this.dimensionParameterArray = dimensionParameterArray;
  }

  @SuppressWarnings("all")
  public GriefProfile(GriefProfile other) {
    this(
        other.type,
        other.griefedId,
        other.alertColor,
        other.denied,
        other.stealthy,
        other.dimensionParameterArray
    );
  }

  GriefProfile(GriefProfileStorageLine line) throws IllegalArgumentException {
    String[] tokens = line.getTokens();
    if (tokens.length < 3) {
      throw new IllegalArgumentException("Not enough arguments. "
          + "Use format <TYPE> <OBJECT_ID> <COLOR> [FLAGS]");
    }
    this.type = GriefAlert.GriefType.from(tokens[0]);
    if (tokens[1].contains("[a-zA-Z]:")) {
      this.griefedId = tokens[1];
    } else {
      this.griefedId = "minecraft:" + tokens[1];
    }
    this.alertColor = General.stringToColor(tokens[2]);
    DimensionParameterArray toDimensionParameterArray = new DimensionParameterArray();
    boolean toDenied = false;
    boolean toStealthy = false;
    for (int i = 3; i < tokens.length; i++) {
      if (tokens[i].equals("--denied") || tokens[i].equals("-d")) {
        toDenied = true;
      }
      if (tokens[i].equals("--stealth") || tokens[i].equals("-s")) {
        toStealthy = true;
      }
      if (tokens[i].equals("--ignore-overworld")) {
        toDimensionParameterArray.setIgnored(DimensionTypes.OVERWORLD, true);
      }
      if (tokens[i].equals("--ignore-nether")) {
        toDimensionParameterArray.setIgnored(DimensionTypes.NETHER, true);
      }
      if (tokens[i].equals("--ignore-the_end")) {
        toDimensionParameterArray.setIgnored(DimensionTypes.THE_END, true);
      }
    }
    this.denied = toDenied;
    this.stealthy = toStealthy;
    this.dimensionParameterArray = toDimensionParameterArray;
  }



  GriefProfileStorageLine toStorageLine() {
    GriefProfileStorageLine.Builder builder = GriefProfileStorageLine.builder()
        .addItem(getGriefType().getName())
        .addItem(getGriefedId().replaceAll("minecraft:", ""))
        .addItem(alertColor.getName());
    if (denied) {
      builder.addItem("-d");
    }
    if (stealthy) {
      builder.addItem("-s");
    }
    if (dimensionParameterArray.isIgnored(DimensionTypes.OVERWORLD)) {
      builder.addItem("--ignore-overworld");
    }
    if (dimensionParameterArray.isIgnored(DimensionTypes.NETHER)) {
      builder.addItem("--ignore-nether");
    }
    if (dimensionParameterArray.isIgnored(DimensionTypes.THE_END)) {
      builder.addItem("--ignore-the-end");
    }
    return builder.build();
  }

  DimensionParameterArray getDimensionStructure() {
    return dimensionParameterArray;
  }

  public boolean isDenied() {
    return denied;
  }

  public GriefAlert.GriefType getGriefType() {
    return type;
  }

  public String getGriefedId() {
    return griefedId;
  }

  protected TextColor getAlertColor() {
    return alertColor;
  }

  protected boolean isSimilar(GriefProfile other) {
    return this.getGriefType().equals(other.getGriefType())
        && this.getGriefedId().equalsIgnoreCase(other.getGriefedId());
  }

  public static class DimensionParameterArray {

    // If each of the following is ignored: Overworld, Nether, The End
    private final boolean[] array = {false, false, false};

    boolean isIgnored(@Nonnull DimensionType dimensionType) {
      if (DimensionTypes.OVERWORLD.equals(dimensionType)) {
        return array[0];
      } else if (DimensionTypes.NETHER.equals(dimensionType)) {
        return array[1];
      } else if (DimensionTypes.THE_END.equals(dimensionType)) {
        return array[2];
      }
      throw new IllegalArgumentException(dimensionType.getName().toLowerCase()
          + " is not a valid dimension dimension type.");
    }

    void setIgnored(DimensionType dimensionType, boolean isIgnored) {
      if (DimensionTypes.OVERWORLD.equals(dimensionType)) {
        array[0] = isIgnored;
        return;
      } else if (DimensionTypes.NETHER.equals(dimensionType)) {
        array[1] = isIgnored;
        return;
      } else if (DimensionTypes.THE_END.equals(dimensionType)) {
        array[2] = isIgnored;
        return;
      }
      throw new IllegalArgumentException(dimensionType.getName().toLowerCase()
          + " is not a valid dimension dimension type.");
    }

    public void toggleIgnored(DimensionType type) {
      setIgnored(type, !isIgnored(type));
    }

    /**
     * Return a Linked List of all dimensions which are marked as 'ignored'.
     *
     * @return A Linked List of names of dimensions
     */
    public List<String> getIgnoredList() {
      List<String> toReturn = new LinkedList<>();
      if (array[0]) {
        toReturn.add(DimensionTypes.OVERWORLD.getName());
      }
      if (array[1]) {
        toReturn.add(DimensionTypes.NETHER.getName());
      }
      if (array[2]) {
        toReturn.add(DimensionTypes.THE_END.getName());
      }
      return toReturn;
    }
  }

}
