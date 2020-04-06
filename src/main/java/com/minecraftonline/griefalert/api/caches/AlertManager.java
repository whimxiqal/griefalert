/* Created by PietElite */

package com.minecraftonline.griefalert.api.caches;

import com.google.common.collect.Sets;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.prism.PrismAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.alerts.AlertCheck;
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
import com.minecraftonline.griefalert.util.SpongeUtil;
import com.minecraftonline.griefalert.util.enums.Permissions;
import com.minecraftonline.griefalert.util.enums.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
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
   * @param force   whether the teleportation is performed regardless if it's safe
   * @return true if the officer correctly checked the alert
   * @see Alert
   */
  public boolean check(Alert alert, Player officer, boolean force) {

    // Perform all checks to make sure it will work

    // See if a staff member has already checked this alert recently
    if (Settings.ALERT_CHECK_TIMEOUT.getValue() > 0
        && !alert.getChecks().isEmpty()
        && !alert.getChecks().get(0).getOfficerUuid().equals(officer.getUniqueId())) {
      AlertCheck firstCheck = alert.getChecks().get(0);
      double secondsSinceFirstCheck =
          ((double) (Instant.now().toEpochMilli()
              - firstCheck.getChecked().toInstant().toEpochMilli())
          ) / 1000;
      if (secondsSinceFirstCheck < Settings.ALERT_CHECK_TIMEOUT.getValue()) {
        officer.sendMessage(Format.error(
            SpongeUtil.getUser(firstCheck.getOfficerUuid())
                .map(Format::userName)
                .orElse(Text.of("An unknown officer")),
            " is currently checking this alert. Please wait ",
            Format.bonus(
                Settings.ALERT_CHECK_TIMEOUT.getValue()
                    - (int) Math.floor(secondsSinceFirstCheck)),
            " seconds."));
        return false;
      }
    }

    // Save the officer's previous transform and add it into the alert's database later
    // if the officer successfully teleports.
    Transform<World> officerPreviousTransform = officer.getTransform();

    // Teleport the officer
    if (force) {
      officer.setTransform(alert.getGrieferTransform());
    } else {
      if (!officer.setTransformSafely(alert.getGrieferTransform())) {
        Errors.sendCannotTeleportSafely(officer, alert.getGrieferTransform());
        return false;
      }
    }

    // Post an event to show that the Alert is getting checked
    PluginContainer plugin = GriefAlert.getInstance().getPluginContainer();
    EventContext eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, plugin).build();
    Sponge.getEventManager()
        .post(new PreCheckAlertEvent(alert, Cause.of(eventContext, plugin), officer));


    // The officer has teleported successfully, so save their previous location in the history
    this.addOfficerTransform(officer.getUniqueId(), officerPreviousTransform);

    // Send the messages
    Communication.getStaffBroadcastChannelWithout(officer).send(Format.info(
        Format.userName(officer),
        " is checking alert number ",
        Format.bonus(CheckCommand.clickToCheck(alert.getCacheIndex()))));

    // Notify the officer of other staff members who may have checked this alert already
    if (!alert.getChecks().isEmpty()) {
      officer.sendMessage(Format.info(
          "This alert has already been checked by: ",
          Text.joinWith(
              Format.bonus(", "),
              Sets.newHashSet(
                  alert.getChecks()
                      .stream()
                      .map(AlertCheck::getOfficerUuid)
                      .collect(Collectors.toList()))
                  .stream()
                  .map(uuid -> SpongeUtil.getUser(uuid)
                      .map(Format::userName)
                      .orElse(Text.of("Unknown")))
                  .limit(10)
                  .collect(Collectors.toList()))));
    }

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

    // Give officer invulnerability
    if (Settings.CHECK_INVULNERABILITY.getValue() > 0) {
      giveInvulnerability(
          officer,
          Settings.CHECK_INVULNERABILITY.getValue(),
          Format.info(String.format(
              "You have been given %d seconds of invulnerability",
              Settings.CHECK_INVULNERABILITY.getValue())),
          Format.info(
              "Invulnerability from alert ",
              Format.bonus(alert.getCacheIndex()),
              " has been revoked"));
    }

    alert.addCheck(new AlertCheck(officer.getUniqueId(), new Date()));
    return true;
  }

  private void giveInvulnerability(Player player,
                                   int seconds,
                                   Text giveMessage,
                                   Text revokeMessage) {
    // Give invulnerability
    player.sendMessage(giveMessage);
    UUID playerUuid = player.getUniqueId();
    EventListener<DamageEntityEvent> cancelDamage = event -> {
      if (event.getTargetEntity().getUniqueId().equals(playerUuid)) {
        event.setCancelled(true);
      }
    };
    Sponge.getEventManager().registerListener(
        GriefAlert.getInstance(),
        DamageEntityEvent.class,
        cancelDamage);
    Optional<Player> playerOptional = Optional.of(player);
    Task.builder().delay(seconds, TimeUnit.SECONDS)
        .execute(() -> {
          Sponge.getEventManager().unregisterListeners(cancelDamage);
          // Garbage collection might get rid of this player? Made it optional just in case.
          playerOptional.ifPresent(p -> p.sendMessage(revokeMessage));
        })
        .name("Remove invulnerability for player " + player.getName())
        .submit(GriefAlert.getInstance());
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
          if (serializableAlert == null) {
            return null;
          }
          return serializableAlert.deserialize();
        });
      } catch (Exception e) {
        GriefAlert.getInstance().getLogger().error(
            "An error occurred while loading the latest Alert cache. "
                + "The serialized items are likely outdated. "
                + "A fresh cache was created.");
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
    } catch (IOException e) {
      GriefAlert.getInstance().getLogger().error("Alert cache could not be serialized and saved.");
      e.printStackTrace();
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
