package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.GriefEvent;
import com.minecraftonline.griefalert.griefevents.profiles.EventWrapper;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class GlobalListener implements EventListener<Event> {

  private List<EventClassWrapper<? extends Event>> eventStack = new ArrayList<>();

  /**
   * The listener for GriefAlert which listens to every event passed through the game.
   * This global listener parses through which events are useful and if the event
   * (using a special wrapper class) has characteristics matching that of a saved Grief
   * Profile, the event is combined with the Profile into a Grief Event.
   *
   * @param plugin The main plugin instance
   * @see com.minecraftonline.griefalert.griefevents.GriefEvent
   * @see com.minecraftonline.griefalert.griefevents.profiles.GriefProfile
   */
  public GlobalListener(GriefAlert plugin) {

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
                changeBlockEventBreak.getTransactions().get(0).getOriginal()
                    .getState().getType().getId(),
                changeBlockEventBreak.getTransactions().get(0).getOriginal()
                    .getState().getType().getTranslation().get()
            );
            if (changeBlockEventBreak.getTransactions().get(0).getOriginal()
                .getLocation().isPresent()) {
              eventWrapper.setGriefedLocation(
                  changeBlockEventBreak.getTransactions().get(0).getOriginal().getLocation().get()
              );
            }
            if (plugin.getMuseum().getProfileBuilder(root).isPresent()) {
              plugin.getMuseum().getProfileBuilder(root).get().incorporate(eventWrapper, root);
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
                changeBlockEventPlace.getTransactions().get(0).getFinal()
                    .getState().getType().getId(),
                changeBlockEventPlace.getTransactions().get(0).getFinal()
                    .getState().getType().getTranslation().get()
            );
            if (changeBlockEventPlace.getTransactions().get(0).getFinal()
                .getLocation().isPresent()) {
              eventWrapper.setGriefedLocation(
                  changeBlockEventPlace.getTransactions().get(0).getFinal().getLocation().get()
              );
            }
            if (plugin.getMuseum().getProfileBuilder(root).isPresent()) {
              plugin.getMuseum().getProfileBuilder(root).get().incorporate(eventWrapper, root);
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
                changeBlockEventModify.getTransactions().get(0).getOriginal()
                    .getState().getType().getId(),
                changeBlockEventModify.getTransactions().get(0).getOriginal()
                    .getState().getType().getTranslation().get()
            );
            if (changeBlockEventModify.getTransactions().get(0).getOriginal()
                .getLocation().isPresent()) {
              eventWrapper.setGriefedLocation(
                  changeBlockEventModify.getTransactions().get(0).getOriginal().getLocation().get()
              );
            }
            if (plugin.getMuseum().getProfileBuilder(root).isPresent()) {
              plugin.getMuseum().getProfileBuilder(root).get().incorporate(eventWrapper, root);
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
              plugin.getMuseum().getProfileBuilder(root).get().incorporate(eventWrapper, root);
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
            eventWrapper.setGriefedLocation(interactEntityEventPrimary.getTargetEntity()
                .getLocation());
            if (plugin.getMuseum().getProfileBuilder(root).isPresent()) {
              plugin.getMuseum().getProfileBuilder(root).get().incorporate(eventWrapper, root);
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
            eventWrapper.setGriefedLocation(interactEntityEventSecondary.getTargetEntity()
                .getLocation());
            if (plugin.getMuseum().getProfileBuilder(root).isPresent()) {
              plugin.getMuseum().getProfileBuilder(root).get().incorporate(eventWrapper, root);
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
                interactBlockEventSecondary.getTargetBlock().getState().getType()
                    .getTranslation().get()
            );
            if (interactBlockEventSecondary.getTargetBlock().getLocation().isPresent()) {
              eventWrapper.setGriefedLocation(interactBlockEventSecondary.getTargetBlock()
                  .getLocation().get());
            }
            if (plugin.getMuseum().getProfileBuilder(root).isPresent()) {
              // We only want the Grief Profile building to work with empty hands
              if (!root.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
                plugin.getMuseum().getProfileBuilder(root).get().incorporate(eventWrapper, root);
                interactBlockEventSecondary.setCancelled(true);
                return;
              }
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


  }

  @Override
  public void handle(@NonnullByDefault Event event) throws Exception {
    for (EventClassWrapper eventClassWrapper : eventStack) {
      eventClassWrapper.ifEventThenHandle(event);
    }
  }

}
