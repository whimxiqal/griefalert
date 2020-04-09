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

package com.minecraftonline.griefalert.api.alerts;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.events.PreBroadcastAlertEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.structures.RotatingList;
import com.minecraftonline.griefalert.commands.CheckCommand;
import com.minecraftonline.griefalert.util.Communication;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.enums.Details;
import com.minecraftonline.griefalert.util.enums.Permissions;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
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
public abstract class GeneralAlert implements Alert {

  private int cacheIndex;
  private final GriefProfile griefProfile;

  private final List<Detail<Alert>> details =
      new LinkedList<>();
  private boolean silent = false;
  private boolean pushed = false;
  private final Date created;
  private final List<AlertCheck> checks = Lists.newLinkedList();

  protected GeneralAlert(GriefProfile griefProfile) {
    this.griefProfile = griefProfile;
    created = new Date();
    details.add(Details.PLAYER);
    details.add(Details.EVENT);
    details.add(Details.TARGET);
    details.add(Details.LOCATION);
    details.add(Details.TIME);
    details.add(Details.IN_HAND);
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
        Format.userName(getGriefer()),
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
    Text hoverText = Text.of(Format.prefix(), Format.endLine(), Text.joinWith(
        Format.endLine(),
        details.stream()
            .filter(detail -> !detail.isPrimary())
            .map(detail -> detail.get(this))
            .flatMap(optional -> optional.map(Stream::of).orElseGet(Stream::empty))
            .collect(Collectors.toList())));
    if (!hoverText.toPlain().isEmpty()) {
      builder.onHover(TextActions.showText(hoverText));
    }
    return builder.build();
  }

  @Nonnull
  @Override
  public final Text getSummary() {
    return Text.joinWith(
        Format.endLine(),
        details.stream()
            .map(detail -> detail.get(this))
            .flatMap(optional -> optional.map(Stream::of).orElseGet(Stream::empty))
            .collect(Collectors.toList()));
  }

  protected void addDetail(Detail<Alert> detail) {
    this.details.add(detail);
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
   * Run this {@link Alert}. This constitutes pushing this <code>Alert</code>
   * to the {@link RotatingList} followed by
   * broadcasting the <code>Alert</code> to staff members if the <code>Alert</code>
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
      builder.append(CheckCommand.clickToCheck(i));
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
    return griefProfile.getColored(GriefProfile.Colored.EVENT)
        .orElse(Format.ALERT_EVENT_COLOR);
  }

  @Nonnull
  @Override
  public final TextColor getTargetColor() {
    return griefProfile.getColored(GriefProfile.Colored.TARGET)
        .orElse(Format.ALERT_TARGET_COLOR);
  }

  @Nonnull
  @Override
  public final TextColor getDimensionColor() {
    return griefProfile.getColored(GriefProfile.Colored.DIMENSION)
        .orElse(Format.ALERT_DIMENSION_COLOR);
  }

  @Override
  public final void setCacheIndex(int cacheIndex) {
    this.cacheIndex = cacheIndex;
  }

  public List<Detail<Alert>> getDetails() {
    return details;
  }

  @Nonnull
  @Override
  public List<AlertCheck> getChecks() {
    return checks;
  }

  @Override
  public void addCheck(@Nonnull AlertCheck check) {
    checks.add(check);
  }
}
