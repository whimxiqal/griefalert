package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.structures.RotatingQueue;
import org.spongepowered.api.entity.EntitySnapshot;

import java.util.HashMap;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;

public class AlertQueue extends RotatingQueue<Alert> {

  public AlertQueue(int capacity) {
    super(capacity);
  }

  public Optional<EntitySnapshot> getOfficerSnapshot(UUID uuid) {

    // TODO: Add a way for officers to return back to previous locations when checking grief
    //  alerts

    return Optional.empty();
  }

}
