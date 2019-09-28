package com.minecraftonline.griefalert.griefevents;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.comms.StaffMessenger;
import com.minecraftonline.griefalert.griefevents.logging.LoggedGriefEvent;
import com.minecraftonline.griefalert.griefevents.profiles.EventWrapper;
import com.minecraftonline.griefalert.griefevents.profiles.GriefProfile;
import com.minecraftonline.griefalert.tools.ClickableMessage;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.UUID;

public class GriefEvent extends GriefProfile {

  private final EventWrapper event;
  private final GriefAlert plugin;
  private int cacheCode;

  public static void throwGriefEvent(GriefAlert plugin, GriefProfile profile, EventWrapper event) {
    GriefEvent generated = new GriefEvent(plugin, profile, event);
    generated.cache();                      // Save the data in the cache store for access in-game
    generated.broadcast();                  // Tell all staff members
    generated.log();                        // Log this event with the database
    generated.runSpecialBehavior(plugin);   // Execute the special behavior this event may have
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

  public void log() {
    plugin.getGriefLogger().log(LoggedGriefEvent.fromGriefEvent(this));
  }

  public String getSpecialLogString() {
    return specialtyBehavior.getSpecialLogString(this);
  }

  public void broadcast() {
    String[] messageList = {
        event.getGriefer().getName(),
        event.getType().toPreteriteVerb(),
        "a",
        event.getGriefedName(),
        event.getGriefedLocation().isPresent() ?
            "in the " + event.getGriefedLocation().get().getExtent().getDimension().getType().getName() :
            ""
    };
    Text message = ClickableMessage.builder(Text.of(alertColor, String.join(" ", messageList)))
        .addClickableCommand(
            String.valueOf(cacheCode),
            "/griefalert check " + String.valueOf(cacheCode),
            Text.of("Teleport to this event location")
        ).build().toText();
    StaffMessenger.getStaffBroadcastChannel().send(message);
  }

  public int getCacheCode() {
    return cacheCode;
  }
}
