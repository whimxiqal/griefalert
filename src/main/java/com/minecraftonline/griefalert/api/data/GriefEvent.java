/* Created by PietElite */

package com.minecraftonline.griefalert.api.data;

import com.helion3.prism.api.data.PrismEvent;
import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@CatalogedBy(GriefEvents.class)
public class GriefEvent implements CatalogType {

  private final String id;
  private final String name;
  private final String preterite;

  public GriefEvent(String id, String name, String preterite) {
    this.id = id;
    this.name = name;
    this.preterite = preterite;
  }

  public static GriefEvent of(String id, String name, String preterite) {
    return new GriefEvent(id, name, preterite);
  }

  public static GriefEvent of(PrismEvent prismEvent) {
    return new GriefEvent(prismEvent.getId(), prismEvent.getName(), prismEvent.getPastTense());
  }

  @Override
  @NonnullByDefault
  public String getId() {
    return id;
  }

  @Override
  @NonnullByDefault
  public String getName() {
    return name;
  }

  public String getPreterite() {
    return preterite;
  }

  @Override
  public String toString() {
    return getId();
  }
}
