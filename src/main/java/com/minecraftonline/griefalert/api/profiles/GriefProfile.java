package com.minecraftonline.griefalert.api.profiles;

import com.helion3.prism.api.data.PrismEvent;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.util.*;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.DimensionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GriefProfile {

  private final String target;
  private final GriefEvent eventType;
  private final List<DimensionType> ignoredDimensions;

  private GriefProfile(String target, GriefEvent eventType, DimensionType... ignoredDimensions) {
    this.target = target;
    this.eventType = eventType;
    this.ignoredDimensions = Arrays.asList(ignoredDimensions);
  }

  public static GriefProfile of(String target, GriefEvent eventType, DimensionType... ignoredDimensions) {
    return new GriefProfile(target, eventType, ignoredDimensions);
  }

  public String getTarget() {
    return target;
  }

  public GriefEvent getEventType() {
    return eventType;
  }

  public boolean isIgnoredIn(DimensionType dimension) {
    return ignoredDimensions.contains(dimension);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof GriefProfile)) {
      return false;
    }
    GriefProfile other = (GriefProfile) obj;
    return this.target.equals(other.target)
        && this.eventType.equals(other.eventType)
        && this.ignoredDimensions.equals(other.ignoredDimensions);
  }

  // ----------------------
  // PART OF AN OLD PROJECT
  // ----------------------

  // TODO: Clean out GriefProfile of 'builder' things


  public static Builder builder(GriefEvent prismEvent, String griefedObjectId) {
    return new Builder(prismEvent, griefedObjectId);
  }

  public static class Builder {
    private String griefedObjectId;
    private GriefEvent eventType;
    private List<DimensionType> ignoredDimensions = new ArrayList<>();

    Builder(GriefEvent eventType, String griefedObjectId) {
      this.eventType = eventType;
      this.griefedObjectId = griefedObjectId;
    }

    public GriefProfile build() {
      return new GriefProfile(griefedObjectId, eventType, ignoredDimensions.toArray(new DimensionType[0]));
    }

    public void setGriefedObjectId(String griefedObjectId) {
      this.griefedObjectId = griefedObjectId;
    }

    public void setEventType(GriefEvent eventType) {
      this.eventType = eventType;
    }

    public void setDimension(DimensionType dimensionType, boolean ignored) {
      if (ignored) {
        ignoredDimensions.add(dimensionType);
      } else {
        ignoredDimensions.remove(dimensionType);
      }
    }

    public Text print() {
      return Text.of(
          TextColors.GOLD, TextStyles.ITALIC, "Grief Profile Builder",
          TextColors.AQUA, "\nGrief Type: ", TextColors.WHITE, eventType.getName(),
          TextColors.AQUA, "\nObject: ", TextColors.WHITE, griefedObjectId.replaceAll("[a-zA-Z]*:", ""),
          TextColors.AQUA, "\nIgnored Dimensions: ", TextColors.WHITE, String.join(
              ", ",
              General.convertList(ignoredDimensions, DimensionType::getName)
          )
      );
    }

  }

}
