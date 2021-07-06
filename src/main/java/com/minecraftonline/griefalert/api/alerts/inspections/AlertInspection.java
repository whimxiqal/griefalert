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

package com.minecraftonline.griefalert.api.alerts.inspections;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.util.SpongeUtil;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.world.World;

/**
 * Stores the information the time a player inspected an {@link Alert}.
 *
 * @author PietElite
 */
public final class AlertInspection implements Serializable {

  private final UUID officerUuid;
  private final UUID grieferUuid;
  private final String eventId;
  private final String target;
  private final int posX;
  private final int posY;
  private final int posZ;
  private final UUID worldUuid;
  private final Instant inspected;
  private final UUID previousWorldUuid;
  private final double previousPositionX;
  private final double previousPositionY;
  private final double previousPositionZ;
  private final double previousRotationX;
  private final double previousRotationY;
  private final double previousRotationZ;
  private final int alertIndex;

  private boolean uninspected = false;

  /**
   * Basic constructor for an {@link AlertInspection}
   * corresponding to a specific {@link Alert}.
   *
   * @param officerUuid       the UUID of the officer who checked the Alert
   * @param grieferUuid       the UUID of the griefer during the Alert
   * @param eventId           the id of the event
   * @param target            the minecraft target
   * @param blockPosition     the block position in the world
   * @param worldUuid         the UUID of the world
   * @param inspected         the time of the inspection
   * @param previousTransform the transform of the officer before the inspection
   * @param alertIndex        the index of the Alert
   */
  public AlertInspection(@NotNull final UUID officerUuid,
                         @NotNull final UUID grieferUuid,
                         @NotNull final String eventId,
                         @NotNull final String target,
                         @NotNull final Vector3i blockPosition,
                         @NotNull final UUID worldUuid,
                         @NotNull final Instant inspected,
                         @NotNull final Transform<World> previousTransform,
                         final int alertIndex) {
    this.officerUuid = officerUuid;
    this.grieferUuid = grieferUuid;
    this.eventId = eventId;
    this.target = target;
    this.posX = blockPosition.getX();
    this.posY = blockPosition.getY();
    this.posZ = blockPosition.getZ();
    this.worldUuid = worldUuid;
    this.inspected = inspected;
    this.previousWorldUuid = previousTransform.getExtent().getUniqueId();
    this.previousPositionX = previousTransform.getPosition().getX();
    this.previousPositionY = previousTransform.getPosition().getY();
    this.previousPositionZ = previousTransform.getPosition().getZ();
    this.previousRotationX = previousTransform.getRotation().getX();
    this.previousRotationY = previousTransform.getRotation().getY();
    this.previousRotationZ = previousTransform.getRotation().getZ();
    this.alertIndex = alertIndex;
  }

  @NotNull
  public UUID getOfficerUuid() {
    return officerUuid;
  }

  @NotNull
  public UUID getGrieferUuid() {
    return grieferUuid;
  }

  @NotNull
  public String getEventId() {
    return eventId;
  }

  @NotNull
  public String getTarget() {
    return target;
  }

  public int getX() {
    return posX;
  }

  public int getY() {
    return posY;
  }

  public int getZ() {
    return posZ;
  }

  @NotNull
  public UUID getWorldUuid() {
    return worldUuid;
  }

  @NotNull
  public Instant getInspected() {
    return inspected;
  }

  /**
   * Get the transform for the player before the inspection.
   *
   * @return the previous transform
   */
  @NotNull
  public Transform<World> getPreviousTransform() {
    return new Transform<>(
        SpongeUtil.getWorld(previousWorldUuid)
            .orElseThrow(() -> new RuntimeException("AlertInspection stored an invalid previous World Uuid: "
                + previousWorldUuid)),
        new Vector3d(previousPositionX, previousPositionY, previousPositionZ),
        new Vector3d(previousRotationX, previousRotationY, previousRotationZ));
  }

  public int getAlertIndex() {
    return alertIndex;
  }

  public void uninspect() {
    uninspected = true;
  }

  public boolean isUninspected() {
    return uninspected;
  }

}
