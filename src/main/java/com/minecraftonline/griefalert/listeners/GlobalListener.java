package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.GriefEvent;
import com.minecraftonline.griefalert.griefevents.profiles.EventWrapper;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
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
            Player root = (Player) changeBlockEventBreak.getCause().root();
            EventWrapper eventWrapper = new EventWrapper(
                changeBlockEventBreak,
                GriefAlert.GriefType.DESTROY,
                root.createSnapshot(),
                root,
                changeBlockEventBreak.getTransactions().get(0).getOriginal().getState().getType().getId(),
                changeBlockEventBreak.getTransactions().get(0).getOriginal().getState().getType().getTranslation().get()
            );
            if (changeBlockEventBreak.getTransactions().get(0).getOriginal().getLocation().isPresent()) {
              eventWrapper.setGriefedLocation(changeBlockEventBreak.getTransactions().get(0).getOriginal().getLocation().get());
            }
            if (plugin.getMuseum().getProfileBuilder(root).isPresent()) {
              plugin.getMuseum().getProfileBuilder(root).get().attach(eventWrapper, root);
              changeBlockEventBreak.setCancelled(true);
              return;
            }
            if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
              GriefEvent.throwGriefEvent(
                  plugin,
                  plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                  eventWrapper);
            }
          }
        })
    );

    eventStack.add(new EventClassWrapper<>(
        ChangeBlockEvent.Place.class,
        (changeBlockEventPlace) -> {
          if (changeBlockEventPlace.getCause().root() instanceof Player) {
            Player root = (Player) changeBlockEventPlace.getCause().root();
            EventWrapper eventWrapper = new EventWrapper(
                changeBlockEventPlace,
                GriefAlert.GriefType.USE,
                root.createSnapshot(),
                root,
                changeBlockEventPlace.getTransactions().get(0).getFinal().getState().getType().getId(),
                changeBlockEventPlace.getTransactions().get(0).getFinal().getState().getType().getTranslation().get()
            );
            if (changeBlockEventPlace.getTransactions().get(0).getOriginal().getLocation().isPresent()) {
              eventWrapper.setGriefedLocation(changeBlockEventPlace.getTransactions().get(0).getOriginal().getLocation().get());
            }
            if (plugin.getMuseum().getProfileBuilder(root).isPresent()) {
              plugin.getMuseum().getProfileBuilder(root).get().attach(eventWrapper, root);
              changeBlockEventPlace.setCancelled(true);
              return;
            }
            if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
              GriefEvent.throwGriefEvent(
                  plugin,
                  plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                  eventWrapper);
            }
          }
        })
    );

    eventStack.add(new EventClassWrapper<>(
        ChangeBlockEvent.Modify.class,
        (changeBlockEventModify) -> {
          if (changeBlockEventModify.getCause().root() instanceof Player) {
            Player root = (Player) changeBlockEventModify.getCause().root();
            EventWrapper eventWrapper = new EventWrapper(
                changeBlockEventModify,
                GriefAlert.GriefType.INTERACT,
                root.createSnapshot(),
                root,
                changeBlockEventModify.getTransactions().get(0).getOriginal().getState().getType().getId(),
                changeBlockEventModify.getTransactions().get(0).getOriginal().getState().getType().getTranslation().get()
            );
            if (changeBlockEventModify.getTransactions().get(0).getOriginal().getLocation().isPresent()) {
              eventWrapper.setGriefedLocation(changeBlockEventModify.getTransactions().get(0).getOriginal().getLocation().get());
            }
            if (plugin.getMuseum().getProfileBuilder(root).isPresent()) {
              plugin.getMuseum().getProfileBuilder(root).get().attach(eventWrapper, root);
              changeBlockEventModify.setCancelled(true);
              return;
            }
            if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
              GriefEvent.throwGriefEvent(
                  plugin,
                  plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                  eventWrapper);
            }
          }
        }
    ));

    eventStack.add(new EventClassWrapper<>(
        UseItemStackEvent.Finish.class,
        (useItemStackEventFinish -> {
          if (useItemStackEventFinish.getCause().root() instanceof Player) {
            Player root = (Player) useItemStackEventFinish.getCause().root();
            EventWrapper eventWrapper = new EventWrapper(
                useItemStackEventFinish,
                GriefAlert.GriefType.USE,
                root.createSnapshot(),
                root,
                useItemStackEventFinish.getItemStackInUse().getType().getId(),
                useItemStackEventFinish.getItemStackInUse().getType().getTranslation().get()
            );
            eventWrapper.setGriefedLocation(root.getLocation());
            if (plugin.getMuseum().getProfileBuilder(root).isPresent()) {
              plugin.getMuseum().getProfileBuilder(root).get().attach(eventWrapper, root);
              return;
            }
            if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
              GriefEvent.throwGriefEvent(
                  plugin,
                  plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                  eventWrapper);
            }
          }
        })
    ));

    eventStack.add(new EventClassWrapper<>(
        InteractEntityEvent.Primary.class,
        (interactEntityEventPrimary) -> {
          if (interactEntityEventPrimary.getCause().root() instanceof Player) {
            Player root = (Player) interactEntityEventPrimary.getCause().root();
            EventWrapper eventWrapper = new EventWrapper(
                interactEntityEventPrimary,
                GriefAlert.GriefType.DESTROY,
                root.createSnapshot(),
                root,
                interactEntityEventPrimary.getTargetEntity().getType().getId(),
                interactEntityEventPrimary.getTargetEntity().getType().getTranslation().get()
            );
            eventWrapper.setGriefedLocation(interactEntityEventPrimary.getTargetEntity().getLocation());
            if (plugin.getMuseum().getProfileBuilder(root).isPresent()) {
              plugin.getMuseum().getProfileBuilder(root).get().attach(eventWrapper, root);
              interactEntityEventPrimary.setCancelled(true);
              return;
            }
            if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
              GriefEvent.throwGriefEvent(
                  plugin,
                  plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                  eventWrapper);
            }
          }
        }
    ));

    eventStack.add(new EventClassWrapper<>(
        InteractEntityEvent.Secondary.class,
        (interactEntityEventSecondary) -> {
          if (interactEntityEventSecondary.getCause().root() instanceof Player) {
            Player root = (Player) interactEntityEventSecondary.getCause().root();
            EventWrapper eventWrapper = new EventWrapper(
                interactEntityEventSecondary,
                GriefAlert.GriefType.INTERACT,
                root.createSnapshot(),
                root,
                interactEntityEventSecondary.getTargetEntity().getType().getId(),
                interactEntityEventSecondary.getTargetEntity().getType().getTranslation().get()
            );
            eventWrapper.setGriefedLocation(interactEntityEventSecondary.getTargetEntity().getLocation());
            if (plugin.getMuseum().getProfileBuilder(root).isPresent()) {
              plugin.getMuseum().getProfileBuilder(root).get().attach(eventWrapper, root);
              interactEntityEventSecondary.setCancelled(true);
              return;
            }
            if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
              GriefEvent.throwGriefEvent(
                  plugin,
                  plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                  eventWrapper);
            }
          }
        }
    ));

    eventStack.add(new EventClassWrapper<>(
        InteractBlockEvent.Secondary.class,
        (interactBlockEventSecondary) -> {
          if (interactBlockEventSecondary.getCause().root() instanceof Player) {
            Player root = (Player) interactBlockEventSecondary.getCause().root();
            EventWrapper eventWrapper = new EventWrapper(
                interactBlockEventSecondary,
                GriefAlert.GriefType.INTERACT,
                root.createSnapshot(),
                root,
                interactBlockEventSecondary.getTargetBlock().getState().getType().getId(),
                interactBlockEventSecondary.getTargetBlock().getState().getType().getTranslation().get()
            );
            if (interactBlockEventSecondary.getTargetBlock().getLocation().isPresent()) {
              eventWrapper.setGriefedLocation(interactBlockEventSecondary.getTargetBlock().getLocation().get());
            }
            // This event is not used for building profiles (Add these manually)
            if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
              GriefEvent.throwGriefEvent(
                  plugin,
                  plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                  eventWrapper);
            }
          }
        }
    ));


  }

  @Override
  public void handle(Event event) throws Exception {
    for (EventClassWrapper eventClassWrapper : eventStack) {
      eventClassWrapper.ifEventThenHandle(event);
    }
  }

}
