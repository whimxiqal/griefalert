package com.minecraftonline.griefalert.listeners;

import java.util.function.Consumer;
import org.spongepowered.api.event.Event;

class EventClassWrapper<T extends Event> {

  private final Consumer<T> behavior;
  private final Class<T> eventClass;

  EventClassWrapper(Class<T> eventClass, Consumer<T> consumer) {
    this.eventClass = eventClass;
    this.behavior = consumer;
  }

  @SuppressWarnings("unchecked")
  void ifEventThenHandle(Event event) {
    try {
      if (eventClass.isInstance(event)) {
        behavior.accept((T) event);
      }
    } catch (ClassCastException e) {
      e.printStackTrace();
    }
  }
}
