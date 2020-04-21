/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert.caches;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.GeneralAlert;
import com.minecraftonline.griefalert.alerts.prism.PrismAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.alerts.AlertInspection;
import com.minecraftonline.griefalert.api.events.PreBroadcastAlertEvent;
import com.minecraftonline.griefalert.api.events.PreInspectAlertEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.services.AlertService;
import com.minecraftonline.griefalert.api.services.Request;
import com.minecraftonline.griefalert.api.structures.HashMapStack;
import com.minecraftonline.griefalert.api.structures.MapStack;
import com.minecraftonline.griefalert.api.structures.RotatingArrayList;
import com.minecraftonline.griefalert.api.structures.RotatingList;
import com.minecraftonline.griefalert.commands.CheckCommand;
import com.minecraftonline.griefalert.util.Alerts;
import com.minecraftonline.griefalert.util.Communication;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.SpongeUtil;
import com.minecraftonline.griefalert.util.enums.AlertTags;
import com.minecraftonline.griefalert.util.enums.Permissions;
import com.minecraftonline.griefalert.util.enums.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public final class AlertServiceImpl implements AlertService {

  // A map relating each player to a list of consecutive similar alerts for silencing
  private final MapStack<UUID, GriefProfile> grieferRepeatHistory = new HashMapStack<>();
  private final MapStack<UUID, Transform<World>> officerCheckHistory = new HashMapStack<>();
  private final RotatingList<AlertItem> alertCache;
  private final File alertStorageFile;

  /**
   * Default constructor.
   */
  public AlertServiceImpl() {
    alertStorageFile = new File(
        GriefAlert.getInstance().getDataDirectory().getAbsolutePath()
            + "/alerts.ser");
    this.alertCache = generateAlertList();
  }

  @Override
  public int submit(@NotNull Alert alert) {

    if (Permissions.has(Alerts.getGriefer(alert), Permissions.GRIEFALERT_SILENT)) {
      alert.setSilent(true);
    }

    try {
      SerializationUtils.serialize(alert);
    } catch (SerializationException e) {
      GriefAlert.getInstance().getLogger().error(
          "An alert that was submitted to the AlertService does not correctly implement "
              + "java.io.Serializable. The saved Alerts might not be recoverable on restart.");
    }
    int index = push(alert);

    PreBroadcastAlertEvent.post(
        alert,
        Alerts.getGriefer(alert),
        GriefAlert.getInstance().getPluginContainer());

    broadcast(index);

    return index;
  }

  /**
   * Add the given alert to local storage and update the alert repeat history.
   *
   * @param alert the alert to push
   * @return the retrieval code for this alert
   */
  public int push(@Nonnull final Alert alert) {
    // Push the alert to the RotatingQueue
    int output = alertCache.push(AlertItem.of(alert, alertCache.cursor()));

    // Load the info for all alerts
    if (alert instanceof GeneralAlert) {
      GeneralAlert generalAlert = (GeneralAlert) alert;
      generalAlert.getDetails().forEach(detail -> detail.get(alert));
    }

    updateRepeatHistory(alert);
    return output;
  }

  private void updateRepeatHistory(@Nonnull final Alert alert) {
    UUID grieferUuid = alert.getGrieferUuid();
    if (grieferRepeatHistory.peek(grieferUuid)
        .filter(griefProfile -> alert.getGriefProfile().equals(griefProfile))
        .isPresent()) {
      alert.setSilent(true);
    } else {
      grieferRepeatHistory.clear(grieferUuid);
    }
    grieferRepeatHistory.push(grieferUuid, alert.getGriefProfile());
    int silentAlertLimit = Settings.MAX_HIDDEN_REPEATED_EVENTS.getValue();
    if (grieferRepeatHistory.size(grieferUuid) >= silentAlertLimit) {
      grieferRepeatHistory.clear(grieferUuid);
    }
  }

  @Nonnull
  @Override
  public Alert getAlert(int index) throws IllegalArgumentException {
    try {
      return alertCache.get(index).get();
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException(String.format("There is no Alert with index %d", index));
    }
  }

  private List<AlertItem> getAlerts(Request filters, Sort sort) {
    List<AlertItem> allAlerts;
    switch (sort) {
      case CHRONOLOGICAL:
        allAlerts = alertCache.getDataByTime();
        break;
      case REVERSE_CHRONOLOGICAL:
        allAlerts = alertCache.getDataByTime();
        Collections.reverse(allAlerts);
        break;
      case INDEX:
        allAlerts = alertCache.getDataByIndex();
        break;
      case REVERSE_INDEX:
        allAlerts = alertCache.getDataByIndex();
        Collections.reverse(allAlerts);
        break;
      default:
        throw new RuntimeException("Unsupported Sort type");
    }
    return allAlerts.stream()
        .filter(alert -> filters.getPlayerUuids().isEmpty()
            || filters.getPlayerUuids().contains(alert.get().getGrieferUuid()))
        .filter(alert -> filters.getEvents().isEmpty()
            || filters.getEvents().contains(alert.get().getGriefEvent()))
        .filter(alert -> filters.getTargets().isEmpty()
            || filters.getTargets().stream().anyMatch(str -> alert.get().getTarget().contains(str)))
        .collect(Collectors.toList());
  }

  @Override
  public void lookup(@NotNull Collection<MessageReceiver> receivers,
                     @NotNull Request filters,
                     @Nonnull Sort sort,
                     boolean spread) {
    List<AlertItem> alerts = getAlerts(filters, sort);
    if (alerts.isEmpty()) {
      receivers.forEach(receiver -> receiver.sendMessage(
          Format.info("There are no alerts matching those parameters")));
      return;
    }
    PaginationList.builder()
        .title(Text.of(TextColors.YELLOW, "Alert Lookup"))
        .header(Format.request(filters))
        .contents(formatAlerts(alerts, spread))
        .padding(Format.bonus("="))
        .build()
        .sendTo(receivers);
  }

  @Override
  public void reset() {
    this.alertCache.clear();
    this.grieferRepeatHistory.clearAll();
    this.officerCheckHistory.clearAll();
  }

  private List<Text> formatAlerts(List<AlertItem> alerts, boolean spread) {

    if (alerts.isEmpty()) {
      return Lists.newLinkedList();
    }

    if (spread) {
      return alerts.stream().map(alert ->
          Format.buildBroadcast(alert.get(), alert.index())).collect(Collectors.toList());
    }

    LinkedList<LinkedList<AlertItem>> collapsed = Lists.newLinkedList();
    collapsed.add(Lists.newLinkedList());
    alerts.forEach(alert -> {
      if (collapsed.getLast().isEmpty()
          || (collapsed.getLast().getLast().get().getGriefProfile()
          .equals(alert.get().getGriefProfile())
          && collapsed.getLast().getLast().get().getGrieferUuid()
          .equals(alert.get().getGrieferUuid()))) {
        collapsed.getLast().add(alert);
      } else {
        LinkedList<AlertItem> list = Lists.newLinkedList();
        list.add(alert);
        collapsed.add(list);
      }
    });
    return collapsed.stream()
        .map(list -> {
          AlertItem firstAlert = list.getFirst();
          List<Text> tokens = Lists.newLinkedList();
          tokens.add(Format.userName(Alerts.getGriefer(firstAlert.get())));
          tokens.add(Text.of(firstAlert.get().getGriefProfile()
                  .getColored(GriefProfile.Colorable.EVENT)
                  .orElse(Format.ALERT_EVENT_COLOR),
              Format.action(firstAlert.get().getGriefEvent())));
          tokens.add(Text.of(firstAlert.get().getGriefProfile()
                  .getColored(GriefProfile.Colorable.TARGET)
                  .orElse(Format.ALERT_TARGET_COLOR),
              Format.item(firstAlert.get().getTarget())));
          tokens.add(Text.of(firstAlert.get().getGriefProfile()
                  .getColored(GriefProfile.Colorable.DIMENSION)
                  .orElse(Format.ALERT_DIMENSION_COLOR),
              Format.dimension(Alerts.getWorld(firstAlert.get()).getDimension().getType())));
          if (list.size() < 5) {
            tokens.add(Text.joinWith(Format.space(),
                list.stream()
                    .map(alert -> CheckCommand.clickToCheck(alert.index()))
                    .collect(Collectors.toList())));
          } else {
            tokens.add(Text.joinWith(Format.space(),
                CheckCommand.clickToCheck(list.get(0).index()),
                Format.bonus("..."),
                Format.bonus("x", list.size() - 2),
                Format.bonus("..."),
                CheckCommand.clickToCheck(list.get(list.size() - 1).index())));
          }
          return Text.joinWith(Format.bonus(", "), tokens);
        }).collect(Collectors.toList());
  }

  @Override
  public boolean inspect(int index, @NotNull Player officer, boolean force)
      throws IndexOutOfBoundsException {

    // Perform all checks to make sure it will work
    AlertItem alertItem = alertCache.get(index);

    // See if a staff member has already checked this alert recently
    if (Settings.ALERT_CHECK_TIMEOUT.getValue() > 0
        && !alertItem.getChecks().isEmpty()
        && !alertItem.getChecks().get(0).getOfficerUuid().equals(officer.getUniqueId())) {
      AlertInspection firstCheck = alertItem.getChecks().get(0);
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
    Transform<World> grieferTransform = Alerts.buildTransform(alertItem.get());
    if (force) {
      officer.setTransform(grieferTransform);
    } else {
      if (!officer.setTransformSafely(grieferTransform)) {
        Errors.sendCannotTeleportSafely(officer, grieferTransform);
        return false;
      }
    }

    // Post an event to show that the Alert is getting checked
    PluginContainer plugin = GriefAlert.getInstance().getPluginContainer();
    PreInspectAlertEvent.post(alertItem.get(), officer, plugin);

    // The officer has teleported successfully, so save their previous location in the history
    this.addOfficerTransform(officer.getUniqueId(), officerPreviousTransform);

    // Send the messages
    Communication.getStaffBroadcastChannelWithout(officer).send(Format.info(
        Format.userName(officer),
        " is checking alert number ",
        Format.bonus(CheckCommand.clickToCheck(index))));

    officer.sendMessage(Format.heading("Checking Alert: ",
        Format.bonus(index)));
    officer.sendMessage(alertItem.get().getMessage());

    // Notify the officer of other staff members who may have checked this alert already
    if (!alertItem.getChecks().isEmpty()) {
      officer.sendMessage(Format.info(
          "This alert has already been checked by: ",
          Text.joinWith(
              Format.bonus(", "),
              Sets.newHashSet(
                  alertItem.getChecks()
                      .stream()
                      .map(AlertInspection::getOfficerUuid)
                      .collect(Collectors.toList()))
                  .stream()
                  .map(uuid -> SpongeUtil.getUser(uuid)
                      .map(Format::userName)
                      .orElse(Text.of("Unknown")))
                  .limit(10)
                  .collect(Collectors.toList()))));
    }

    // Build a panel for the officer with useful commands
    Text.Builder panel = Text.builder().append(Format.bonus("=="));
    if (officer.hasPermission(Permissions.GRIEFALERT_COMMAND_QUERY.toString())) {
      panel.append(
          Format.space(2),
          AlertTags.getTagRecent(Alerts.getGriefer(alertItem.get()).getName()));
    }
    if (officer.hasPermission(Permissions.GRIEFALERT_COMMAND_SHOW.toString())) {
      panel.append(
          Format.space(2),
          AlertTags.getTagShow(index));
    }
    if (officer.hasPermission(Permissions.GRIEFALERT_COMMAND_INFO.toString())) {
      panel.append(
          Format.space(2),
          AlertTags.getTagInfo(index));
    }
    panel.append(
        Format.space(2),
        AlertTags.getTagReturn());

    if (alertItem.get() instanceof PrismAlert
        && officer.hasPermission(Permissions.GRIEFALERT_COMMAND_ROLLBACK.toString())) {
      panel.append(Text.of(
          Format.space(2),
          AlertTags.getTagFix(index)));

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
              Format.bonus(index),
              " has been revoked"));
    }

    alertItem.addCheck(new AlertInspection(officer.getUniqueId(), new Date()));
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
   */
  public boolean unInspect(@NotNull Player officer) {

    Optional<Transform<World>> previousTransformOptional = officerCheckHistory
        .pop(officer.getUniqueId());
    int revertsRemaining = officerCheckHistory.size(officer.getUniqueId());

    if (!previousTransformOptional.isPresent()) {
      officer.sendMessage(Format.info("You have no previous location."));
      return false;
    }

    if (!officer.setTransformSafely(previousTransformOptional.get())) {
      Errors.sendCannotTeleportSafely(officer, previousTransformOptional.get());
    } else {
      officer.sendMessage(Format.success("Returned to previous location."));
    }
    officer.sendMessage(Format.info(
        Format.bonus(revertsRemaining),
        " previous locations available."));

    return true;
  }

  private void addOfficerTransform(UUID officerUuid, Transform<World> transform) {
    officerCheckHistory.push(officerUuid, transform);
  }

  /**
   * Send the {@link Alert} at the given location in the alert cache.
   *
   * @param index the location of the {@link Alert} in the alert cache
   * @return true if the alert was correctly broadcast
   * @throws IndexOutOfBoundsException if the given index is invalid
   */
  public boolean broadcast(int index) throws IllegalArgumentException {
    Alert alert;
    try {
      alert = alertCache.get(index).get();
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException("Could not broadcast an Alert at the index: " + index);
    }
    if (alert.isSilent()) {
      return false;
    } else {
      Communication.getStaffBroadcastChannel().send(Format.buildBroadcast(alert, index));
      return true;
    }
  }

  /**
   * Give a {@link RotatingList}, generated by either a temporary file
   * or a completely new one if the storage file doesn't exist.
   *
   * @return the new list
   */
  public RotatingList<AlertItem> generateAlertList() {
    if (alertStorageFile.exists()) {
      try {
        return restoreAlerts();
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
      oos.writeObject(alertCache);
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
  public RotatingList<AlertItem> restoreAlerts() throws Exception {
    FileInputStream fis = new FileInputStream(alertStorageFile);
    ObjectInputStream ois = new ObjectInputStream(fis);

    final RotatingArrayList<AlertItem> rotatingArrayList =
        (RotatingArrayList<AlertItem>) ois.readObject();

    ois.close();
    fis.close();
    if (alertStorageFile.delete()) {
      GriefAlert.getInstance().getLogger().debug("Deleted alert storage file");
    }
    return rotatingArrayList;
  }

  private static class AlertItem implements Serializable {
    private final Alert alert;
    private final List<AlertInspection> checks = Lists.newLinkedList();
    private final int index;

    private AlertItem(Alert alert, int index) {
      this.alert = alert;
      this.index = index;
    }

    static AlertItem of(Alert alert, int index) {
      return new AlertItem(alert, index);
    }

    public Alert get() {
      return alert;
    }

    public int index() {
      return index;
    }

    public void addCheck(@Nonnull AlertInspection check) {
      checks.add(check);
    }

    @Nonnull
    public List<AlertInspection> getChecks() {
      return checks;
    }

  }

}