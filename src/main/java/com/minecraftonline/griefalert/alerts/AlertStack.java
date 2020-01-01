/* Created by PietElite */

package com.minecraftonline.griefalert.alerts;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.structures.RotatingStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;
import javax.annotation.Nonnull;


/**
 * The ongoing queue for all <code>Alert</code>s. This is only local storage. Someone
 * can access each alert by their 'cachecode' using the <code>RotatingQueue</code>
 * API.
 *
 * @see Alert
 * @see RotatingStack
 */
public final class AlertStack extends RotatingStack<Alert> {

  // A map relating each player to a list of consecutive similar alerts for silencing
  private final HashMap<UUID, LinkedList<Alert>> repeatMap = new HashMap<>();

  public AlertStack(final int capacity) {
    super(capacity);
  }


  /**
   * Adds an alert to the queue. This method will replace the old <code>Alert</code> at
   * that location, if it already exists, with the new one. Also, the given <code>alert
   * </code> will also be assigned the corresponding index for retrieval.
   *
   * @param alert The alert to add
   * @return The retrieval code
   */
  @Override
  public int push(@Nonnull final Alert alert) {

    // Push the alert to the RotatingQueue
    int output = super.push(alert);

    // Set the alert with the proper index
    alert.setStackIndex(output);

    // Update alertMap
    repeatMap.putIfAbsent(alert.getGriefer().getUniqueId(), new LinkedList<>());
    LinkedList<Alert> repeats = repeatMap.get(alert.getGriefer().getUniqueId());

    if (!repeats.isEmpty() && alert.isRepeatOf(repeats.getLast())) {
      alert.setSilent(true);
    }

    repeats.add(alert);

    int hiddenEventLimit = GriefAlert.getInstance().getConfigHelper().getHiddenRepeatedEventLimit();
    if (repeats.size() >= hiddenEventLimit) {
      repeats.clear();
    }

    // Finish
    return output;
  }

}
