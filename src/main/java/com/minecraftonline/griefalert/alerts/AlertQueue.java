package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.structures.RotatingQueue;

public class AlertQueue extends RotatingQueue<Alert> {

  public AlertQueue(int capacity) {
    super(capacity);
  }

}
