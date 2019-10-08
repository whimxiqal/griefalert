package com.minecraftonline.griefalert.griefevents.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionType;

public class LogQuery {

  private String query;

  public static Builder builder() {
    return new Builder();
  }

  private LogQuery(String s) {
    this.query = s;
  }

  String getSQLCommand() {
    return query;
  }

  enum Comparison {
    EQUALS {
      @Override
      public String toString() {
        return "=";
      }
    },
    LESS {
      @Override
      public String toString() {
        return "<";
      }
    },
    GREATER {
      @Override
      public String toString() {
        return ">";
      }
    },
    LESS_EQUALS {
      @Override
      public String toString() {
        return "<=";
      }
    },
    GREATER_EQUALS {
      @Override
      public String toString() {
        return ">=";
      }
    }
  }

  static class Condition implements CharSequence {
    final Factory.ComponentType column;
    final Comparison comparison;
    final Object value;

    Condition(Factory.ComponentType column, Comparison comparison, Object value) {
      this.column = column;
      this.comparison = comparison;
      this.value = value;
    }

    @Override
    public int length() {
      return toString().length();
    }

    @Override
    public char charAt(int index) {
      return toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
      return toString().subSequence(start, end);
    }

    @Override
    public String toString() {
      String result = column.name().toLowerCase()
          + " " + comparison.toString() + " ";
      if (value instanceof String) {
        result += "'" + value.toString() + "'";
      } else {
        result += value.toString();
      }
      return result;
    }
  }

  public static class Builder {

    List<Condition> conditions = new LinkedList<>();

    public LogQuery build() {
      String s = "SELECT * FROM "
          + GriefEventLogger.GRIEF_TABLE_NAME
          + " WHERE "
          + String.join(" AND ", conditions);
      return new LogQuery(s);
    }

    public Builder addBlockCondition(int x, int y, int z, DimensionType dimension) {
      conditions.add(new Condition(Factory.ComponentType.GRIEFED_TRANSFORM_X, Comparison.GREATER_EQUALS, x));
      conditions.add(new Condition(Factory.ComponentType.GRIEFED_TRANSFORM_X, Comparison.LESS, x + 1));
      conditions.add(new Condition(Factory.ComponentType.GRIEFED_TRANSFORM_Y, Comparison.GREATER_EQUALS, y));
      conditions.add(new Condition(Factory.ComponentType.GRIEFED_TRANSFORM_Y, Comparison.LESS, y + 1));
      conditions.add(new Condition(Factory.ComponentType.GRIEFED_TRANSFORM_Z, Comparison.GREATER_EQUALS, z));
      conditions.add(new Condition(Factory.ComponentType.GRIEFED_TRANSFORM_Z, Comparison.LESS, z + 1));
      conditions.add(new Condition(Factory.ComponentType.DIMENSION, Comparison.EQUALS, dimension.getName()));
      return this;
    }

    public Builder addAfterCondition(Date date) {
      conditions.add(new Condition(Factory.ComponentType.TIMESTAMP_, Comparison.GREATER_EQUALS, (new SimpleDateFormat("yyyy-MM-dd")).format(date)));
      return this;
    }

    public Builder addBeforeCondition(Date date) {
      conditions.add(new Condition(Factory.ComponentType.TIMESTAMP_, Comparison.LESS_EQUALS, (new SimpleDateFormat("yyyy-MM-dd")).format(date)));
      return this;
    }

    public Builder addGriefedCondition(String griefedId) {
      conditions.add(new Condition(Factory.ComponentType.GRIEFED_ID, Comparison.EQUALS, griefedId));
      return this;
    }

    public Builder addBlockCondition(int blockXMin, int blockYMin, int blockZMin, int blockXMax, int blockYMax, int blockZMax, DimensionType dimension) {
      conditions.add(new Condition(Factory.ComponentType.GRIEFED_TRANSFORM_X, Comparison.GREATER_EQUALS, blockXMin));
      conditions.add(new Condition(Factory.ComponentType.GRIEFED_TRANSFORM_X, Comparison.LESS, blockXMax + 1));
      conditions.add(new Condition(Factory.ComponentType.GRIEFED_TRANSFORM_Y, Comparison.GREATER_EQUALS, blockYMin));
      conditions.add(new Condition(Factory.ComponentType.GRIEFED_TRANSFORM_Y, Comparison.LESS, blockYMax + 1));
      conditions.add(new Condition(Factory.ComponentType.GRIEFED_TRANSFORM_Z, Comparison.GREATER_EQUALS, blockZMin));
      conditions.add(new Condition(Factory.ComponentType.GRIEFED_TRANSFORM_Z, Comparison.LESS, blockZMax + 1));
      conditions.add(new Condition(Factory.ComponentType.DIMENSION, Comparison.EQUALS, dimension.getName()));
      return this;
    }

    public Builder addTypeCondition(String type) {
      conditions.add(new Condition(Factory.ComponentType.GRIEF_TYPE, Comparison.EQUALS, type));
      return this;
    }

  }
}


