/* Created by PietElite */

package com.minecraftonline.griefalert.api.data;

import com.helion3.prism.api.data.PrismEvent;
import com.minecraftonline.griefalert.util.GriefEvents;
import javax.annotation.Nonnull;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.util.annotation.CatalogedBy;

/**
 * An object to describe a specific action. This is one of the factors which match
 * certain in-game events with {@link com.minecraftonline.griefalert.api.records.GriefProfile}s
 * so that {@link com.minecraftonline.griefalert.api.alerts.Alert}s can be triggered.
 */
@CatalogedBy(GriefEvents.class)
public class GriefEvent implements CatalogType {

  private final String id;
  private final String name;
  private final String preterite;

  private GriefEvent(String id, String name, String preterite) {
    this.id = id;
    this.name = name;
    this.preterite = preterite;
  }

  /**
   * Factory method for a <code>GriefEvent</code>.
   *
   * @param id        The identifier
   * @param name      The human-readable name
   * @param preterite The past tense version of this event
   * @return The corresponding generated <code>GriefEvent</code>
   */
  public static GriefEvent of(String id, String name, String preterite) {
    return new GriefEvent(id, name, preterite);
  }

  /**
   * Factory method for a <code>GriefEvent</code> given a <code>PrismEvent</code>, which are what
   * <code>GriefEvent</code>s are loosely based on.
   *
   * @param prismEvent The similar <code>PrismEvent</code>
   * @return The corresponding generated <code>GriefEvent</code>
   */
  public static GriefEvent of(PrismEvent prismEvent) {
    return new GriefEvent(prismEvent.getId(), prismEvent.getName(), prismEvent.getPastTense());
  }

  @Override
  @Nonnull
  public String getId() {
    return id;
  }

  @Override
  @Nonnull
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
