package com.minecraftonline.griefalert.griefevents.logging;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.GriefEvent;
import com.minecraftonline.griefalert.griefevents.profiles.GriefProfile;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;
import java.util.function.Function;

public class Factory {

  private List<LogComponent> logComponents = new LinkedList<>();

  List<LogComponent> getLogComponents() {
    return logComponents;
  }

  Factory() {
    logComponents.add(new LogComponent<>(
        ComponentType.GRIEF_TYPE,
        griefEvent -> "'" + griefEvent.getGriefType().getName() + "'",
        resultSet -> {
          try {
            return GriefAlert.GriefType.from(resultSet.getString(ComponentType.GRIEF_TYPE.name()));
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));
    logComponents.add(new LogComponent<>(
        ComponentType.PLAYER_UUID,
        griefEvent -> "'" + griefEvent.getEvent().getGriefer().getUniqueId().toString() + "'",
        resultSet -> {
          try {
            return UUID.fromString(resultSet.getString(ComponentType.PLAYER_UUID.name()));
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));
    logComponents.add(new LogComponent<>(
        ComponentType.PLAYER_ROTATION_PITCH,
        griefEvent -> {
          if (griefEvent.getEvent().getGrieferSnapshot().getTransform().isPresent()) {
            return String.valueOf(griefEvent.getEvent().getGrieferSnapshot().getTransform().get().getPitch());
          } else {
            return "";
          }
        },
        resultSet -> {
          try {
            return resultSet.getDouble(ComponentType.PLAYER_ROTATION_PITCH.name());
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));
    logComponents.add(new LogComponent<>(
        ComponentType.PLAYER_ROTATION_YAW,
        griefEvent -> {
          if (griefEvent.getEvent().getGrieferSnapshot().getTransform().isPresent()) {
            return String.valueOf(griefEvent.getEvent().getGrieferSnapshot().getTransform().get().getYaw());
          } else {
            return "";
          }
        },
        resultSet -> {
          try {
            return resultSet.getDouble(ComponentType.PLAYER_ROTATION_YAW.name());
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));
    logComponents.add(new LogComponent<>(
        ComponentType.PLAYER_ROTATION_ROLL,
        griefEvent -> {
          if (griefEvent.getEvent().getGrieferSnapshot().getTransform().isPresent()) {
            return String.valueOf(griefEvent.getEvent().getGrieferSnapshot().getTransform().get().getRoll());
          } else {
            return "";
          }
        },
        resultSet -> {
          try {
            return resultSet.getDouble(ComponentType.PLAYER_ROTATION_ROLL.name());
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));
    logComponents.add(new LogComponent<>(
        ComponentType.PLAYER_TRANSFORM_X,
        griefEvent -> {
          if (griefEvent.getEvent().getGrieferSnapshot().getLocation().isPresent()) {
            return String.valueOf(griefEvent.getEvent().getGrieferSnapshot().getLocation().get().getX());
          } else {
            return "";
          }
        },
        resultSet -> {
          try {
            return resultSet.getDouble(ComponentType.PLAYER_TRANSFORM_X.name());
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));
    logComponents.add(new LogComponent<>(
        ComponentType.PLAYER_TRANSFORM_Y,
        griefEvent -> {
          if (griefEvent.getEvent().getGrieferSnapshot().getLocation().isPresent()) {
            return String.valueOf(griefEvent.getEvent().getGrieferSnapshot().getLocation().get().getY());
          } else {
            return "";
          }
        },
        resultSet -> {
          try {
            return resultSet.getDouble(ComponentType.PLAYER_TRANSFORM_Y.name());
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));
    logComponents.add(new LogComponent<>(
        ComponentType.PLAYER_TRANSFORM_Z,
        griefEvent -> {
          if (griefEvent.getEvent().getGrieferSnapshot().getLocation().isPresent()) {
            return String.valueOf(griefEvent.getEvent().getGrieferSnapshot().getLocation().get().getZ());
          } else {
            return "";
          }
        },
        resultSet -> {
          try {
            return resultSet.getDouble(ComponentType.PLAYER_TRANSFORM_Z.name());
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));
    logComponents.add(new LogComponent<>(
        ComponentType.DIMENSION,
        griefEvent -> {
          if (griefEvent.getEvent().getGrieferSnapshot().getLocation().isPresent()) {
            return "'" + griefEvent.getEvent().getGrieferSnapshot().getLocation().get().getExtent().getDimension().getType().getName() + "'";
          } else {
            return "";
          }
        },
        resultSet -> {
          try {
            return resultSet.getString(ComponentType.DIMENSION.name());
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));
    logComponents.add(new LogComponent<>(
        ComponentType.WORLD_ID,
        griefEvent -> {
          if (griefEvent.getEvent().getGrieferSnapshot().getLocation().isPresent()) {
            return "'" + griefEvent.getEvent().getGrieferSnapshot().getLocation().get().getExtent().getUniqueId().toString() + "'";
          } else {
            return "";
          }
        },
        resultSet -> {
          try {
            return UUID.fromString(resultSet.getString(ComponentType.WORLD_ID.name()));
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));
    logComponents.add(new LogComponent<>(
        ComponentType.GRIEFED_ID,
        griefEvent -> "'" + griefEvent.getGriefedId() + "'",
        resultSet -> {
          try {
            return resultSet.getString(ComponentType.GRIEFED_ID.name());
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));
    logComponents.add(new LogComponent<>(
        ComponentType.GRIEFED_TRANSFORM_X,
        griefEvent -> {
          if (griefEvent.getEvent().getGriefedLocation().isPresent()) {
            return String.valueOf(griefEvent.getEvent().getGriefedLocation().get().getX());
          } else {
            return "";
          }
        },
        resultSet -> {
          try {
            return resultSet.getDouble(ComponentType.GRIEFED_TRANSFORM_X.name());
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));
    logComponents.add(new LogComponent<>(
        ComponentType.GRIEFED_TRANSFORM_Y,
        griefEvent -> {
          if (griefEvent.getEvent().getGriefedLocation().isPresent()) {
            return String.valueOf(griefEvent.getEvent().getGriefedLocation().get().getY());
          } else {
            return "";
          }
        },
        resultSet -> {
          try {
            return resultSet.getDouble(ComponentType.GRIEFED_TRANSFORM_Y.name());
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));
    logComponents.add(new LogComponent<>(
        ComponentType.GRIEFED_TRANSFORM_Z,
        griefEvent -> {
          if (griefEvent.getEvent().getGriefedLocation().isPresent()) {
            return String.valueOf(griefEvent.getEvent().getGriefedLocation().get().getZ());
          } else {
            return "";
          }
        },
        resultSet -> {
          try {
            return resultSet.getDouble(ComponentType.GRIEFED_TRANSFORM_Z.name());
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));
    logComponents.add(new LogComponent<>(
        ComponentType.SPECIALTY,
        griefEvent -> "'" + griefEvent.getSpecialLogString() + "'",
        resultSet -> ""));
    logComponents.add(new LogComponent<>(
        ComponentType.MC_VERSION,
        griefEvent -> "'" + GriefAlert.MC_VERSION + "'",
        null));
    logComponents.add(new LogComponent<>(
        ComponentType.TIMESTAMP_,
        null,
        resultSet -> {
          try {
            Date date = resultSet.getDate(ComponentType.TIMESTAMP_.name());

            return resultSet.getDate(ComponentType.TIMESTAMP_.name());
          } catch (SQLException e) {
            e.printStackTrace();
            return null;
          }
        }));

  }

  static class LogComponent<T> {

    private ComponentType type;
    private Function<GriefEvent, String> toSql;
    private Function<ResultSet, T> fromSql;

    ComponentType getType() {
      return type;
    }

    String toSql(GriefEvent griefEvent) {
      return toSql.apply(griefEvent);
    }

    boolean forSql() {
      return toSql != null;
    }

    boolean forRetrieval() {
      return fromSql != null;
    }

    T fromSql(ResultSet resultSet) {
      return fromSql.apply(resultSet);
    }

    private LogComponent(ComponentType type, Function<GriefEvent, String> toSql, Function<ResultSet, T> fromSql) {
      this.type = type;
      this.toSql = toSql;
      this.fromSql = fromSql;
    }
  }

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
    MC_VERSION,
    TIMESTAMP_
  }

}

