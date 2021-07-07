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

package com.minecraftonline.griefalert.sponge.alert.listeners;

import com.minecraftonline.griefalert.SpongeGriefAlert;
import com.minecraftonline.griefalert.common.data.records.PrismRecord;
import com.minecraftonline.griefalert.common.data.records.PrismRecordPreSaveEvent;
import com.minecraftonline.griefalert.sponge.alert.alerts.prism.BreakAlert;
import com.minecraftonline.griefalert.sponge.alert.alerts.prism.DeathAlert;
import com.minecraftonline.griefalert.sponge.alert.alerts.prism.PlaceAlert;
import com.minecraftonline.griefalert.sponge.alert.alerts.prism.PrismAlert;
import com.minecraftonline.griefalert.sponge.alert.alerts.prism.ReplaceAlert;
import com.minecraftonline.griefalert.sponge.alert.alerts.prism.SignBreakAlert;
import com.minecraftonline.griefalert.common.alert.alerts.Alert;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvent;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvents;
import com.minecraftonline.griefalert.common.alert.records.GriefProfile;
import com.minecraftonline.griefalert.sponge.alert.util.Format;
import com.minecraftonline.griefalert.sponge.alert.util.General;
import com.minecraftonline.griefalert.sponge.alert.util.PrismUtil;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * A listener for Prism's event that is fired before a record is saved.
 *
 * @author PietElite
 */
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

    Optional<World> worldOptional = PrismUtil.getLocation(container)
        .map(Location::getExtent);
    if (!worldOptional.isPresent()) {
      return;
    }

    Optional<String> playerUuidOptional = PrismUtil.getPlayerUuid(container);
    if (!playerUuidOptional.isPresent()) {
      return;
    }

    Optional<GriefProfile> profileOptional = SpongeGriefAlert.getSpongeInstance()
        .getProfileCache()
        .getProfileOf(
            Sponge.getRegistry().getType(GriefEvent.class, record.getEvent()).get(),
            targetOptional.get(),
            worldOptional.get()
        );

    // If yes, create an Alert of the appropriate type
    profileOptional.ifPresent((profile) -> {
      SpongeGriefAlert.getSpongeInstance().getLogger().debug("PrismEvent matched a GriefProfile");

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
        SpongeGriefAlert.getSpongeInstance().getLogger().error("A PrismRecord matched a Grief Profile but "
            + "an Alert could not be made.");
        SpongeGriefAlert.getSpongeInstance().getLogger().error(PrismUtil.printRecord(container));
        SpongeGriefAlert.getSpongeInstance().getLogger().error(Format.profile(profile).toPlain());
        return;
      }

      Task.builder()
          .execute(() -> SpongeGriefAlert.getSpongeInstance().getAlertService().submit(alert))
          .submit(SpongeGriefAlert.getSpongeInstance());
    });

    // Check for replacement alerts
    if (Sponge.getRegistry().getType(GriefEvent.class,
        record.getEvent()).get().equals(GriefEvents.PLACE)) {

      Optional<String> originalBlockId = PrismUtil.getOriginalBlockState(container).map(
          (state) -> state.getType().getId());

      if (!originalBlockId.isPresent()) {
        SpongeGriefAlert.getSpongeInstance().getLogger().info("No original block present");
        return;
      }

      Optional<GriefProfile> replaceOptional = SpongeGriefAlert.getSpongeInstance().getProfileCache()
          .getProfileOf(
              GriefEvents.TRANSFORM,
              originalBlockId.get(),
              worldOptional.get());

      Optional<String> replacementBlockId = PrismUtil.getReplacementBlock(container)
          .map((state) -> state.getType().getId());

      replaceOptional.ifPresent((profile) -> {

        PrismAlert alert = new ReplaceAlert(
            profile,
            record,
            replacementBlockId.orElse("unknown block"));

        Task.builder()
            .execute(() -> SpongeGriefAlert.getSpongeInstance().getAlertService().submit(alert))
            .submit(SpongeGriefAlert.getSpongeInstance());
      });
    }
  }

}
