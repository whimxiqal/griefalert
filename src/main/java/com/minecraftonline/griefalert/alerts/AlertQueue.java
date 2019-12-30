package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.structures.RotatingQueue;
import com.minecraftonline.griefalert.storage.ConfigHelper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

public class AlertQueue extends RotatingQueue<Alert> {

  private HashMap<UUID, LinkedList<Alert>> repeatMap = new HashMap<>();

  public AlertQueue(int capacity) {
    super(capacity);
  }

  @Override
  public int push(Alert alert) {
    int output = super.push(alert);
    alert.setCacheCode(output);

    repeatMap.putIfAbsent(alert.getGriefer().getUniqueId(), new LinkedList<>());
    LinkedList<Alert> repeats = repeatMap.get(alert.getGriefer().getUniqueId());

    if (!repeats.isEmpty() && alert.isRepeatOf(repeats.getLast())) {
      alert.setSilent(true);
    }

    repeats.add(alert);

    if (repeats.size() >= GriefAlert.getInstance().getConfigHelper().getHiddenRepeatedEventLimit()) {
      repeats.clear();
    }

    return output;
  }

}
