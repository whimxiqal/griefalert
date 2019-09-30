package com.minecraftonline.griefalert.griefevents;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.comms.StaffMessenger;
import com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent;
import com.minecraftonline.griefalert.griefevents.profiles.EventWrapper;
import com.minecraftonline.griefalert.griefevents.profiles.GriefProfile;
import com.minecraftonline.griefalert.tools.ClickableMessage;
import org.spongepowered.api.Sponge;
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

  public static void throwGriefEvent(GriefAlert plugin, GriefProfile profile, EventWrapper event) {
    GriefEvent generated = new GriefEvent(plugin, profile, event);
    if (plugin.getGriefEventCache().isStealthyFromRepetition(generated)) generated.stealthy = true;
    if (event.getGriefer().hasPermission(GriefAlert.Permission.GRIEFALERT_SILENT.toString())) generated.stealthy = true;
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
        event.getGriefedLocation().isPresent() ?
            "in the " + event.getGriefedLocation().get().getExtent().getDimension().getType().getName() :
            ""
    };
    Text message = ClickableMessage.builder(Text.of(alertColor, String.join(" ", messageList)))
        .addClickableCommand(
            String.valueOf(cacheCode),
            "/griefalert check " + cacheCode,
            Text.of("Teleport here.\n", getSummary())
        ).build().toText();
    if (!stealthy) {
      MessageChannel staffChannel = StaffMessenger.getStaffBroadcastChannel();
      if (plugin.getConfigHelper().isAlertEventsToConsole()) {
        staffChannel.asMutable().addMember(Sponge.getServer().getConsole());
      }
      staffChannel.send(message);
    } else {
      plugin.getLogger().info("Stealthy Alert: " + message.toPlain());
    }
  }

  public Text getSummary() {
    Text.Builder builder = Text.builder()
        .color(TextColors.GRAY)
        .append(Text.of(TextColors.GOLD, "==== Grief Alert " + cacheCode + " ====\n"))
        .append(Text.of(TextColors.LIGHT_PURPLE, event.getGriefer().getName()))
        .append(Text.of(TextColors.AQUA, TextStyles.BOLD, " " + event.getType().toPreteritVerb().toLowerCase()))
        .append(Text.of(" a ", TextStyles.BOLD, TextColors.GREEN, event.getGriefedName()));
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
    return this.getGriefType().equals(other.getGriefType())
        && this.getEvent().getGriefer().equals(other.getEvent().getGriefer())
        && this.getEvent().getGriefedId().equals(other.getEvent().getGriefedId());
  }

  public int getCacheCode() {
    return cacheCode;
  }
}
