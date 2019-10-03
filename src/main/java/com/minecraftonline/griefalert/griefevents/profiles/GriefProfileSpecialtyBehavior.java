package com.minecraftonline.griefalert.griefevents.profiles;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.GriefEvent;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.immutable.tileentity.ImmutableSignData;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;


public class GriefProfileSpecialtyBehavior implements Consumer<GriefAlert> {

  private final GriefAlert.GriefType requiredGriefType;
  private final String requiredGriefedId;
  private final Consumer<GriefAlert> consumer;
  private final Function<GriefEvent, String> logBehavior;

  private static final List<GriefProfileSpecialtyBehavior> SPECIALTY_BEHAVIORS = Arrays.asList(
      new GriefProfileSpecialtyBehavior(
          GriefAlert.GriefType.USE,
          "wall_sign",
          (plugin) -> Sponge.getServer().getBroadcastChannel().send(
              Text.of(TextColors.AQUA, "Special 'Use Sign Behavior'")
          ),
          (griefEvent) -> {
            if (griefEvent.getEvent().getEvent() instanceof ChangeBlockEvent.Place) {
              for (ImmutableDataManipulator dataManipulator :
                  (((ChangeBlockEvent.Place) griefEvent.getEvent().getEvent())
                      .getTransactions().get(0).getOriginal().getManipulators())) {
                if (dataManipulator instanceof ImmutableSignData) {
                  List<String> signLines = new LinkedList<>();
                  for (Text text : ((ImmutableSignData) dataManipulator).asList()) {
                    signLines.add(text.toPlain());
                  }
                  return String.join("\\n", signLines);
                }
              }
            }
            return "";
          }
      )
  );

  private static final GriefProfileSpecialtyBehavior NONE =
      new GriefProfileSpecialtyBehavior(null, null, (plugin) -> {}, (profile) -> "");

  private GriefProfileSpecialtyBehavior(
      GriefAlert.GriefType requiredGriefType,
      String requiredGriefedId,
      Consumer<GriefAlert> consumer,
      Function<GriefEvent, String> logBehavior) {
    this.requiredGriefType = requiredGriefType;
    this.requiredGriefedId = requiredGriefedId;
    this.consumer = consumer;
    this.logBehavior = logBehavior;
  }

  @Nonnull
  static GriefProfileSpecialtyBehavior getMatching(GriefProfile griefProfile) {
    for (GriefProfileSpecialtyBehavior specialtyBehavior : SPECIALTY_BEHAVIORS) {
      if (specialtyBehavior.matches(griefProfile)) {
        return specialtyBehavior;
      }
    }
    return NONE;
  }

  public String getSpecialLogString(GriefEvent griefEvent) {
    return logBehavior.apply(griefEvent);
  }

  private boolean matches(GriefProfile griefProfile) {
    return this.requiredGriefType.equals(griefProfile.getGriefType())
        && this.requiredGriefedId.equals(griefProfile.getGriefedId());
  }

  @Override
  public void accept(GriefAlert griefAlert) {
    consumer.accept(griefAlert);
  }
}
