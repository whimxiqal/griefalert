package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.profiles.EventWrapper;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;

import java.util.*;

public class GlobalListener implements EventListener<Event> {

  private final GriefAlert plugin;
  private List<EventClassWrapper<? extends Event>> eventStack = new ArrayList<>();

  public GlobalListener(GriefAlert plugin) {

    this.plugin = plugin;

    eventStack.add(new EventClassWrapper<>(
        ChangeBlockEvent.Break.class,
        (changeBlockEventBreak) -> {
          if (changeBlockEventBreak.getCause().root() instanceof Player) {
            EventWrapper eventWrapper = new EventWrapper(
                changeBlockEventBreak,
                GriefAlert.GriefType.DESTROY,
                ((Player) changeBlockEventBreak.getCause().root()).createSnapshot(),
                (Player) changeBlockEventBreak.getCause().root(),
                changeBlockEventBreak.getTransactions().get(0).getOriginal().getState().getType().getName()
            );
            if (changeBlockEventBreak.getTransactions().get(0).getOriginal().getLocation().isPresent()) {
              eventWrapper.setGriefedLocation(changeBlockEventBreak.getTransactions().get(0).getOriginal().getLocation().get());
            }
            plugin.handleGriefEvent(eventWrapper);
          }
        })
    );

    eventStack.add(new EventClassWrapper<>(
        ChangeBlockEvent.Grow.class,
        (changeBlockEventGrow) -> {
          if (changeBlockEventGrow.getCause().root() instanceof Player) {
            EventWrapper eventWrapper = new EventWrapper(
                changeBlockEventGrow,
                GriefAlert.GriefType.INTERACT,
                ((Player) changeBlockEventGrow.getCause().root()).createSnapshot(),
                (Player) changeBlockEventGrow.getCause().root(),
                changeBlockEventGrow.getTransactions().get(0).getOriginal().getState().getType().getName()
            );
            if (changeBlockEventGrow.getTransactions().get(0).getOriginal().getLocation().isPresent()) {
              eventWrapper.setGriefedLocation(changeBlockEventGrow.getTransactions().get(0).getOriginal().getLocation().get());
            }
            plugin.handleGriefEvent(eventWrapper);
          }
        }
    ));

    eventStack.add(new EventClassWrapper<>(
        ChangeBlockEvent.Modify.class,
        (changeBlockEventModify) -> {
          if (changeBlockEventModify.getCause().root() instanceof Player) {
            EventWrapper eventWrapper = new EventWrapper(
                changeBlockEventModify,
                GriefAlert.GriefType.INTERACT,
                ((Player) changeBlockEventModify.getCause().root()).createSnapshot(),
                (Player) changeBlockEventModify.getCause().root(),
                changeBlockEventModify.getTransactions().get(0).getOriginal().getState().getType().getName()
            );
            if (changeBlockEventModify.getTransactions().get(0).getOriginal().getLocation().isPresent()) {
              eventWrapper.setGriefedLocation(changeBlockEventModify.getTransactions().get(0).getOriginal().getLocation().get());
            }
            plugin.handleGriefEvent(eventWrapper);
          }
        }
    ));

    eventStack.add(new EventClassWrapper<>(
        InteractBlockEvent.Secondary.class,
        (interactBlockEventSecondary) -> {
          if (interactBlockEventSecondary.getCause().root() instanceof Player) {
            EventWrapper eventWrapper = new EventWrapper(
                interactBlockEventSecondary,
                GriefAlert.GriefType.INTERACT,
                ((Player) interactBlockEventSecondary.getCause().root()).createSnapshot(),
                (Player) interactBlockEventSecondary.getCause().root(),
                interactBlockEventSecondary.getTargetBlock().getState().getType().getName()
            );
            if (interactBlockEventSecondary.getTargetBlock().getLocation().isPresent()) {
              interactBlockEventSecondary.getTargetBlock().getLocation().get();
            }
            plugin.handleGriefEvent(eventWrapper);
          }
        }
    ));

    eventStack.add(new EventClassWrapper<>(
        UseItemStackEvent.class,
        (useItemStackEvent -> {
          if (useItemStackEvent.getCause().root() instanceof Player) {
            EventWrapper eventWrapper = new EventWrapper(
                useItemStackEvent,
                GriefAlert.GriefType.USE,
                ((Player) useItemStackEvent.getCause().root()).createSnapshot(),
                (Player) useItemStackEvent.getCause().root(),
                useItemStackEvent.getItemStackInUse().getType().getName()
            );
            if (eventWrapper.getGrieferSnapshot().getLocation().isPresent()) {
              eventWrapper.getGrieferSnapshot().getLocation().get();
            }
            plugin.handleGriefEvent(eventWrapper);
          }
        })
    ));


  }

  @Override
  public void handle(Event event) throws Exception {
    for (EventClassWrapper eventWrapper : eventStack) {
      eventWrapper.ifEventThenHandle(event);
    }
  }

}
