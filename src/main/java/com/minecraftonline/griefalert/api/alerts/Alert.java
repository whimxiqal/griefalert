package com.minecraftonline.griefalert.api.alerts;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.structures.MapStack;
import com.minecraftonline.griefalert.util.Comms;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import com.minecraftonline.griefalert.util.Permissions;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

/**
 * An object to hold all information about an Alert caused by an
 * event matching a {@link GriefProfile}.
 */
public abstract class Alert implements Runnable {

  private int stackIndex;
  private final GriefProfile griefProfile;
  private boolean silent = false;

  protected Alert(GriefProfile griefProfile) {
    this.griefProfile = griefProfile;
  }

  public final boolean checkBy(Player officer) {

    // Prepare PrismRecord for sending to a PrismRecordEvent
    PluginContainer plugin = GriefAlert.getInstance().getPluginContainer();
    EventContext eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, plugin).build();

    PreCheckAlertEvent preCheckAlertEvent = new PreCheckAlertEvent(this, Cause.of(eventContext, plugin));

    // Tell Sponge that this PrismRecordEvent has occurred
    Sponge.getEventManager().post(preCheckAlertEvent);

    // Get the necessary transform so the officer can teleport
    if (!getTransform().isPresent()) {
      return false;
    }

    Transform<World> alertTransform = getTransform().get();

    // Save the officer's previous transform and add it into the alert's database later
    // if the officer successfully teleports.
    Transform<World> officerPreviousTransform = officer.getTransform();

    // CheckEvent

    // Teleport the officer
    if (!officer.setTransformSafely(alertTransform)) {
      Errors.sendCannotTeleportSafely(officer, alertTransform);
      return false;
    }

    // The officer has teleported successfully, so save their previous location in the history
    GriefAlert.getInstance().getAlertQueue().getOfficerCheckHistory().push(
        officer.getUniqueId(),
        officerPreviousTransform);

    officer.sendMessage(Format.heading("Checking Grief Alert:"));
    officer.sendMessage(getSummary());
    Comms.getStaffBroadcastChannel().send(Format.info(
        General.formatPlayerName(officer),
        " is checking alert number ",
        Format.bonus(clickToCheck(getStackIndex()))));
    return true;

  }

  public static Optional<Integer> revertTransform(Player officer) {

    MapStack<UUID, Transform<World>> history = GriefAlert
        .getInstance()
        .getAlertQueue()
        .getOfficerCheckHistory();

    Optional<Transform<World>> previousTransformOptional = history.pop(officer.getUniqueId());

    if (!previousTransformOptional.isPresent()) {
      return Optional.empty();
    }

    if (!officer.setTransformSafely(previousTransformOptional.get())) {
      Errors.sendCannotTeleportSafely(officer, previousTransformOptional.get());
    }

    return Optional.of(history.size(officer.getUniqueId()));

  }


  /**
   * Getter for the bare Text without interactivity.
   *
   * @return the Text representation of this Alert.
   */
  public Text getMessageText() {
    Text.Builder builder = Text.builder();
    builder.append(Text.of(
        General.formatPlayerName(getGriefer()),
        Format.space(),
        getEventColor(), getGriefEvent().getPreterite(),
        Format.space(),
        getTargetColor(), Grammar.addIndefiniteArticle(getTarget().replace("minecraft:", ""))));
    getTransform().ifPresent((transform -> builder.append(Text.of(
        TextColors.RED, " in the ",
        getDimensionColor(), transform.getExtent().getDimension().getType().getName()))));
    return builder.build();
  }

  public abstract Optional<Transform<World>> getTransform();

  public abstract Player getGriefer();

  public abstract GriefEvent getGriefEvent();

  public final String getTarget() {
    return griefProfile.getTarget();
  }

  ;

  public Optional<String> getExtraSummaryContent() {
    return Optional.empty();
  }

  /**
   * Getter for the integer which represents this cached item in the ongoing.
   * AlertCache, which implements a RotatingQueue.
   *
   * @return integer for cache code
   */
  public final int getStackIndex() {
    return stackIndex;
  }

  public boolean isSilent() {
    return silent;
  }

  public void setSilent(boolean silent) {
    this.silent = silent;
  }

  public void pushAndRun() {
    GriefAlert.getInstance().getAlertQueue().push(this);
    run();
  }

  @Override
  public void run() {

    if (getGriefer().hasPermission(Permissions.GRIEFALERT_SILENT.toString())) {
      setSilent(true);
    }

    if (!isSilent()) {
      Comms.getStaffBroadcastChannel().send(getFullText());
    }

  }

  /**
   * Get the final text version of the Alert to broadcast to staff.
   *
   * @return the Text for broadcasting.
   */
  public final Text getFullText() {
    return getFullText(Lists.newArrayList(getStackIndex()));
  }

  /**
   * Get the final text version of the Alert to broadcast to staff with
   * other interactive items to access other alerts.
   *
   * @param allIndices The list of other items to add to the text.
   * @return A full text to be broadcast to staff.
   */
  public Text getFullText(List<Integer> allIndices) {
    Text.Builder builder = Text.builder().append(getMessageText());
    allIndices.forEach((i) -> {
      builder.append(Format.space());
      builder.append(clickToCheck(i));
    });
    return builder.build();
  }

  private Text clickToCheck(int index) {
    return Format.command(String.valueOf(index),
        "/ga check " + index,
        GriefAlert.getInstance().getAlertQueue().get(index).getSummary());
  }

  public final boolean isRepeatOf(Alert other) {
    return getMessageText().equals(other.getMessageText());
  }

  protected final TextColor getEventColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.EVENT_COLOR).map((s) -> Sponge.getRegistry().getType(TextColor.class, s)
            .orElse(Format.ALERT_EVENT_COLOR))
        .orElse(Format.ALERT_EVENT_COLOR);
  }

  protected final TextColor getTargetColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.TARGET_COLOR).map((s) -> Sponge.getRegistry().getType(TextColor.class, s)
            .orElse(Format.ALERT_TARGET_COLOR))
        .orElse(Format.ALERT_TARGET_COLOR);
  }

  protected final TextColor getDimensionColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.DIMENSION_COLOR).map((s) -> Sponge.getRegistry().getType(TextColor.class, s)
            .orElse(Format.ALERT_DIMENSION_COLOR))
        .orElse(Format.ALERT_DIMENSION_COLOR);
  }

  public Text getSummary() {
    Text.Builder builder = Text.builder();

    builder.append(Text.of(TextColors.DARK_AQUA, "Player: ", TextColors.GRAY, General.formatPlayerName(getGriefer())), Format.endLine());
    builder.append(Text.of(TextColors.DARK_AQUA, "Event: ", TextColors.GRAY, griefProfile.getGriefEvent().getId()), Format.endLine());
    builder.append(Text.of(TextColors.DARK_AQUA, "Target: ", TextColors.GRAY, griefProfile.getTarget().replace("minecraft:", "")), Format.endLine());
    getExtraSummaryContent().ifPresent((content) ->
        builder.append(Text.of(TextColors.DARK_AQUA, "Extra: ", TextColors.GRAY, content, Format.endLine()))
    );
    getTransform().ifPresent((transform) ->
        builder.append(Text.of(TextColors.DARK_AQUA, "Location: ", TextColors.GRAY, Format.location(
            transform.getLocation()))));

    return builder.build();
  }


  public final void setStackIndex(int stackIndex) {
    this.stackIndex = stackIndex;
  }

}
