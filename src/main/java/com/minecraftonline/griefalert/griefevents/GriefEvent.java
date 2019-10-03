package com.minecraftonline.griefalert.griefevents;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.comms.Messenger;
import com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent;
import com.minecraftonline.griefalert.griefevents.profiles.EventWrapper;
import com.minecraftonline.griefalert.griefevents.profiles.GriefProfile;
import com.minecraftonline.griefalert.tools.ClickableMessage;
import com.minecraftonline.griefalert.tools.General;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class GriefEvent extends GriefProfile {

  private final EventWrapper event;
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
    GriefEvent generated = new GriefEvent(plugin, profile, event);
    if (plugin.getGriefEventCache().isStealthyFromRepetition(generated)) {
      generated.stealthy = true;
    }
    if (event.getGriefer().hasPermission(GriefAlert.Permission.GRIEFALERT_SILENT.toString())) {
      generated.stealthy = true;
    }
    generated.cache();                      // Save the data in the cache store for access in-game
    generated.broadcast();                  // Tell all staff members
    generated.log();                        // Log this event with the database
    generated.runSpecialBehavior(plugin);   // Execute the special behavior this event may have
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

  private GriefEvent(GriefAlert plugin, GriefProfile profile, EventWrapper event) {
    super(profile);
    this.plugin = plugin;
    this.event = event;
  }

  public EventWrapper getEvent() {
    return event;
  }

  private void log() {
    plugin.getGriefLogger().log(LoggedGriefEvent.fromGriefEvent(this));
  }

  public String getSpecialLogString() {
    return specialtyBehavior.getSpecialLogString(this);
  }

  private void broadcast() {
    String[] messageList = {
        event.getGriefer().getName(),
        event.getType().toPreteritVerb(),
        "a",
        event.getGriefedName(),
        "in",
        (event.getGriefedLocation().isPresent()
            ?
            "the " + event.getGriefedLocation().get().getExtent()
                .getDimension().getType().getName() :
            "an unknown dimension")
    };
    Text message = ClickableMessage.builder(Text.of(getAlertColor(), String.join(" ", messageList)))
        .addClickableCommand(
            String.valueOf(cacheCode),
            "/griefalert check " + cacheCode,
            Text.of("Teleport here.\n", getSummary())
        ).build().toText();
    // Make sure the Grief Event isn't stealthy before sending it out too all staff.
    if (!stealthy) {
      MessageChannel staffChannel = Messenger.getStaffBroadcastChannel();
      staffChannel.send(message);
    }
    // Whether or not the Grief Event is stealthy, show the alert to console if its enabled.
    if (plugin.getConfigHelper().isAlertEventsToConsole()) {
      plugin.getLogger().info(message.toPlain());
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
        .append(Text.of(TextColors.AQUA, TextStyles.BOLD, " " + event.getType().toPreteritVerb()
            .toLowerCase()))
        .append(Text.of(TextStyles.BOLD, TextColors.GREEN,
            General.correctIndefiniteArticles(" a " + event.getGriefedName())));
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

  boolean isSimilar(GriefEvent other) {
    return this.getEvent().getGriefer().equals(other.getEvent().getGriefer())
        && super.isSimilar(other);
  }

  public int getCacheCode() {
    return cacheCode;
  }
}
