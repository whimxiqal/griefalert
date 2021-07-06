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

package com.minecraftonline.griefalert.sponge.alert.caches;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.minecraftonline.griefalert.SpongeGriefAlert;
import com.minecraftonline.griefalert.sponge.alert.alerts.GeneralAlert;
import com.minecraftonline.griefalert.common.alert.alerts.Alert;
import com.minecraftonline.griefalert.common.alert.alerts.inspections.AlertInspection;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvent;
import com.minecraftonline.griefalert.common.alert.events.PreBroadcastAlertEvent;
import com.minecraftonline.griefalert.common.alert.events.PreInspectAlertEvent;
import com.minecraftonline.griefalert.common.alert.records.GriefProfile;
import com.minecraftonline.griefalert.common.alert.services.AlertService;
import com.minecraftonline.griefalert.common.alert.services.AlertRequest;
import com.minecraftonline.griefalert.common.alert.structures.RotatingArrayList;
import com.minecraftonline.griefalert.common.alert.structures.RotatingList;
import com.minecraftonline.griefalert.sponge.alert.commands.CheckCommand;
import com.minecraftonline.griefalert.sponge.alert.inventories.InspectionInventory;
import com.minecraftonline.griefalert.sponge.alert.util.Alerts;
import com.minecraftonline.griefalert.sponge.alert.util.Communication;
import com.minecraftonline.griefalert.sponge.alert.util.Errors;
import com.minecraftonline.griefalert.sponge.alert.util.Format;
import com.minecraftonline.griefalert.sponge.alert.util.SpongeUtil;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Permissions;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Settings;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * The implementation for the {@link AlertService} interface.
 */
public final class AlertServiceImpl implements AlertService {

  // A map relating each player to a list of consecutive similar alerts for silencing
  private final Map<UUID, RecentAlertHistory> grieferRepeatHistory = new HashMap<>();
  private final Table<UUID, Integer, AlertInspection> officerInspectHistory = HashBasedTable.create();
  private final File alertStorageFile;
  private RotatingList<AlertItem> alertCache;

  /**
   * Default constructor.
   */
  public AlertServiceImpl() {
    alertStorageFile = new File(
        SpongeGriefAlert.getSpongeInstance().getDataDirectory().getAbsolutePath()
            + "/alerts.ser");
    this.alertCache = generateAlertList();
  }

  @Override
  public int submit(@NotNull Alert alert) {

    // Silence alert if the player has the associated permission
    if (Permissions.has(Alerts.getGriefer(alert), Permissions.GRIEFALERT_SILENT)) {
      alert.setSilent(true);
    }

    // Silence alerts based on translucency logic
    if (alert.getGriefProfile().isTranslucent()) {
      if (alert instanceof GeneralAlert) {
        ((GeneralAlert) alert).getDetails()
            .stream()
            .filter(detail -> detail.getLabel().equalsIgnoreCase("Block Creator"))
            .findFirst()
            .flatMap(detail -> detail.evaluateInfo(alert))
            .filter(text -> text.toPlain().equalsIgnoreCase(Alerts.getGriefer(alert).getName()))
            .ifPresent(text -> alert.setSilent(true));
      }
    }

    try {
      SerializationUtils.serialize(alert);
    } catch (SerializationException e) {
      SpongeGriefAlert.getSpongeInstance().getLogger().error(
          "An alert that was submitted to the AlertService does not correctly implement "
              + "java.io.Serializable. The saved Alerts might not be recoverable on restart.");
    }
    int index = push(alert);

    PreBroadcastAlertEvent.post(
        alert,
        Alerts.getGriefer(alert),
        SpongeGriefAlert.getSpongeInstance().getPluginContainer());

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
    Set<Map.Entry<UUID, AlertInspection>> inspectionSetCopy = new HashSet<>(officerInspectHistory
        .column(output)
        .entrySet());
    inspectionSetCopy.forEach(inspectionEntry ->
        officerInspectHistory.remove(inspectionEntry.getKey(), output));

    //.forEach(((uuid, inspection) -> officerInspectHistory.remove(uuid, output)));
    return output;
  }

  private void updateRepeatHistory(@Nonnull final Alert alert) {
    RecentAlertHistory history = grieferRepeatHistory.computeIfAbsent(alert.getGrieferUuid(),
        uuid -> new RecentAlertHistory());
    if (history.put(alert.getGriefProfile())) {
      alert.setSilent(true);
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

  private List<AlertItem> getAlerts(AlertRequest filters, Sort sort) {
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
    Stream<AlertItem> stream = allAlerts.stream()
        .filter(alert -> filters.getPlayerUuids().isEmpty()
            || filters.getPlayerUuids().contains(alert.get().getGrieferUuid()))
        .filter(alert -> filters.getEvents().isEmpty()
            || filters.getEvents().stream().map(GriefEvent::getId).anyMatch(id ->
            id.equals(alert.get().getGriefEvent().getId())))
        .filter(alert -> filters.getTargets().isEmpty()
            || filters.getTargets().stream().anyMatch(str -> alert.get().getTarget().contains(str)));
    if (filters.getMaximum().isPresent()) {
      stream = stream.limit(filters.getMaximum().get());
    }
    return stream.collect(Collectors.toList());
  }

  @Override
  public void lookup(@NotNull Collection<MessageReceiver> receivers,
                     @NotNull AlertRequest filters,
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
    this.alertCache = generateAlertList();
    this.grieferRepeatHistory.values().forEach(RecentAlertHistory::clear);
    this.grieferRepeatHistory.clear();
    this.officerInspectHistory.clear();
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
                  .getColored(GriefProfile.Colorable.WORLD)
                  .orElse(Format.ALERT_WORLD_COLOR),
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
  public boolean inspect(int index, @NotNull Player officer, boolean force, boolean block)
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
              - firstCheck.getInspected().toEpochMilli())
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
    final Transform<World> officerPreviousTransform = officer.getTransform();

    // Teleport the officer
    officer.getVehicle().ifPresent(Entity::clearPassengers);
    officer.clearPassengers();
    Transform<World> grieferTransform = Alerts.buildTransform(alertItem.get());
    Location<World> griefLocation = Alerts.getGriefLocation(alertItem.get());
    if (force) {
      if (block) {
        officer.setLocation(griefLocation);
      } else {
        officer.setTransform(grieferTransform);
      }
    } else {
      if (block) {
        if (!officer.setLocationSafely(griefLocation)) {
          Errors.sendCannotTeleportSafely(officer, griefLocation);
          return false;
        }
      } else {
        if (!officer.setTransformSafely(grieferTransform)) {
          Errors.sendCannotTeleportSafely(officer, grieferTransform);
          return false;
        }
      }
    }

    // Post an event to show that the Alert is getting checked
    PreInspectAlertEvent.post(alertItem.get(), officer, SpongeGriefAlert.getSpongeInstance().getPluginContainer());

    // Send the messages
    Communication.getStaffBroadcastChannelWithout(officer).send(Format.info(
        Format.userName(officer),
        " is checking alert number ",
        Format.bonus(CheckCommand.clickToCheck(index))));

    officer.sendMessage(Format.heading(
        "Inspecting Alert ",
        Format.bonus(index),
        Format.space(),
        Format.command("PANEL",
            "/griefalert panel",
            Text.of(Format.prefix(),
                Format.endLine(),
                Format.bonus("Open an Inspection Panel")))));

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

    AlertInspection inspection = new AlertInspection(
        officer.getUniqueId(),
        alertItem.get().getGrieferUuid(),
        alertItem.get().getGriefEvent().getId(),
        alertItem.get().getTarget(),
        alertItem.get().getGriefPosition(),
        alertItem.get().getWorldUuid(),
        Instant.now(),
        officerPreviousTransform,
        index);
    officerInspectHistory.put(officer.getUniqueId(), index, inspection);
    alertItem.addCheck(inspection);
    SpongeGriefAlert.getSpongeInstance().getInspectionStorage().write(inspection);

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
        SpongeGriefAlert.getSpongeInstance(),
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
        .submit(SpongeGriefAlert.getSpongeInstance());
  }

  @Override
  public boolean uninspect(@NotNull Player officer) {

    Optional<AlertInspection> lastInspection = getLastReturnableInspection(officer.getUniqueId());

    if (!lastInspection.isPresent()) {
      officer.sendMessage(Format.info("You have no previous location"));
      return false;
    }

    return uninspect(officer, lastInspection.get().getAlertIndex());
  }

  @Override
  public boolean uninspect(@NotNull Player officer, int index) {

    AlertInspection inspection = officerInspectHistory.get(
        officer.getUniqueId(),
        index);

    if (inspection == null) {
      officer.sendMessage(Format.info(
          "You have no previous location for alert ",
          Format.bonus(index)));
      return false;
    }

    if (Settings.INSPECTION_RETURN_TIMEOUT.getValue() >= 0) {
      if (inspection.getInspected()
          .isBefore(Instant.now()
          .minus(Settings.INSPECTION_RETURN_TIMEOUT.getValue(), ChronoUnit.MINUTES))) {
        officer.sendMessage(Format.error("Your return location has expired"));
        return false;
      }
    }

    if (inspection.isUninspected()) {
      officer.sendMessage(Format.error(
          "You have already returned from alert ",
          Format.bonus(index)
      ));
      return false;
    }

    if (!officer.setTransformSafely(inspection.getPreviousTransform())) {
      Errors.sendCannotTeleportSafely(officer, inspection.getPreviousTransform());
    } else {
      inspection.uninspect();
      officer.sendMessage(Format.success(
          "Returned to location before grief check ",
          Format.bonus(inspection.getAlertIndex()),
          "!"));
    }

    return true;
  }

  @Override
  public boolean openPanel(@Nonnull Player officer) {
    Optional<AlertInspection> lastInspection = getLastInspection(officer.getUniqueId());
    if (lastInspection.isPresent()) {
      InspectionInventory.openInspectionPanel(officer,
          getInspectionsByTime(officer.getUniqueId()).getLast().getAlertIndex());
      return true;
    } else {
      return false;
    }
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
        SpongeGriefAlert.getSpongeInstance().getLogger().error(
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
      SpongeGriefAlert.getSpongeInstance().getLogger().error("Alert cache could not be serialized and saved.");
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
      SpongeGriefAlert.getSpongeInstance().getLogger().debug("Deleted alert storage file");
    }
    return rotatingArrayList;
  }

  private LinkedList<AlertInspection> getInspectionsByTime(UUID officerUuid) {
    List<AlertInspection> inspections = Lists.newLinkedList();
    inspections.addAll(officerInspectHistory.row(officerUuid).values());
    inspections.sort(Comparator.comparing(AlertInspection::getInspected));
    return Lists.newLinkedList(inspections);
  }

  @Nonnull
  @Override
  public Optional<AlertInspection> getLastInspection(Player officer) {
    return this.getLastInspection(officer.getUniqueId());
  }

  private Optional<AlertInspection> getLastInspection(UUID officerUuid) {
    LinkedList<AlertInspection> inspections = getInspectionsByTime(officerUuid);
    if (inspections.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(inspections.getLast());
  }

  private Optional<AlertInspection> getLastReturnableInspection(UUID officerUuid) {
    LinkedList<AlertInspection> inspections = getInspectionsByTime(officerUuid);
    AlertInspection inspection;
    for (Iterator<AlertInspection> it = inspections.descendingIterator(); it.hasNext(); ) {
      inspection = it.next();
      if (!inspection.isUninspected()) {
        return Optional.of(inspection);
      }
    }
    return Optional.empty();
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
