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

package com.minecraftonline.griefalert.listeners;

import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.api.records.PrismRecordPreSaveEvent;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.alerts.prism.BreakAlert;
import com.minecraftonline.griefalert.alerts.prism.DeathAlert;
import com.minecraftonline.griefalert.alerts.prism.PlaceAlert;
import com.minecraftonline.griefalert.alerts.prism.PrismAlert;
import com.minecraftonline.griefalert.alerts.prism.ReplaceAlert;
import com.minecraftonline.griefalert.alerts.prism.SignBreakAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.PrismUtil;
import com.minecraftonline.griefalert.util.enums.GriefEvents;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.DimensionType;

public class PrismRecordListener implements EventListener<PrismRecordPreSaveEvent> {


  @Override
  public void handle(PrismRecordPreSaveEvent event) {
    PrismRecord record = event.getPrismRecord();
    DataContainer container = event.getPrismRecord().getDataContainer();

    // See if this record matches any GriefProfiles
    if (!Sponge.getRegistry().getType(GriefEvent.class, record.getEvent()).isPresent()) {
      return;
    }

    // Replace spaces with underscores because for some reason PrismUtil removes them...
    Optional<String> targetOptional = PrismUtil.getTarget(container).map(General::ensureIdFormat);
    if (!targetOptional.isPresent()) {
      return;
    }

    Optional<DimensionType> dimensionTypeOptional = PrismUtil.getLocation(container)
        .map((location) -> location.getExtent().getDimension().getType());
    if (!dimensionTypeOptional.isPresent()) {
      return;
    }

    Optional<String> playerUuidOptional = PrismUtil.getPlayerUuid(container);
    if (!playerUuidOptional.isPresent()) {
      return;
    }

    Optional<GriefProfile> profileOptional = GriefAlert.getInstance()
        .getProfileCache()
        .getProfileOf(
            Sponge.getRegistry().getType(GriefEvent.class, record.getEvent()).get(),
            targetOptional.get(),
            dimensionTypeOptional.get()
        );

    // If yes, create an Alert of the appropriate type
    profileOptional.ifPresent((profile) -> {
      GriefAlert.getInstance().getLogger().debug("PrismEvent matched a GriefProfile");

      Alert alert;
      if (profile.getGriefEvent().equals(GriefEvents.BREAK)) {
        if (targetOptional.get().contains("sign")) {
          alert = new SignBreakAlert(profile, record);
        } else {
          alert = new BreakAlert(profile, record);
        }
      } else if (profile.getGriefEvent().equals(GriefEvents.PLACE)) {
        alert = new PlaceAlert(profile, record);
      } else if (profile.getGriefEvent().equals(GriefEvents.DEATH)) {
        alert = new DeathAlert(profile, record);
      } else {
        GriefAlert.getInstance().getLogger().error("A PrismRecord matched a Grief Profile but "
            + "an Alert could not be made.");
        GriefAlert.getInstance().getLogger().error(PrismUtil.printRecord(container));
        GriefAlert.getInstance().getLogger().error(profile.print().toPlain());
        return;
      }

      Task.builder()
          .execute(() -> GriefAlert.getInstance().getAlertService().submit(alert))
          .submit(GriefAlert.getInstance());
    });

    // Check for replacement alerts
    if (Sponge.getRegistry().getType(GriefEvent.class,
        record.getEvent()).get().equals(GriefEvents.PLACE)) {

      Optional<String> originalBlockId = PrismUtil.getOriginalBlockState(container).map(
          (state) -> state.getType().getId());

      if (!originalBlockId.isPresent()) {
        GriefAlert.getInstance().getLogger().info("No original block present");
        return;
      }

      Optional<GriefProfile> replaceOptional = GriefAlert.getInstance().getProfileCache()
          .getProfileOf(
              GriefEvents.REPLACE,
              originalBlockId.get(),
              dimensionTypeOptional.get());

      Optional<String> replacementBlockId = PrismUtil.getReplacementBlock(container)
          .map((state) -> state.getType().getId());

      replaceOptional.ifPresent((profile) -> {

        PrismAlert alert = new ReplaceAlert(
            profile,
            record,
            replacementBlockId.orElse("unknown block"));

        Task.builder()
            .execute(() -> GriefAlert.getInstance().getAlertService().submit(alert))
            .submit(GriefAlert.getInstance());
      });
    }
  }

}
