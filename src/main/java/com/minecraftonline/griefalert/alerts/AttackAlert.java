package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.*;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Queries;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.TargetEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class AttackAlert extends SpongeAlert {

  private AttackAlert(GriefProfile griefProfile, InteractEntityEvent.Primary event) {
    super(griefProfile, event);
  }

  public static AttackAlert of(GriefProfile griefProfile, InteractEntityEvent.Primary event) {
    switch (griefProfile.getTarget()) {
      case "minecraft:item_frame":
        return new AttackAlert(griefProfile, event) {
          @Override
          public Text getMessageText() {
            Text.Builder builder = Text.builder();
            builder.append(Text.of(
                General.formatPlayerName(getGriefer()),
                Format.space(),
                getEventColor(), "attacked",
                Format.space(),
                getTargetColor(), "an item_frame",
                Format.space(),
                TextColors.RED,
                String.format(
                    "(%s)",
                    Hanging.getItemFrameContentMessage((InteractEntityEvent) getEvent()))));
            getTransform().ifPresent((transform -> builder.append(Text.of(
                TextColors.RED, " in the ",
                getDimensionColor(), transform.getExtent().getDimension().getType().getName()))));
            return builder.build();
          }
        };
      case "minecraft:armor_stand":
        return new AttackAlert(griefProfile, event) {
          @Override
          public Text getMessageText() {
            // TODO: implement
            return Text.of("Armor Stand attacked.");
          }
        };
      default:
        return new AttackAlert(griefProfile, event);
    }
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.ATTACK;
  }

}
