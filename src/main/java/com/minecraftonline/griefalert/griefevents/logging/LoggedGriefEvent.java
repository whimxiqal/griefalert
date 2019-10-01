package com.minecraftonline.griefalert.griefevents.logging;

import com.minecraftonline.griefalert.griefevents.GriefEvent;

import java.util.HashMap;

public class LoggedGriefEvent {

  HashMap<ComponentType, Object> componentMap = new HashMap<>();

  enum ComponentType {
    GRIEF_TYPE,
    PLAYER_UUID,
    PLAYER_ROTATION_PITCH,
    PLAYER_ROTATION_YAW,
    PLAYER_ROTATION_ROLL,
    PLAYER_TRANSFORM_X,
    PLAYER_TRANSFORM_Y,
    PLAYER_TRANSFORM_Z,
    DIMENSION,
    WORLD_ID,
    GRIEFED_ID,
    GRIEFED_TRANSFORM_X,
    GRIEFED_TRANSFORM_Y,
    GRIEFED_TRANSFORM_Z,
    SPECIALTY,
    MC_VERSION
  }

  public static LoggedGriefEvent fromGriefEvent(GriefEvent griefEvent) {
    return new LoggedGriefEvent(griefEvent);
  }

  private LoggedGriefEvent(GriefEvent griefEvent) {
    componentMap.put(ComponentType.GRIEF_TYPE, griefEvent.getGriefType().getName());
    componentMap.put(
        ComponentType.PLAYER_UUID,
        griefEvent.getEvent().getGriefer().getUniqueId().toString()
    );
    if (griefEvent.getEvent().getGrieferSnapshot().getTransform().isPresent()) {
      componentMap.put(
          ComponentType.PLAYER_ROTATION_PITCH,
          griefEvent.getEvent().getGrieferSnapshot().getTransform().get().getPitch()
      );
      componentMap.put(
          ComponentType.PLAYER_ROTATION_YAW,
          griefEvent.getEvent().getGrieferSnapshot().getTransform().get().getYaw()
      );
      componentMap.put(
          ComponentType.PLAYER_ROTATION_ROLL,
          griefEvent.getEvent().getGrieferSnapshot().getTransform().get().getRoll()
      );
      componentMap.put(
          ComponentType.PLAYER_TRANSFORM_X,
          griefEvent.getEvent().getGrieferSnapshot().getTransform().get().getPosition().getX()
      );
      componentMap.put(
          ComponentType.PLAYER_TRANSFORM_Y,
          griefEvent.getEvent().getGrieferSnapshot().getTransform().get().getPosition().getY()
      );
      componentMap.put(
          ComponentType.PLAYER_TRANSFORM_Z,
          griefEvent.getEvent().getGrieferSnapshot().getTransform().get().getPosition().getZ()
      );
      componentMap.put(
          ComponentType.DIMENSION,
          griefEvent.getEvent().getGrieferSnapshot().getTransform().get().getLocation().getExtent()
              .getDimension().getType().getName()
      );
      componentMap.put(
          ComponentType.WORLD_ID,
          griefEvent.getEvent().getGrieferSnapshot().getTransform().get().getLocation().getExtent()
              .getUniqueId().toString()
      );
    }
    componentMap.put(ComponentType.GRIEFED_ID, griefEvent.getEvent().getGriefedId());
    if (griefEvent.getEvent().getGriefedLocation().isPresent()) {
      componentMap.put(
          ComponentType.GRIEFED_TRANSFORM_X,
          griefEvent.getEvent().getGriefedLocation().get().getX()
      );
      componentMap.put(
          ComponentType.GRIEFED_TRANSFORM_Y,
          griefEvent.getEvent().getGriefedLocation().get().getY()
      );
      componentMap.put(
          ComponentType.GRIEFED_TRANSFORM_Z,
          griefEvent.getEvent().getGriefedLocation().get().getZ()
      );
    }
    componentMap.put(ComponentType.SPECIALTY, griefEvent.getSpecialLogString());
    componentMap.put(ComponentType.MC_VERSION, "1.12.2");
  }

}
