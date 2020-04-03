/* Created by PietElite */

package com.minecraftonline.griefalert.api.caches;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.prism.PrismAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.alerts.SerializableAlert;
import com.minecraftonline.griefalert.api.events.PreCheckAlertEvent;
import com.minecraftonline.griefalert.api.structures.HashMapStack;
import com.minecraftonline.griefalert.api.structures.MapStack;
import com.minecraftonline.griefalert.api.structures.RotatingArrayList;
import com.minecraftonline.griefalert.api.structures.RotatingList;
import com.minecraftonline.griefalert.commands.CheckCommand;
import com.minecraftonline.griefalert.util.Communication;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.Permissions;
import com.minecraftonline.griefalert.util.enums.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

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
public final class AlertManager {

  // A map relating each player to a list of consecutive similar alerts for silencing
  private final MapStack<UUID, Alert> grieferRepeatHistory = new HashMapStack<>();
  private final MapStack<UUID, Transform<World>> officerCheckHistory = new HashMapStack<>();
  private final RotatingList<Alert> alertCache;
  private final File alertStorageFile;

  /**
   * Default constructor.
   */
  public AlertManager() {
    alertStorageFile = new File(
        GriefAlert.getInstance().getDataDirectory().getAbsolutePath()
            + "/alerts.ser");
    this.alertCache = generateAlertList();
  }

  public RotatingList<Alert> getAlertCache() {
    return alertCache;
  }

  /**
   * Adds an alert to the queue. This method will replace the old <code>Alert</code> at
   * that location, if it already exists, with the new one. Also, the given <code>alert
   * </code> will also be assigned the corresponding index for retrieval.
   *
   * @param alert The alert to add
   * @return The retrieval index
   */
  public int push(@Nonnull final Alert alert) {

    // Push the alert to the RotatingQueue
    int output = alertCache.push(alert);

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

    int silentAlertLimit = Settings.MAX_HIDDEN_REPEATED_EVENTS.getValue();
    if (grieferRepeatHistory.size(grieferUuid) >= silentAlertLimit) {
      grieferRepeatHistory.clear(grieferUuid);
    }

    // Finish
    return output;
  }

  /**
   * Check the <code>Alert</code> with the given <code>Player</code>.
   *
   * @param alert   the <code>Alert</code> to check.
   * @param officer The staff member
   * @param force whether the teleportation is performed regardless if it's safe
   * @return true if the player teleported correctly
   * @see Alert
   */
  public boolean check(Alert alert, Player officer, boolean force) {
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
    if (force) {
      officer.setTransform(alert.getGrieferTransform());
    } else {
      if (!officer.setTransformSafely(alert.getGrieferTransform())) {
        Errors.sendCannotTeleportSafely(officer, alert.getGrieferTransform());
        return false;
      }
    }

    // The officer has teleported successfully, so save their previous location in the history
    this.addOfficerTransform(officer.getUniqueId(), officerPreviousTransform);

    // Send the messages
    Communication.getStaffBroadcastChannelWithout(officer).send(Format.info(
        Format.userName(officer),
        " is checking alert number ",
        Format.bonus(CheckCommand.clickToCheck(alert.getCacheIndex()))));

    officer.sendMessage(Format.heading("Checking Grief Alert: ",
        Format.bonus(alert.getCacheIndex())));
    officer.sendMessage(Text.of(TextColors.YELLOW, alert.getMessageText().toPlain()));
    Text.Builder panel = Text.builder().append(Format.bonus("=="));

    if (officer.hasPermission(Permissions.GRIEFALERT_COMMAND_QUERY.toString())) {
      panel.append(
          Format.space(2),
          Format.getTagRecent(alert.getGriefer().getName()));
    }
    if (officer.hasPermission(Permissions.GRIEFALERT_COMMAND_SHOW.toString())) {
      panel.append(
          Format.space(2),
          Format.getTagShow(alert.getCacheIndex()));
    }
    if (officer.hasPermission(Permissions.GRIEFALERT_COMMAND_INFO.toString())) {
      panel.append(
          Format.space(2),
          Format.getTagInfo(alert.getCacheIndex()));
    }
    panel.append(
        Format.space(2),
        Format.getTagReturn());

    if (alert instanceof PrismAlert
        && officer.hasPermission(Permissions.GRIEFALERT_COMMAND_ROLLBACK.toString())) {
      if (((PrismAlert) alert).isReversed()) {
        panel.append(Format.bonus(
            TextColors.DARK_GRAY,
            TextStyles.ITALIC,
            Format.space(2),
            "ROLLED BACK"));
      } else {
        panel.append(Text.of(
            Format.space(2),
            Format.getTagRollback(alert.getCacheIndex())));
      }
    }

    panel.append(Text.of(
        Format.space(2),
        Format.bonus("==")));
    officer.sendMessage(panel.build());

    return true;
  }


  /**
   * Return an officer to their previous known location before a grief check.
   *
   * @param officer The officer to teleport
   * @return An optional of how many saved locations are left. Return an empty
   *         Optional if there were no transforms left to begin with.
   */
  public Optional<Integer> revertOfficerTransform(Player officer) {

    Optional<Transform<World>> previousTransformOptional = officerCheckHistory
        .pop(officer.getUniqueId());
    int revertsRemaining = officerCheckHistory.size(officer.getUniqueId());

    if (!previousTransformOptional.isPresent()) {
      officer.sendMessage(Format.info("You have no previous location."));
      return Optional.empty();
    }

    if (!officer.setTransformSafely(previousTransformOptional.get())) {
      Errors.sendCannotTeleportSafely(officer, previousTransformOptional.get());
    } else {
      officer.sendMessage(Format.success("Returned to previous location."));
    }
    officer.sendMessage(Format.info(
        Format.bonus(revertsRemaining),
        " previous locations available."));

    return Optional.of(revertsRemaining);
  }

  private void addOfficerTransform(UUID officerUuid, Transform<World> transform) {
    officerCheckHistory.push(officerUuid, transform);
  }

  /**
   * Give a {@link RotatingList}, generated by either a temporary file
   * or a completely new one if the storage file doesn't exist.
   *
   * @return the new list
   */
  public RotatingList<Alert> generateAlertList() {
    if (alertStorageFile.exists()) {
      try {
        return restoreAlerts().map(serializableAlert -> {
          try {
            if (serializableAlert == null) {
              return null;
            }
            return serializableAlert.deserialize();
          } catch (Exception e) {
            e.printStackTrace();
            return null;
          }
        });
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return new RotatingArrayList<>(Settings.ALERTS_CODE_LIMIT.getValue());
  }

  /**
   * Save the alert list to a serialized file for later access.
   */
  public void saveAlerts() {
    try {
      FileOutputStream fos = new FileOutputStream(alertStorageFile, false);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(alertCache.serialize(SerializableAlert::of));
      oos.close();
      fos.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * Generate a {@link RotatingList} from a serialized file.
   *
   * @return the deserialized list
   * @throws Exception if an issue with creating a list
   */
  @SuppressWarnings("unchecked")
  public RotatingList<SerializableAlert> restoreAlerts() throws Exception {
    FileInputStream fis = new FileInputStream(alertStorageFile);
    ObjectInputStream ois = new ObjectInputStream(fis);

    final RotatingArrayList<SerializableAlert> rotatingArrayList =
        ((RotatingArrayList.Serializable<SerializableAlert>) ois.readObject()).deserialize();

    ois.close();
    fis.close();
    if (alertStorageFile.delete()) {
      GriefAlert.getInstance().getLogger().debug("Deleted alert storage file");
    }
    return rotatingArrayList;
  }

  /**
   * Clear all alert data.
   */
  public void clearAll() {
    this.getAlertCache().clear();
    this.grieferRepeatHistory.clearAll();
    this.officerCheckHistory.clearAll();
  }

}
