package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.GriefEvents;
import com.minecraftonline.griefalert.util.Hanging;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class InteractAlert extends SpongeAlert {

  InteractAlert(GriefProfile griefProfile, InteractEvent event) {
    super(griefProfile, event);
  }

  public static InteractAlert of(GriefProfile griefProfile, InteractEvent event) {

      switch (griefProfile.getTarget()) {
        case "minecraft:item_frame":
          return new InteractAlert(griefProfile, event) {
            @Override
            public Text getMessageText() {
              Text.Builder builder = Text.builder();
              builder.append(Text.of(
                  General.formatPlayerName(getGriefer()),
                  Format.space(),
                  getEventColor(), "interacted with",
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
          return new InteractAlert(griefProfile, event) {
            @Override
            public Text getMessageText() {
              // TODO: implement
              return Text.of("Armor Stand interacted.");
            }
          };
        default:
          return new InteractAlert(griefProfile, event);
      }
  }

  @Override
  public GriefEvent getGriefEvent() {
    return GriefEvents.INTERACT;
  }
}
