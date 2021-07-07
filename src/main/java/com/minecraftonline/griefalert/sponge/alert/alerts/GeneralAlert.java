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

package com.minecraftonline.griefalert.sponge.alert.alerts;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.common.alert.alerts.Alert;
import com.minecraftonline.griefalert.common.alert.alerts.Detail;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvent;
import com.minecraftonline.griefalert.common.alert.records.GriefProfile;
import com.minecraftonline.griefalert.sponge.alert.templates.Arg;
import com.minecraftonline.griefalert.sponge.alert.templates.Templates;
import com.minecraftonline.griefalert.sponge.alert.util.Alerts;
import com.minecraftonline.griefalert.sponge.alert.util.Format;
import com.minecraftonline.griefalert.sponge.alert.util.Grammar;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Details;
import com.minecraftonline.griefalert.sponge.alert.util.enums.Settings;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextElement;
import org.spongepowered.api.text.action.TextActions;

/**
 * An object to hold all information about an Alert caused by an
 * event matching a {@link GriefProfile}.
 *
 * @author PietElite
 */
public abstract class GeneralAlert implements Alert {

  private final GriefProfile griefProfile;

  private final List<Detail<Alert>> details = Lists.newLinkedList();
  private final Date created;
  private boolean silent = false;

  protected GeneralAlert(GriefProfile griefProfile) {
    this.griefProfile = griefProfile;
    created = new Date();
    details.add(Details.player());
    details.add(Details.event());
    details.add(Details.target());
    details.add(Details.location());
    details.add(Details.time());
    details.add(Details.inHand());
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

  @Override
  @Nonnull
  public final Text getMessage() {

    Text.Builder ellipses = Text.builder().append(Format.bonus("(...)"));
    Text hoverText = Text.of(Format.prefix(), Format.endLine(), Text.joinWith(
        Format.endLine(),
        details.stream()
            .filter(detail -> !detail.isPrimary())
            .map(detail -> detail.get(this))
            .flatMap(optional -> optional.map(Stream::of).orElseGet(Stream::empty))
            .collect(Collectors.toList())));
    if (!hoverText.toPlain().isEmpty()) {
      ellipses.onHover(TextActions.showText(hoverText));
    }

    return Templates.ALERT.getTextTemplate().apply(
        ImmutableMap.<String, TextElement>builder()
            .put(Arg.GRIEFER.name(),
                Format.userName(Alerts.getGriefer(this)))
            .put(Arg.EVENT_COLOR.name(),
                this.getGriefProfile().getColored(GriefProfile.Colorable.EVENT)
                    .orElse(Format.ALERT_EVENT_COLOR))
            .put(Arg.EVENT.name(),
                Format.action(this.getGriefEvent()))
            .put(Arg.TARGET_COLOR.name(),
                this.getGriefProfile().getColored(GriefProfile.Colorable.TARGET)
                    .orElse(Format.ALERT_TARGET_COLOR))
            .put(Arg.TARGET.name(),
                Grammar.addIndefiniteArticle(Format.item(this.getTarget())))
            .put(Arg.WORLD_COLOR.name(),
                this.getGriefProfile().getColored(GriefProfile.Colorable.WORLD)
                    .orElse(Format.ALERT_WORLD_COLOR))
            .put(Arg.WORLD.name(),
                Settings.DIMENSIONED_ALERTS.getValue()
                    ? Text.of("the ", Format.dimension(Alerts.getWorld(this).getDimension().getType()))
                    : Text.of(Alerts.getWorld(this).getName()))
            .put(Arg.SUFFIX.name(),
                ellipses.build())
            .build()).build();
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

  public List<Detail<Alert>> getDetails() {
    return details;
  }


}
