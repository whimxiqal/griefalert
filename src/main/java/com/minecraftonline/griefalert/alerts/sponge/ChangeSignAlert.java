/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.sponge;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.templates.Arg;
import com.minecraftonline.griefalert.api.templates.Templates;
import com.minecraftonline.griefalert.util.Alerts;
import com.minecraftonline.griefalert.util.Format;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import com.minecraftonline.griefalert.util.Grammar;
import com.minecraftonline.griefalert.util.enums.Settings;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextElement;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public class ChangeSignAlert implements Alert {

  private final GriefProfile griefProfile;
  private final UUID grieferUuid;
  private final Vector3d grieferPosition;
  private final Vector3d grieferRotation;
  private final UUID worldUuid;
  private final Vector3i griefPosition;
  private final Date created;
  private final List<String> stringLines;
  private final String target;
  private boolean silent;

  /**
   * An alert to throw when someone throws a {@link ChangeSignEvent}.
   *
   * @param griefProfile the profile flagging this event
   * @param event the event triggering this alert
   */
  public ChangeSignAlert(GriefProfile griefProfile, ChangeSignEvent event) {
    this.griefProfile = griefProfile;
    Player griefer = event.getCause().first(Player.class).orElseThrow(() ->
        new RuntimeException("Could not find player in ChangeSignEvent"));
    this.grieferUuid = griefer.getUniqueId();
    Transform<World> grieferTransform = griefer.getTransform();
    this.grieferPosition = grieferTransform.getPosition();
    this.grieferRotation = grieferTransform.getRotation();
    this.worldUuid = grieferTransform.getExtent().getUniqueId();
    this.griefPosition = event.getTargetTile().getLocation().getBlockPosition();
    this.created = new Date();
    this.stringLines = event.getText().lines().get().stream().map(text -> {
      try {
        return DataFormats.JSON.write(text.toContainer());
      } catch (IOException e) {
        e.printStackTrace();
        return "";
      }
    }).collect(Collectors.toList());
    this.target = event.getTargetTile().getBlock().getType().getId();
    this.silent = false;
  }

  @Nonnull
  @Override
  public GriefProfile getGriefProfile() {
    return griefProfile;
  }

  @Nonnull
  @Override
  public UUID getGrieferUuid() {
    return grieferUuid;
  }

  @Nonnull
  @Override
  public Vector3d getGrieferPosition() {
    return grieferPosition;
  }

  @Nonnull
  @Override
  public Vector3d getGrieferRotation() {
    return grieferRotation;
  }

  @Nonnull
  @Override
  public Vector3i getGriefPosition() {
    return griefPosition;
  }

  @Nonnull
  @Override
  public UUID getWorldUuid() {
    return worldUuid;
  }

  @Nonnull
  @Override
  public Date getCreated() {
    return created;
  }

  @Nonnull
  @Override
  public Text getMessage() {

    List<Text> signLines = formatLines().stream().map(alertDetail ->
        alertDetail.get(this).get()).collect(Collectors.toList());
    Text signContent;
    if (!signLines.isEmpty()) {
      signContent = Text.joinWith(
          Format.endLine(),
          formatLines().stream()
              .map(alertDetail -> alertDetail.get(this).get())
              .collect(Collectors.toList()));
    } else {
      signContent = Format.bonus("Empty");
    }
    Text ellipses = Text.builder().append(Format.bonus("(...)"))
        .onHover(TextActions.showText(Text.of(
            Format.prefix(),
            Format.endLine(),
            signContent)))
        .build();

    return Templates.ALERT.getTextTemplate().apply(
        ImmutableMap.<String, TextElement>builder()
            .put(Arg.GRIEFER.name(),
                Format.userName(Alerts.getGriefer(this)))
            .put(Arg.EVENT_COLOR.name(),
                this.getGriefProfile().getColored(GriefProfile.Colorable.EVENT)
                    .orElse(Format.ALERT_EVENT_COLOR))
            .put(Arg.EVENT.name(),
                Format.action(this.getGriefEvent(), "applied changes to"))
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
                ellipses)
            .build()).build();
  }

  @Nonnull
  @Override
  public Text getSummary() {
    return Text.joinWith(
        Format.endLine(),
        formatLines().stream()
            .map(alertDetail -> alertDetail.get(this).get())
            .collect(Collectors.toList()));
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
  public GriefEvent getGriefEvent() {
    return griefProfile.getGriefEvent();
  }

  @Nonnull
  @Override
  public String getTarget() {
    return target;
  }

  private List<Detail<Alert>> formatLines() {
    List<Text> lines = stringLines.stream().map(str -> {
      try {
        return Sponge.getDataManager().deserialize(Text.class, DataFormats.JSON.read(str)).get();
      } catch (IOException | NoSuchElementException e) {
        e.printStackTrace();
        return Text.EMPTY;
      }
    }).collect(Collectors.toList());
    List<Detail<Alert>> details = Lists.newLinkedList();
    for (int i = 0; i < lines.size(); i++) {
      if (lines.get(i).isEmpty()) {
        continue;
      }

      details.add(Detail.of(
          String.format("Line %d", i + 1),
          String.format("The %d'th line on the sign after it was edited", i + 1),
          lines.get(i),
          true));
    }
    return details;
  }
}
