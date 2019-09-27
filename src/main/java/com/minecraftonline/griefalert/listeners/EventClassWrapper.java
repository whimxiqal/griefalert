package com.minecraftonline.griefalert.listeners;

import org.spongepowered.api.event.Event;

import java.util.function.Consumer;

public class EventClassWrapper<T extends Event> {

  private final Consumer<T> consumer;
  private final Class<T> eventClass;

  EventClassWrapper(Class<T> eventClass, Consumer<T> consumer) {
    this.eventClass = eventClass;
    this.consumer = consumer;
  }

  public boolean isEvent(Event event) {
    return (event.getClass().isInstance(eventClass));
  }

  public void handle(T event) {
    consumer.accept(event);
  }

  public Class<T> getEventClass() {
    return eventClass;
  }

  public void ifEventThenHandle(Event event) {
    try {
      if (eventClass.isInstance(event)) {
        handle((T) event);
      }
    } catch (ClassCastException e) {
      e.printStackTrace();
    }
  }
}
