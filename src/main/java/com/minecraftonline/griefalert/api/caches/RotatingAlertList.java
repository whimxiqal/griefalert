/* Created by PietElite */

package com.minecraftonline.griefalert.api.caches;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.prism.PrismAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.events.PreCheckAlertEvent;
import com.minecraftonline.griefalert.api.structures.HashMapStack;
import com.minecraftonline.griefalert.api.structures.MapStack;
import com.minecraftonline.griefalert.api.structures.RotatingArrayList;
import com.minecraftonline.griefalert.commands.GriefAlertCheckCommand;
import com.minecraftonline.griefalert.util.Communication;
import com.minecraftonline.griefalert.util.Errors;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

import com.minecraftonline.griefalert.util.Format;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.World;

/**
 * The ongoing queue for all <code>Alert</code>s. This is only local storage. Someone
 * can access each alert by their 'cachecode' using the <code>RotatingQueue</code>
 * API.
 *
 * @see Alert
 * @see RotatingArrayList
 */
public final class RotatingAlertList extends RotatingArrayList<Alert> {

  // A map relating each player to a list of consecutive similar alerts for silencing
  private final MapStack<UUID, Alert> grieferRepeatHistory = new HashMapStack<>();
  private final MapStack<UUID, Transform<World>> officerCheckHistory = new HashMapStack<>();

  /**
   * Default constructor.
   *
   * @param capacity the capacity of this structure
   */
  public RotatingAlertList(final int capacity) {
    super(capacity);
  }

  /**
   * Adds an alert to the queue. This method will replace the old <code>Alert</code> at
   * that location, if it already exists, with the new one. Also, the given <code>alert
   * </code> will also be assigned the corresponding index for retrieval.
   *
   * @param alert The alert to add
   * @return The retrieval index
   */
  @Override
  public int push(@Nonnull final Alert alert) {

    // Push the alert to the RotatingQueue
    int output = super.push(alert);

    // Set the alert with the proper index
    alert.setCacheIndex(output);

    // Update alertMap
    UUID grieferUuid = alert.getGriefer().getUniqueId();
    if (grieferRepeatHistory.peek(grieferUuid).filter(alert::isRepeatOf).isPresent()) {
      alert.setSilent(true);
    } else {
      grieferRepeatHistory.clear(grieferUuid);
    }

    grieferRepeatHistory.push(grieferUuid, alert);

    int silentAlertLimit = GriefAlert.getInstance().getConfigHelper().getHiddenRepeatedEventLimit();
    if (grieferRepeatHistory.size(grieferUuid) >= silentAlertLimit) {
      grieferRepeatHistory.clear(grieferUuid);
    }

    // Finish
    return output;
  }

  /**
   * Check the <code>Alert</code> with the given <code>Player</code>.
   *
   * @param index   the index of the <code>Alert</code> to check.
   * @param officer The staff member
   * @return true if the player teleported correctly
   * @see Alert
   */
  public boolean check(int index, Player officer) {
    Alert alert;
    try {
      alert = get(index);
    } catch (IndexOutOfBoundsException e) {
      officer.sendMessage(Format.error("No alert exists at that index"));
      return false;
    }
    return check(alert, officer);
  }

  /**
   * Check the <code>Alert</code> with the given <code>Player</code>.
   *
   * @param alert   the <code>Alert</code> to check.
   * @param officer The staff member
   * @return true if the player teleported correctly
   * @see Alert
   */
  public boolean check(Alert alert, Player officer) {
    // Post an event to show that the Alert is getting checked
    PluginContainer plugin = GriefAlert.getInstance().getPluginContainer();
    EventContext eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, plugin).build();

    PreCheckAlertEvent preCheckAlertEvent = new PreCheckAlertEvent(
        alert,
        Cause.of(eventContext, plugin), officer);

    Sponge.getEventManager().post(preCheckAlertEvent);

    // Save the officer's previous transform and add it into the alert's database later
    // if the officer successfully teleports.
    Transform<World> officerPreviousTransform = officer.getTransform();

    // CheckEvent

    // Teleport the officer
    if (!officer.setTransformSafely(alert.getGrieferTransform())) {
      Errors.sendCannotTeleportSafely(officer, alert.getGrieferTransform());
      return false;
    }

    // The officer has teleported successfully, so save their previous location in the history
    GriefAlert.getInstance().getRotatingAlertList().addOfficerTransform(
        officer.getUniqueId(),
        officerPreviousTransform);

    // Send the messages
    Communication.getStaffBroadcastChannel().send(Format.info(
        Format.playerName(officer),
        " is checking alert number ",
        Format.bonus(GriefAlertCheckCommand.clickToCheck(alert.getCacheIndex()))));

    officer.sendMessage(Format.heading("Checking Grief Alert: ",
        Format.bonus(alert.getCacheIndex())));
    officer.sendMessage(Text.of(TextColors.YELLOW, alert.getMessageText().toPlain()));
    Text.Builder panel = Text.builder().append(Text.of(
        Format.bonus("=="),
        Format.space(),
        Format.command(
            "RECENT",
            String.format(
                "/griefalert query -p %s",
                alert.getGriefer().getName()),
            Text.of("Search for recent events caused by this player")),
        Format.space(),
        Format.command(
            "SHOW",
            String.format(
                "/griefalert show %s",
                alert.getCacheIndex()),
            Text.of("Show the alert location in the world")),
        Format.space(),
        Format.command(
            "INFO",
            String.format(
                "/griefalert info %s",
                alert.getCacheIndex()),
            Text.of("Display itemized information about the alert"))));

    if (alert instanceof PrismAlert) {
      if (((PrismAlert) alert).isReversed()) {
        panel.append(Format.bonus(
            TextColors.YELLOW,
            TextStyles.ITALIC,
            "ROLLED BACK"));
      } else {
        panel.append(Text.of(
            Format.space(),
            Format.command(
                "ROLLBACK",
                String.format(
                    "/griefalert rollback %s",
                    alert.getCacheIndex()),
                Text.of("Rollback this event"))));
      }
    }

    panel.append(Text.of(
        Format.space(),
        Format.bonus("==")));
    officer.sendMessage(panel.build());

    return true;
  }


  /**
   * Return an officer to their previous known location before a grief check.
   *
   * @param officer The officer to teleport
   * @return An optional of how many saved locations are left. Return an empty
   * Optional if there were no transforms left to begin with.
   */
  public Optional<Integer> revertOfficerTransform(Player officer) {

    Optional<Transform<World>> previousTransformOptional = officerCheckHistory
        .pop(officer.getUniqueId());

    if (!previousTransformOptional.isPresent()) {
      return Optional.empty();
    }

    if (!officer.setTransformSafely(previousTransformOptional.get())) {
      Errors.sendCannotTeleportSafely(officer, previousTransformOptional.get());
    }

    return Optional.of(officerCheckHistory.size(officer.getUniqueId()));
  }

  public void addOfficerTransform(UUID officerUuid, Transform<World> transform) {
    officerCheckHistory.push(officerUuid, transform);
  }
}
