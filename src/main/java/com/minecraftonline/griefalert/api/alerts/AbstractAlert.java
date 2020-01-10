/* Created by PietElite */

package com.minecraftonline.griefalert.api.alerts;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.caches.RotatingAlertList;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.events.PreBroadcastAlertEvent;
import com.minecraftonline.griefalert.api.events.PreCheckAlertEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Communication;
import com.minecraftonline.griefalert.util.Errors;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import com.minecraftonline.griefalert.util.Permissions;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

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
 *
 * @author PietElite
 */
public abstract class AbstractAlert implements Alert {

  private int cacheIndex;
  private final GriefProfile griefProfile;
  private boolean silent = false;
  private boolean pushed = false;

  protected AbstractAlert(GriefProfile griefProfile) {
    this.griefProfile = griefProfile;
  }

  @Nonnull
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

  @Nonnull
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

  @Nonnull
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

  @Nonnull
  @Override
  public final GriefEvent getGriefEvent() {
    return griefProfile.getGriefEvent();
  }

  @Nonnull
  @Override
  public final String getTarget() {
    return griefProfile.getTarget();
  }

  /**
   * Run this <code>Alert</code>. This constitutes pushing this <code>Alert</code>
   * to the {@link RotatingAlertList} followed by
   * broadcasting the <code>ALert</code> to staff members if the <code>Alert</code>
   * is not silent. This can only be done once.
   */
  @Override
  public final void run() {
    push();
    postPreBroadcastAlertEvent();
    broadcast();
  }

  private void push() {

    if (pushed) {
      try {
        throw new IllegalAccessException("Tried to push an alert to the Alert Queue a second time");
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    GriefAlert.getInstance().getRotatingAlertList().push(this);
    pushed = true;

  }

  private void postPreBroadcastAlertEvent() {

    PluginContainer plugin = GriefAlert.getInstance().getPluginContainer();
    EventContext eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, plugin).build();

    PreBroadcastAlertEvent preBroadcastAlertEvent = new PreBroadcastAlertEvent(
        this,
        Cause.of(eventContext, plugin));

    Sponge.getEventManager().post(preBroadcastAlertEvent);

  }

  private void broadcast() {

    if (getGriefer().hasPermission(Permissions.GRIEFALERT_SILENT.toString())) {
      setSilent(true);
    }

    if (!isSilent()) {
      Communication.getStaffBroadcastChannel().send(getTextWithIndex());
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
  public final boolean checkBy(@Nonnull final Player officer) {

    // Post an event to show that the Alert is getting checked
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
    GriefAlert.getInstance().getRotatingAlertList().addOfficerTransform(
        officer.getUniqueId(),
        officerPreviousTransform);

    // Send the messages
    Communication.getStaffBroadcastChannel().send(Format.info(
        Format.playerName(officer),
        " is checking alert number ",
        Format.bonus(clickToCheck(getCacheIndex()))));

    officer.sendMessage(Format.heading("Checking Grief Alert: ",
        Format.bonus(getCacheIndex())));
    officer.sendMessage(getMessageText());
    officer.sendMessage(Text.of(
        Format.command(
            "RECENT",
            String.format(
                "/griefalert query -p %s",
                getGriefer().getName()),
            Text.of("Search for recent events caused by this player.")),
        Format.space(),
        Format.command(
            "SHOW",
            String.format(
                "/griefalert show %s",
                getCacheIndex()),
            Text.of("Search for recent events caused by this player."))));

    return true;

  }

  @Override
  public final int getCacheIndex() {
    return cacheIndex;
  }

  @Nonnull
  @Override
  public final Text getTextWithIndex() {
    return getTextWithIndices(Lists.newArrayList(getCacheIndex()));
  }

  @Nonnull
  @Override
  public final Text getTextWithIndices(@Nonnull List<Integer> allIndices) {
    Text.Builder builder = Text.builder().append(getMessageText());
    allIndices.forEach((i) -> {
      builder.append(Format.space());
      builder.append(clickToCheck(i));
    });
    return builder.build();
  }

  @Override
  public final boolean isRepeatOf(@Nonnull Alert other) {
    return getMessageText().equals(other.getMessageText());
  }

  @Nonnull
  @Override
  public final TextColor getEventColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.EVENT_COLOR).map((s) ->
            Sponge.getRegistry().getType(TextColor.class, s)
                .orElse(Format.ALERT_EVENT_COLOR))
        .orElse(Format.ALERT_EVENT_COLOR);
  }

  @Nonnull
  @Override
  public final TextColor getTargetColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.TARGET_COLOR).map((s) ->
            Sponge.getRegistry().getType(TextColor.class, s)
                .orElse(Format.ALERT_TARGET_COLOR))
        .orElse(Format.ALERT_TARGET_COLOR);
  }

  @Nonnull
  @Override
  public final TextColor getDimensionColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.DIMENSION_COLOR).map((s) ->
            Sponge.getRegistry().getType(TextColor.class, s)
                .orElse(Format.ALERT_DIMENSION_COLOR))
        .orElse(Format.ALERT_DIMENSION_COLOR);
  }

  @Override
  public final void setCacheIndex(int cacheIndex) {
    this.cacheIndex = cacheIndex;
  }

  private Text clickToCheck(int index) {
    return Format.command(String.valueOf(index),
        "/ga check " + index,
        Text.of("Check this alert"));
  }

}
