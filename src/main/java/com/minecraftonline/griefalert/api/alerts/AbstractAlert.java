/* Created by PietElite */

package com.minecraftonline.griefalert.api.alerts;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.events.PreBroadcastAlertEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.structures.RotatingList;
import com.minecraftonline.griefalert.commands.GriefAlertCheckCommand;
import com.minecraftonline.griefalert.util.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.minecraftonline.griefalert.util.enums.Details;
import com.minecraftonline.griefalert.util.enums.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;


/**
 * An object to hold all information about an Alert caused by an
 * event matching a {@link GriefProfile}.
 *
 * @author PietElite
 */
public abstract class AbstractAlert implements Alert {

  private int cacheIndex;
  private final GriefProfile griefProfile;

  private final List<Detail> primaryDetails =
      new LinkedList<>();
  private final List<Detail> secondaryDetails =
      new LinkedList<>();
  private boolean silent = false;
  private boolean pushed = false;
  private final Date created;

  protected AbstractAlert(GriefProfile griefProfile) {
    this.griefProfile = griefProfile;
    created = new Date();
    primaryDetails.add(Details.PLAYER);
    primaryDetails.add(Details.EVENT);
    primaryDetails.add(Details.TARGET);
    primaryDetails.add(Details.LOCATION);
    secondaryDetails.add(Details.TIME);
  }

  @Nonnull
  @Override
  public final GriefProfile getGriefProfile() {
    return griefProfile;
  }

  @Nonnull
  @Override
  public Date getCreated() {
    return created;
  }

  @Nonnull
  protected Text.Builder getMessageTextBuilder() {
    Text.Builder builder = Text.builder();
    builder.append(Text.of(
        Format.playerName(getGriefer()),
        Format.space(),
        getEventColor(), getGriefEvent().getPreterite(),
        Format.space(),
        getTargetColor(), Grammar.addIndefiniteArticle(Format.item(getTarget())),
        TextColors.RED, " in the ",
        getDimensionColor(), getGrieferTransform().getExtent().getDimension().getType().getName()));
    return builder;
  }

  @Override
  @Nonnull
  public final Text getMessageText() {
    Text.Builder builder = getMessageTextBuilder();
    Text hoverText = Text.joinWith(
        Format.endLine(),
        secondaryDetails.stream()
            .map(detail -> detail.get(this))
            .flatMap(optional -> optional.map(Stream::of).orElseGet(Stream::empty))
            .collect(Collectors.toList()));
    if (!hoverText.toPlain().isEmpty()) {
      builder.onHover(TextActions.showText(hoverText));
    }
    return builder.build();
  }

  @Nonnull
  @Override
  public final Text getSummary() {
    List<Detail> allDetails = new LinkedList<>();
    allDetails.addAll(primaryDetails);
    allDetails.addAll(secondaryDetails);
    return Text.joinWith(
        Format.endLine(),
        allDetails.stream()
            .map(detail -> detail.get(this))
            .flatMap(optional -> optional.map(Stream::of).orElseGet(Stream::empty))
            .collect(Collectors.toList()));
  }

  protected void addDetail(Detail detail) {
    this.secondaryDetails.add(detail);
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
   * to the {@link RotatingList} followed by
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

    GriefAlert.getInstance().getAlertManager().push(this);
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

  @Override
  public int getCacheIndex() {
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
      builder.append(GriefAlertCheckCommand.clickToCheck(i));
    });
    return builder.build();
  }

  @Override
  public final boolean isRepeatOf(@Nonnull Alert other) {
    return getMessageText().toPlain().equals(other.getMessageText().toPlain());
  }

  @Nonnull
  @Override
  public final TextColor getEventColor() {
    return griefProfile.toContainer()
        .getString(GriefProfileDataQueries.EVENT_COLOR).map((s) ->
            Sponge.getRegistry().getType(TextColor.class, s)
                .orElse(Format.ALERT_EVENT_COLOR))
        .orElse(Format.ALERT_EVENT_COLOR);
  }

  @Nonnull
  @Override
  public final TextColor getTargetColor() {
    return griefProfile.toContainer()
        .getString(GriefProfileDataQueries.TARGET_COLOR).map((s) ->
            Sponge.getRegistry().getType(TextColor.class, s)
                .orElse(Format.ALERT_TARGET_COLOR))
        .orElse(Format.ALERT_TARGET_COLOR);
  }

  @Nonnull
  @Override
  public final TextColor getDimensionColor() {
    return griefProfile.toContainer()
        .getString(GriefProfileDataQueries.DIMENSION_COLOR).map((s) ->
            Sponge.getRegistry().getType(TextColor.class, s)
                .orElse(Format.ALERT_DIMENSION_COLOR))
        .orElse(Format.ALERT_DIMENSION_COLOR);
  }

  @Override
  public final void setCacheIndex(int cacheIndex) {
    this.cacheIndex = cacheIndex;
  }


}
