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

package com.minecraftonline.griefalert.alerts.prism;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.helion3.prism.api.data.PrismEvent;
import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.api.services.PrismService;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.alerts.GeneralAlert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.PrismUtil;

import java.io.IOException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


/**
 * <code>Alert</code> for all which derive from Prism.
 */
public abstract class PrismAlert extends GeneralAlert {

  private final Vector3d grieferPosition;
  private final Vector3d grieferRotation;
  private final UUID worldUuid;
  private final Vector3i griefPosition;
  private final UUID grieferUuid;
  private final Date created;
  private final String originalBlockState;
  private final String replacementBlockState;

  PrismAlert(GriefProfile griefProfile, PrismRecord prismRecord) {
    super(griefProfile);
    String recordString;
    // Immediately set the transform of the griefer upon triggering the Alert


    try {
      this.grieferUuid = PrismUtil.getPlayerUuid(prismRecord.getDataContainer()).map(UUID::fromString).get();
      Location<World> griefLocation = PrismUtil.getLocation(prismRecord.getDataContainer()).get();
      this.worldUuid = griefLocation.getExtent().getUniqueId();
      this.griefPosition = griefLocation.getBlockPosition();
      this.created = PrismUtil.getCreated(prismRecord.getDataContainer()).get();
      this.originalBlockState = DataFormats.JSON.write(PrismUtil.getOriginalBlockState(prismRecord.getDataContainer()).get().toContainer());
      this.replacementBlockState = DataFormats.JSON.write(PrismUtil.getReplacementBlock(prismRecord.getDataContainer()).get().toContainer());
    } catch (NoSuchElementException e) {
      throw new IllegalArgumentException("Prism did not contain necessary information");
    } catch (IOException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Prism information could not be handled");
    }

    Transform<World> grieferTransform = Sponge.getServer()
        .getPlayer(getGrieferUuid())
        .map(Entity::getTransform)
        .orElseThrow(RuntimeException::new);
    this.grieferPosition = grieferTransform.getPosition();
    this.grieferRotation = grieferTransform.getRotation();

    // And block details
    getOriginalBlockState().map(BlockState::getTraitMap).ifPresent(map ->
        map.forEach((key, value) -> addDetail(Detail.of(
            "(Original) " + General.capitalize(key.getName()),
            "The " + key.getName() + " trait of the original block of this transaction.",
            Text.of(value.toString())))));
    getReplacementBlockState().map(BlockState::getTraitMap).ifPresent(map ->
        map.forEach((key, value) -> addDetail(Detail.of(
            "(New) " + General.capitalize(key.getName()),
            "The " + key.getName() + " trait of the newly created block of this transaction.",
            Text.of(value.toString())))));
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
  public UUID getGrieferUuid() {
    return grieferUuid;
  }

  @Nonnull
  @Override
  public Date getCreated() {
    return created;
  }

  public Optional<BlockState> getOriginalBlockState() {
    try {
      return Sponge.getDataManager().deserialize(BlockState.class, DataFormats.JSON.read(originalBlockState));
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  public Optional<BlockState> getReplacementBlockState() {
    try {
      return Sponge.getDataManager().deserialize(BlockState.class, DataFormats.JSON.read(replacementBlockState));
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  protected PrismService.Request getRollbackRequest() {
    PrismService.Request.Builder builder = PrismService.requestBuilder();

    builder.addPlayerUuid(getGrieferUuid());
    builder.addTarget(getTarget());
    builder.setEarliest(Date.from(getCreated().toInstant().minusSeconds(1)));
    builder.setLatest(Date.from(getCreated().toInstant().plusSeconds(1)));
    builder.addEvent(Sponge.getRegistry().getType(PrismEvent.class, getGriefEvent().getId()).orElseThrow(() ->
            new RuntimeException("PrismAlert stored an invalid GriefEvent: " + getGriefEvent().getId())));
    builder.addWorldUuid(getWorldUuid());
    builder.setxRange(getGriefPosition().getX(), getGriefPosition().getX());
    builder.setyRange(getGriefPosition().getY(), getGriefPosition().getY());
    builder.setzRange(getGriefPosition().getZ(), getGriefPosition().getZ());

    return builder.build();
  }

  /**
   * Rollback the event which caused this alert.
   *
   * @param src the source of the rollback request
   * @return whether rollback was successful
   */
  public final boolean rollback(@Nonnull CommandSource src) {
    try {
      GriefAlert.getInstance().getPrismService().rollback(src, getRollbackRequest());
      return true;
    } catch (Exception e) {
      GriefAlert.getInstance().getLogger().error("Rollback with PrismService failed");
      e.printStackTrace();
      return false;
    }
  }

//  protected void addQueryConditionsTo(Query query) {
//    query.addCondition(FieldCondition.of(
//        DataQueries.Player,
//        MatchRule.EQUALS,
//        this.getGrieferUuid().toString()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Target,
//        MatchRule.EQUALS,
//        Pattern.compile(this.getTarget().replace('_', ' '))));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Created,
//        MatchRule.GREATER_THAN_EQUAL,
//        Date.from(this.getCreated().toInstant().minusSeconds(1))));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Created,
//        MatchRule.LESS_THAN_EQUAL,
//        Date.from(this.getCreated().toInstant().plusSeconds(1))));
//    query.addCondition(FieldCondition.of(
//        DataQueries.EventName,
//        MatchRule.EQUALS,
//        this.getGriefEvent().getId()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Location.then(DataQueries.WorldUuid),
//        MatchRule.EQUALS,
//        this.getGriefLocation().getExtent().getUniqueId().toString()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Location.then(DataQueries.X),
//        MatchRule.EQUALS,
//        this.getGriefLocation().getPosition().getFloorX()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Location.then(DataQueries.Y),
//        MatchRule.EQUALS,
//        this.getGriefLocation().getPosition().getFloorY()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Location.then(DataQueries.Z),
//        MatchRule.EQUALS,
//        this.getGriefLocation().getPosition().getFloorZ()));
//  }

}
