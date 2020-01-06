/* Created by PietElite */

package com.minecraftonline.griefalert.alerts;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.events.PreCheckAlertEvent;
import com.minecraftonline.griefalert.events.PreRunAlertEvent;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Comms;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import com.minecraftonline.griefalert.util.Permissions;

import java.util.List;
import java.util.Optional;

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
public abstract class AbstractAlert implements Alert {


  // FIELDS

  private int stackIndex;
  private final GriefProfile griefProfile;
  private boolean silent = false;
  private boolean pushed = false;


  // PROTECTED CONSTRUCTOR

  protected AbstractAlert(GriefProfile griefProfile) {
    this.griefProfile = griefProfile;
  }


  // PUBLIC MEMBER METHODS

  @Override
  public Text getMessageText() {
    Text.Builder builder = Text.builder();
    builder.append(Text.of(
        Format.playerName(getGriefer()),
        Format.space(),
        getEventColor(), getGriefEvent().getPreterite(),
        Format.space(),
        getTargetColor(), Grammar.addIndefiniteArticle(getTarget().replace("minecraft:", "")),
        TextColors.RED, " in the ",
        getDimensionColor(), getGrieferTransform().getExtent().getDimension().getType().getName()));
    return builder.build();
  }

  @Override
  public Text getSummary() {
    Text.Builder builder = Text.builder();

    builder.append(Text.of(
        TextColors.DARK_AQUA, "Player: ",
        TextColors.GRAY, Format.playerName(getGriefer())), Format.endLine());
    builder.append(Text.of(
        TextColors.DARK_AQUA, "Event: ",
        TextColors.GRAY, griefProfile.getGriefEvent().getId()), Format.endLine());
    builder.append(Text.of(
        TextColors.DARK_AQUA, "Target: ",
        TextColors.GRAY, griefProfile.getTarget().replace("minecraft:", "")), Format.endLine());
    getExtraSummaryContent().ifPresent((content) ->
        builder.append(Text.of(
            TextColors.DARK_AQUA, "Extra: ",
            TextColors.GRAY, content, Format.endLine()))
    );
    builder.append(Text.of(
        TextColors.DARK_AQUA, "Location: ",
        TextColors.GRAY, Format.location(
            getGrieferTransform().getLocation())));

    return builder.build();
  }

  @Override
  public Optional<String> getExtraSummaryContent() {
    return Optional.empty();
  }

  @Override
  public boolean isSilent() {
    return silent;
  }

  @Override
  public void setSilent(boolean silent) {
    this.silent = silent;
  }


  // FINAL PUBLIC METHODS

  @Override
  public final GriefEvent getGriefEvent() {
    return griefProfile.getGriefEvent();
  }

  @Override
  public final String getTarget() {
    return griefProfile.getTarget();
  }

  /**
   * Run this <code>Alert</code>. This constitutes pushing this <code>Alert</code>
   * to the {@link com.minecraftonline.griefalert.alerts.AlertStack} followed by
   * broadcasting the <code>ALert</code> to staff members if the <code>Alert</code>
   * is not silent. This can only be done once.
   */
  @Override
  public final void run() {

    if (pushed) {
      try {
        throw new IllegalAccessException("Tried to push an alert to the Alert Queue a second time");
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    PluginContainer plugin = GriefAlert.getInstance().getPluginContainer();
    EventContext eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, plugin).build();

    PreRunAlertEvent preRunAlertEvent = new PreRunAlertEvent(this, Cause.of(eventContext, plugin));

    Sponge.getEventManager().post(preRunAlertEvent);

    GriefAlert.getInstance().getAlertQueue().push(this);
    pushed = true;

    if (getGriefer().hasPermission(Permissions.GRIEFALERT_SILENT.toString())) {
      setSilent(true);
    }

    if (!isSilent()) {
      Comms.getStaffBroadcastChannel().send(getTextWithIndex());
    }

  }

  /**
   * Check the <code>Alert</code> with this <code>Player</code>. Before
   * teleporting the <code>Player</code> and sending general <code>Alert</code>
   * information, a {@link PreCheckAlertEvent} is run.
   *
   * @param officer The <code>Player</code> to do the check
   * @return true if the officer was teleported to the location
   */
  @Override
  public final boolean checkBy(Player officer) {

    PluginContainer plugin = GriefAlert.getInstance().getPluginContainer();
    EventContext eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, plugin).build();

    PreCheckAlertEvent preCheckAlertEvent = new PreCheckAlertEvent(
        this,
        Cause.of(eventContext, plugin), officer);

    Sponge.getEventManager().post(preCheckAlertEvent);

    // Save the officer's previous transform and add it into the alert's database later
    // if the officer successfully teleports.
    Transform<World> officerPreviousTransform = officer.getTransform();

    // CheckEvent

    // Teleport the officer
    if (!officer.setTransformSafely(getGrieferTransform())) {
      Errors.sendCannotTeleportSafely(officer, getGrieferTransform());
      return false;
    }

    // The officer has teleported successfully, so save their previous location in the history
    GriefAlert.getInstance().getAlertQueue().addOfficerTransform(
        officer.getUniqueId(),
        officerPreviousTransform);

    officer.sendMessage(Format.heading("Checking Grief Alert:"));
    officer.sendMessage(getSummary());
    Comms.getStaffBroadcastChannel().send(Format.info(
        Format.playerName(officer),
        " is checking alert number ",
        Format.bonus(clickToCheck(getStackIndex()))));
    return true;
  }

  @Override
  public final int getStackIndex() {
    return stackIndex;
  }

  @Override
  public final Text getTextWithIndex() {
    return getTextWithIndices(Lists.newArrayList(getStackIndex()));
  }

  @Override
  public final Text getTextWithIndices(List<Integer> allIndices) {
    Text.Builder builder = Text.builder().append(getMessageText());
    allIndices.forEach((i) -> {
      builder.append(Format.space());
      builder.append(clickToCheck(i));
    });
    return builder.build();
  }

  @Override
  public final boolean isRepeatOf(Alert other) {
    return getMessageText().equals(other.getMessageText());
  }


  // FINAL PROTECTED METHODS

  @Override
  public final TextColor getEventColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.EVENT_COLOR).map((s) ->
            Sponge.getRegistry().getType(TextColor.class, s)
                .orElse(Format.ALERT_EVENT_COLOR))
        .orElse(Format.ALERT_EVENT_COLOR);
  }

  @Override
  public final TextColor getTargetColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.TARGET_COLOR).map((s) ->
            Sponge.getRegistry().getType(TextColor.class, s)
                .orElse(Format.ALERT_TARGET_COLOR))
        .orElse(Format.ALERT_TARGET_COLOR);
  }

  @Override
  public final TextColor getDimensionColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.DIMENSION_COLOR).map((s) ->
            Sponge.getRegistry().getType(TextColor.class, s)
                .orElse(Format.ALERT_DIMENSION_COLOR))
        .orElse(Format.ALERT_DIMENSION_COLOR);
  }

  @Override
  public final void setStackIndex(int stackIndex) {
    this.stackIndex = stackIndex;
  }


  // PRIVATE METHODS

  private Text clickToCheck(int index) {
    return Format.command(String.valueOf(index),
        "/ga check " + index,
        GriefAlert.getInstance().getAlertQueue().get(index).getSummary());
  }

}
