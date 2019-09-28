package com.minecraftonline.griefalert.griefevents.profiles;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.tools.General;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;

import java.util.LinkedList;
import java.util.List;

public class GriefProfile {

  /**
   * String representation of the griefed object.
   */
  protected final String griefedId;
  /**
   * TextColor representation of the color in which an alert will appear.
   */
  protected final TextColor alertColor;
  /**
   * Denotes whether this action will be cancelled upon triggering.
   */
  protected final boolean denied;
  /**
   * Denotes whether this is muted from alerting.
   */
  protected final boolean stealthy;
  /**
   * The type of grief.
   */
  protected final GriefAlert.GriefType type;

  protected final GriefProfileSpecialtyBehavior specialtyBehavior;

  /**
   * The wrapper for information regarding whether a dimension is marked for this grief event.
   */
  protected final DimensionParameterArray dimensionParameterArray;

  public GriefProfile(GriefAlert.GriefType type, String griefedId, TextColor alertColor, boolean denied, boolean stealthy, DimensionParameterArray dimensionParameterArray) {
    this.type = type;
    this.griefedId = griefedId;
    this.alertColor = alertColor;
    this.denied = denied;
    this.stealthy = stealthy;
    this.dimensionParameterArray = dimensionParameterArray;
    this.specialtyBehavior = findSpecialtyBehavior();
  }

  public GriefProfile(GriefProfile other) {
    this(other.type, other.griefedId, other.alertColor, other.denied, other.stealthy, other.dimensionParameterArray);
  }

  GriefProfile(GriefProfileStorageLine line) throws IllegalArgumentException {
    String[] tokens = line.getTokens();
    if (tokens.length < 3) {
      throw new IllegalArgumentException("Not enough arguments. Use format <TYPE> <OBJECT_ID> <COLOR> [FLAGS]");
    }
    this.type = GriefAlert.GriefType.from(tokens[0]);
    this.griefedId = "minecraft:" + tokens[1].replaceAll("minecraft:", "");
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
    this.specialtyBehavior = findSpecialtyBehavior();
  }

  private GriefProfileSpecialtyBehavior findSpecialtyBehavior() {
    return GriefProfileSpecialtyBehavior.getMatching(this);
  }

  protected void runSpecialBehavior(GriefAlert plugin) {
    specialtyBehavior.accept(plugin);
  }

  public GriefProfileStorageLine toStorageLine() {
    GriefProfileStorageLine.Builder builder = new GriefProfileStorageLine.Builder()
        .addItem(getGriefType().getName())
        .addItem(getGriefedId())
        .addItem(alertColor.getName());
    if (denied) builder.addItem("-d");
    if (stealthy) builder.addItem("-s");
    if (dimensionParameterArray.isIgnored(DimensionTypes.OVERWORLD)) builder.addItem("--ignore-overworld");
    if (dimensionParameterArray.isIgnored(DimensionTypes.NETHER)) builder.addItem("--ignore-nether");
    if (dimensionParameterArray.isIgnored(DimensionTypes.THE_END)) builder.addItem("--ignore-the-end");
    return builder.build();
  }

  public static class DimensionParameterArray {

    /**
     * Each term is true if the dimension type corresponding to that index is encompassed by
     * this event.
     *
     * overworld, nether, the_end
     */
    private final boolean[] array = {false, false, false};

    boolean isIgnored(DimensionType dimensionType) {
      switch(dimensionType.getName().toLowerCase()) {
        case "overworld":
          return array[0];
        case "nether":
          return array[1];
        case "the_end":
          return array[2];
        default:
          throw new IllegalArgumentException();
      }
    }

    public void setIgnored(DimensionType dimensionType, boolean isIgnored) {
      switch(dimensionType.getName().toLowerCase()) {
        case "overworld":
          array[0] = isIgnored;
          return;
        case "nether":
          array[1] = isIgnored;
          return;
        case "the_end":
          array[2] = isIgnored;
          return;
        default:
          throw new IllegalArgumentException();
      }
    }

    public List<String> getIgnoredList() {
      List<String> toReturn = new LinkedList<>();
      if (array[0]) toReturn.add("Overworld");
      if (array[1]) toReturn.add("Nether");
      if (array[2]) toReturn.add("The End");
      return toReturn;
    }
  }

  public boolean isDenied() {
    return denied;
  }

  public boolean isStealthy() {
    return stealthy;
  }

  public GriefAlert.GriefType getGriefType() {
    return type;
  }

  public String getGriefedId() {
    return griefedId;
  }

}
