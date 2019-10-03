package com.minecraftonline.griefalert.listeners;

import java.util.function.Consumer;
import java.util.function.Function;

import com.minecraftonline.griefalert.griefevents.profiles.EventWrapper;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventListener;
import org.spongepowered.api.util.annotation.NonnullByDefault;

public class GriefAlertListener<T extends Event> implements EventListener<T> {

  private final Function<T, EventWrapper> converter;
  private final Consumer<EventWrapper> handler;
  private final Consumer<EventListener<T>> registrar;

  GriefAlertListener(Function<T, EventWrapper> converter, Consumer<EventWrapper> handler, Consumer<EventListener<T>> registrar) {
    this.converter = converter;
    this.handler = handler;
    this.registrar = registrar;
  }

  @Override
  @NonnullByDefault
  public void handle(@NonnullByDefault T event) throws Exception {
    EventWrapper wrapper = converter.apply(event);
    if (wrapper != null) {
      handler.accept(wrapper);
    }
    // Else, this was not an event caused by a player
  }

  public void register() {
    registrar.accept(this);
  }

}
