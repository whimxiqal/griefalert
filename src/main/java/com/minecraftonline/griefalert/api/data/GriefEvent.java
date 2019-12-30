package com.minecraftonline.griefalert.api.data;

import com.helion3.prism.api.data.PrismEvent;

public class GriefEvent {

  private final String id;
  private final String name;
  private final String pastTense;

  private GriefEvent(String id, String name, String pastTense) {
    this.id = id;
    this.name = name;
    this.pastTense = pastTense;
  }

  public static GriefEvent of(String id, String name, String pastTense) {
    return new GriefEvent(id, name, pastTense);
  }

  public static GriefEvent of(PrismEvent prismEvent) {
    return new GriefEvent(prismEvent.getId(), prismEvent.getName(), prismEvent.getPastTense());
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPastTense() {
    return pastTense;
  }

  @Override
  public String toString() {
    return getId();
  }
}
