package com.minecraftonline.griefalert.griefevents;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.comms.Messenger;
import com.minecraftonline.griefalert.griefevents.profiles.EventWrapper;
import com.minecraftonline.griefalert.griefevents.profiles.GriefProfile;
import com.minecraftonline.griefalert.griefevents.profiles.SpecialtyBehavior;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.GriefAlertMessage;
import com.minecraftonline.griefalert.util.General;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class GriefEvent extends GriefProfile {

  private final EventWrapper event;
  private final SpecialtyBehavior specialty;
  private final GriefAlert plugin;
  private int cacheCode;

  /**
   * Generate a new Grief Event and perform all necessary actions. The
   * generated event is:
   *
   * <li>set to the proper alert mode based on
   * repetition and the permission level of the griefer</li>
   * <li>cached in the plugin's list of grief events to be used
   * during in-game grief checks</li>
   * <li>alerted to all staff members and the console if the config allows</li>
   * <li>called to perform its special behavior, if it has one (such as signs)</li>
   *
   * @param plugin  The main plugin instance
   * @param profile The specific profile on which to base the grief event
   * @param event   The wrapper for the actual Sponge event which caused this chain of events
   */
  public static void throwGriefEvent(GriefAlert plugin, GriefProfile profile, EventWrapper event) {
    throwGriefEvent(plugin, profile, event, SpecialtyBehavior.NONE);
  }

  public static void throwGriefEvent(GriefAlert plugin, GriefProfile profile, EventWrapper event, SpecialtyBehavior specialty) {
    GriefEvent generated = new GriefEvent(plugin, profile, event, specialty);
    if (plugin.getGriefEventCache().isStealthyFromRepetition(generated)) {
      generated.stealthy = true;
    }
    if (event.getGriefer().hasPermission(GriefAlert.Permission.GRIEFALERT_SILENT.toString())) {
      generated.stealthy = true;
    }
    generated.runSpecialBehavior(plugin);   // Execute the special behavior this event may have
    generated.cache();                      // Save the data in the cache store for access in-game
    generated.broadcast();                  // Tell all staff members
    try {
      if (generated.isDenied()) {
        ((Cancellable) generated.getEvent().getEvent()).setCancelled(true);
      }
    } catch (ClassCastException e) {
      plugin.getLogger().warn("Tried to cancel an event which cannot be cancelled: "
          + generated.getGriefType().getName() + " " + generated.getGriefedId());
    }
  }

  private void cache() {
    cacheCode = plugin.getGriefEventCache().offer(this);
  }

  private GriefEvent(GriefAlert plugin, GriefProfile profile, EventWrapper event, SpecialtyBehavior specialty) {
    super(profile);
    this.plugin = plugin;
    this.event = event;
    this.specialty = specialty;
  }

  public EventWrapper getEvent() {
    return event;
  }

  public String getSpecialLogString() {
    return specialty.getSpecialLogString(this);
  }

  private void runSpecialBehavior(GriefAlert plugin) {
    specialty.accept(plugin, this);
  }

  private void broadcast() {
    GriefAlertMessage.Builder builder = GriefAlertMessage.builder(Text.builder()
        .color(getAlertColor())
        .append(Text.of(
            General.formatPlayerName(event.getGriefer()),
            " ",
            event.getType().toPreteritVerb(),
            Grammar.correctIndefiniteArticles(" a " + event.getGriefedName()),
            " in "
        )).build());
    if (event.getGriefedLocation().isPresent()) {
      builder.append(Text.of("the " + event.getGriefedLocation().get().getExtent().getDimension().getType().getName() + " "));
    } else {
      builder.append(Text.of("an unknown dimension"));
    }
    builder.addClickableCommand(
        String.valueOf(cacheCode),
        "/griefalert check " + cacheCode,
        Text.of("Teleport here.\n", getSummary())
    );
    GriefAlertMessage message = builder.build();
    // Make sure the Grief Event isn't stealthy before sending it out too all staff.
    if (!stealthy) {
      MessageChannel staffChannel = Messenger.getStaffBroadcastChannel();
      staffChannel.send(message.toText());
    }
    // Whether or not the Grief Event is stealthy, show the alert to console if its enabled.
    if (plugin.getConfigHelper().isAlertEventsToConsole()) {
      plugin.getLogger().info(message.toText().toPlain());
    }
  }

  /**
   * Used to get a full summary of all information regarding this specific Grief Event.
   *
   * @return An itemized, readable list of information about this object
   */
  public Text getSummary() {
    Text.Builder builder = Text.builder()
        .color(TextColors.GRAY)
        .append(Text.of(TextColors.GOLD, "==== Grief Alert " + cacheCode + " ====\n"))
        .append(General.formatPlayerName(event.getGriefer()))
        .append(Text.of(TextColors.LIGHT_PURPLE, TextStyles.BOLD, " " + event.getType().toPreteritVerb()
            .toLowerCase()))
        .append(Text.of(TextColors.RED,
            Grammar.correctIndefiniteArticles(" a " + event.getGriefedName())));
    if (event.getGriefedLocation().isPresent()) {
      Location<World> location = event.getGriefedLocation().get();
      builder.append(Text.of("\nLocation: "
          + location.getBlockX() + ", "
          + location.getBlockY() + ", "
          + location.getBlockZ() + " in the "
          + location.getExtent().getDimension().getType().getName()));
    }
    return builder.build();
  }

  public boolean isSimilar(GriefEvent other) {
    return this.getEvent().getGriefer().equals(other.getEvent().getGriefer())
        && super.isSimilar(other);
  }

  @Override
  public String getGriefedId() {
    if (this.getEvent() != null) {
      return this.getEvent().getGriefedId();
    } else {
      return super.getGriefedId();
    }
  }

  public int getCacheCode() {
    return cacheCode;
  }
}
