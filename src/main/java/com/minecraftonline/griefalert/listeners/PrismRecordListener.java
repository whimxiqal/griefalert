package com.minecraftonline.griefalert.listeners;

import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.api.records.PrismRecordPreSaveEvent;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.util.Prism;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.util.Map;

public class PrismRecordListener implements EventListener<PrismRecordPreSaveEvent> {


  @Override
  public void handle(PrismRecordPreSaveEvent event) throws Exception {
    PrismRecord record = event.getPrismRecord();

    // Temporary print statement to see all information within PrismRecord
    printAllData(record);

    // PSEUDO-CODE

    // TODO: Finish handler in PrismRecordListener

    // See if this record matches any GriefProfiles
    // If yes, create an Alert of the appropriate type
    // Add the alert to the AlertQueue
    // Broadcast the Alert's message
  }


  /**
   * Debugging helper method to print all data within a PrismRecord.
   * @param record The PrismRecord.
   */
  private void printAllData(PrismRecord record) {
    Logger l = GriefAlert.getInstance().getLogger();

    l.info("PrismRecord info");
    l.info("----------------");

    Map<DataQuery, Object> dataMap = record.getDataContainer().getValues(true);
    for (DataQuery query : dataMap.keySet()) {
      l.info("{ "
          + query.toString() + ", "
          + "(" + dataMap.get(query).getClass().toString() + ") "
          + dataMap.get(query).toString() + " }");
    }

    Text.Builder parsedBuilder = Text.builder();

    parsedBuilder.append(Text.of("Player: ", Prism.getPlayer(record).map(Player::getName).orElse("")));
    parsedBuilder.append(Text.of("\n"));
    parsedBuilder.append(Text.of("Location: ", Prism.getLocation(record).map(Location::toString).orElse("")));
    parsedBuilder.append(Text.of("\n"));
    parsedBuilder.append(Text.of("Event: ", record.getEvent()));
    parsedBuilder.append(Text.of("\n"));
    parsedBuilder.append(Text.of("Object: ", Prism.getGriefedObjectName(record).orElse("")));

    Sponge.getServer().getBroadcastChannel().send(parsedBuilder.build());

  }
}
