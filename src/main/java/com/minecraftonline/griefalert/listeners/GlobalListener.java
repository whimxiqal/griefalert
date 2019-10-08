package com.minecraftonline.griefalert.listeners;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.GriefEvent;
import com.minecraftonline.griefalert.griefevents.profiles.EventWrapper;

import java.util.ArrayList;
import java.util.List;

import com.minecraftonline.griefalert.griefevents.profiles.GriefProfileMuseum;
import com.minecraftonline.griefalert.griefevents.profiles.SpecialtyBehavior;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.event.item.inventory.UseItemStackEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class GlobalListener {

  private List<GriefAlertListener<? extends Event>> listenerStack = new ArrayList<>();

  public List<GriefAlertListener<? extends Event>> getListenerStack() {
    return listenerStack;
  }

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

    listenerStack.add(new GriefAlertListener<ChangeBlockEvent.Break>(
        // Converter
        changeBlockEventBreak -> {
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
            return eventWrapper;
          } else {
            return null;
          }
        },
        // Behavior
        eventWrapper -> {
          if (plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).isPresent()) {
            plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).get().incorporate(eventWrapper, eventWrapper.getGriefer());
            eventWrapper.cancelEvent();
            return;
          }
          if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
            GriefEvent.throwGriefEvent(
                plugin,
                plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                eventWrapper);
          }
        },
        // Registrar
        listener -> Sponge.getEventManager().registerListener(plugin, ChangeBlockEvent.Break.class, listener)
    ));


    listenerStack.add(new GriefAlertListener<ChangeBlockEvent.Place>(
        // Converter
        changeBlockEventPlace -> {
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
            return eventWrapper;
          } else {
            return null;
          }
        },
        // Behavior
        eventWrapper -> {
          if (plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).isPresent()) {
            plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).get().incorporate(eventWrapper, eventWrapper.getGriefer());
            eventWrapper.cancelEvent();
            return;
          }
          if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
            GriefEvent.throwGriefEvent(
                plugin,
                plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                eventWrapper);
          }
        },
        // Registrar
        listener -> Sponge.getEventManager().registerListener(plugin, ChangeBlockEvent.Place.class, listener)
    ));

    listenerStack.add(new GriefAlertListener<ChangeBlockEvent.Modify>(
        // Converter
        changeBlockEventModify -> {
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
            return eventWrapper;
          } else {
            return null;
          }
        },
        // Behavior
        eventWrapper -> {
          if (plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).isPresent()) {
            plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).get().incorporate(eventWrapper, eventWrapper.getGriefer());
            eventWrapper.cancelEvent();
            return;
          }
          if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
            GriefEvent.throwGriefEvent(
                plugin,
                plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                eventWrapper);
          }
        },
        // Registrar
        listener -> Sponge.getEventManager().registerListener(plugin, ChangeBlockEvent.Modify.class, listener)
    ));

    listenerStack.add(new GriefAlertListener<UseItemStackEvent.Finish>(
        // Converter
        useItemStackEventFinish -> {
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
            return eventWrapper;
          } else {
            return null;
          }
        },
        // Behavior
        eventWrapper -> {
          if (plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).isPresent()) {
            plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).get().incorporate(eventWrapper, eventWrapper.getGriefer());
            return;
          }
          if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
            GriefEvent.throwGriefEvent(
                plugin,
                plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                eventWrapper);
          }
        },
        // Registrar
        listener -> Sponge.getEventManager().registerListener(plugin, UseItemStackEvent.Finish.class, listener)
    ));

    listenerStack.add(new GriefAlertListener<InteractEntityEvent.Primary>(
        // Converter
        interactEntityEventPrimary -> {
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
            return eventWrapper;
          } else {
            return null;
          }
        },
        // Behavior
        eventWrapper -> {
          if (plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).isPresent()) {
            plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).get().incorporate(eventWrapper, eventWrapper.getGriefer());
            eventWrapper.cancelEvent();
            return;
          }
          if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
            GriefEvent.throwGriefEvent(
                plugin,
                plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                eventWrapper);
          }
        },
        // Registrar
        listener -> Sponge.getEventManager().registerListener(plugin, InteractEntityEvent.Primary.class, listener)
    ));

    listenerStack.add(new GriefAlertListener<InteractEntityEvent.Secondary>(
        // Converter
        interactEntityEventPrimary -> {
          if (interactEntityEventPrimary.getCause().root() instanceof Player) {
            Player root = (Player) interactEntityEventPrimary.getCause().root();
            EventWrapper eventWrapper = new EventWrapper(
                interactEntityEventPrimary,
                GriefAlert.GriefType.INTERACT,
                root.createSnapshot(),
                root,
                interactEntityEventPrimary.getTargetEntity().getType().getId(),
                interactEntityEventPrimary.getTargetEntity().getType().getTranslation().get()
            );
            eventWrapper.setGriefedLocation(interactEntityEventPrimary.getTargetEntity()
                .getLocation());
            return eventWrapper;
          } else {
            return null;
          }
        },
        // Behavior
        eventWrapper -> {
          if (plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).isPresent()) {
            plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).get().incorporate(eventWrapper, eventWrapper.getGriefer());
            eventWrapper.cancelEvent();
            return;
          }
          if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
            GriefEvent.throwGriefEvent(
                plugin,
                plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                eventWrapper);
          }
        },
        // Registrar
        listener -> Sponge.getEventManager().registerListener(plugin, InteractEntityEvent.Secondary.class, listener)
    ));

    listenerStack.add(new GriefAlertListener<InteractBlockEvent.Secondary>(
        // Converter
        interactBlockEventSecondary -> {
          if (interactBlockEventSecondary.getCause().root() instanceof Player) {
            Player root = (Player) interactBlockEventSecondary.getCause().root();
            if (root.hasPermission(GriefAlert.Permission.GRIEFALERT_DEGRIEF.toString())
                && root.getItemInHand(HandTypes.MAIN_HAND).isPresent()
                && root.getItemInHand(HandTypes.MAIN_HAND).get().getType()
                .equals(ItemTypes.STICK)
                && !interactBlockEventSecondary.getTargetBlock().getState().getType().equals(BlockTypes.AIR)) {
              // Degriefing!
              EventWrapper eventWrapper = new EventWrapper(
                  interactBlockEventSecondary,
                  GriefAlert.GriefType.DEGRIEF,
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
              return eventWrapper;
            } else {
              // Not Degriefing!
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
              return eventWrapper;
            }
          } else {
            return null;
          }
        },
        // Behavior
        eventWrapper -> {
          if (eventWrapper.getType().equals(GriefAlert.GriefType.DEGRIEF)) {
            GriefEvent.throwGriefEvent(
                plugin,
                GriefProfileMuseum.DEGRIEF_PROFILE,
                eventWrapper
            );
            if (eventWrapper.getGriefedLocation().isPresent()) {
              Location<World> location = eventWrapper.getGriefedLocation().get();
              location.getExtent().setBlock(location.getBlockPosition(), BlockState.builder().blockType(BlockTypes.AIR).build());
            }
            return;
          }
          // Check if we want to build a profile here
          if (plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).isPresent()) {
            // We only want the Grief Profile building to work with empty hands
            if (eventWrapper.getGriefer().getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
              if (eventWrapper.getGriefer().getItemInHand(HandTypes.MAIN_HAND).get().equals(ItemStack.empty())) {
                plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).get().incorporate(eventWrapper, eventWrapper.getGriefer());
                eventWrapper.cancelEvent();
                return;
              }
            }
          }
          if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
            GriefEvent.throwGriefEvent(
                plugin,
                plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                eventWrapper);
          }
        },
        // Registrar
        listener -> Sponge.getEventManager().registerListener(plugin, InteractBlockEvent.Secondary.class, listener)
    ));

    listenerStack.add(new GriefAlertListener<InteractItemEvent.Secondary>(
        // Converter
        interactBlockEventSecondary -> {
          if (interactBlockEventSecondary.getCause().root() instanceof Player) {
            Player root = (Player) interactBlockEventSecondary.getCause().root();
            EventWrapper eventWrapper = new EventWrapper(
                interactBlockEventSecondary,
                GriefAlert.GriefType.USE,
                root.createSnapshot(),
                root,
                interactBlockEventSecondary.getItemStack().getType().getId(),
                interactBlockEventSecondary.getItemStack().getType().getTranslation().get()
            );
            eventWrapper.setGriefedLocation(root.getLocation());
            return eventWrapper;
          } else {
            return null;
          }
        },
        // Behavior
        eventWrapper -> {
          if (plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).isPresent()) {
            // We only want the Grief Profile building to work with empty hands
            if (eventWrapper.getGriefer().getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
              if (!eventWrapper.getGriefer().getItemInHand(HandTypes.MAIN_HAND).get().equals(ItemStack.empty())) {
                plugin.getMuseum().getProfileBuilder(eventWrapper.getGriefer()).get().incorporate(eventWrapper, eventWrapper.getGriefer());
                eventWrapper.cancelEvent();
                return;
              }
            }
          }
          if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
            GriefEvent.throwGriefEvent(
                plugin,
                plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                eventWrapper);
          }
        },
        // Registrar
        listener -> Sponge.getEventManager().registerListener(plugin, InteractItemEvent.Secondary.class, listener)
    ));

    listenerStack.add(new GriefAlertListener<ChangeSignEvent>(
        // Converter
        changeSignEvent -> {
          if (changeSignEvent.getCause().root() instanceof Player) {
            Player root = (Player) changeSignEvent.getCause().root();
            EventWrapper eventWrapper = new EventWrapper(
                changeSignEvent,
                GriefAlert.GriefType.USE,
                root.createSnapshot(),
                root,
                changeSignEvent.getTargetTile().getType().getId(),
                changeSignEvent.getTargetTile().getType().getName()
            );
            eventWrapper.setGriefedLocation(changeSignEvent.getTargetTile().getLocation());
            return eventWrapper;
          } else {
            return null;
          }
        },
        // Behavior
        eventWrapper -> {
          // This is not used for profile building!
          if (plugin.getMuseum().getMatchingProfile(eventWrapper).isPresent()) {
            GriefEvent.throwGriefEvent(
                plugin,
                plugin.getMuseum().getMatchingProfile(eventWrapper).get(),
                eventWrapper,
                SpecialtyBehavior.SIGN_PLACEMENT);
          }
        },
        // Registrar
        listener -> Sponge.getEventManager().registerListener(plugin, ChangeSignEvent.class, listener)
    ));
  }
}
