package com.minecraftonline.griefalert.griefevents.profiles;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.tools.General;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class GriefProfile {

  /**
   * String representation of the griefed object. There is no 'minecraft:' prefix to this value.
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
  protected boolean stealthy;
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
        .addItem(getGriefedId().replaceAll("minecraft:", ""))
        .addItem(alertColor.getName());
    if (denied) builder.addItem("-d");
    if (stealthy) builder.addItem("-s");
    if (dimensionParameterArray.isIgnored(DimensionTypes.OVERWORLD)) builder.addItem("--ignore-overworld");
    if (dimensionParameterArray.isIgnored(DimensionTypes.NETHER)) builder.addItem("--ignore-nether");
    if (dimensionParameterArray.isIgnored(DimensionTypes.THE_END)) builder.addItem("--ignore-the-end");
    return builder.build();
  }

  public DimensionParameterArray getDimensionStructure() {
    return dimensionParameterArray;
  }

  public static class DimensionParameterArray {

    /**
     * Each term is true if the dimension type corresponding to that index is encompassed by
     * this event.
     *
     * overworld, nether, the_end
     */
    private final boolean[] array = {false, false, false};

    boolean isIgnored(@Nonnull DimensionType dimensionType) {
      if (DimensionTypes.OVERWORLD.equals(dimensionType)) {
        return array[0];
      } else if (DimensionTypes.NETHER.equals(dimensionType)) {
        return array[1];
      } else if (DimensionTypes.THE_END.equals(dimensionType)) {
        return array[2];
      }
      throw new IllegalArgumentException(dimensionType.getName().toLowerCase() + " is not a valid dimension dimension type.");
    }

    public void setIgnored(DimensionType dimensionType, boolean isIgnored) {
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
      throw new IllegalArgumentException(dimensionType.getName().toLowerCase() + " is not a valid dimension dimension type.");
    }

    public void toggleIgnored(DimensionType type) {
      setIgnored(type, !isIgnored(type));
    }

    public List<String> getIgnoredList() {
      List<String> toReturn = new LinkedList<>();
      if (array[0]) toReturn.add(DimensionTypes.OVERWORLD.getName());
      if (array[1]) toReturn.add(DimensionTypes.NETHER.getName());
      if (array[2]) toReturn.add(DimensionTypes.THE_END.getName());
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
