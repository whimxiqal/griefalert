package com.minecraftonline.griefalert.api.alerts;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.*;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.*;

public abstract class Alert implements Runnable {

  private static final HashMap<UUID, Stack<Transform<World>>> officerTransformHistory = new HashMap<>();

  protected int cacheCode;
  protected final GriefProfile griefProfile;
  private boolean silent = false;

  protected Alert(GriefProfile griefProfile) {
    this.cacheCode = cacheCode;
    this.griefProfile = griefProfile;
  }

  public final boolean check(Player officer) {

    // Get the necessary transform so the officer can teleport
    if (!getTransform().isPresent()) {
      return false;
    }

    Transform<World> alertTransform = getTransform().get();

    // Save the officer's previous transform and add it into the alert's database later
    // if the officer successfully teleports.
    Transform<World> officerPreviousTransform = officer.getTransform();

    // Teleport the officer
    if (!officer.setTransformSafely(alertTransform)) {
      Errors.sendCannotTeleportSafely(officer, alertTransform);
      return false;
    }

    // The officer has teleported successfully, so save their previous location in the history
    officerTransformHistory.putIfAbsent(officer.getUniqueId(), new Stack<>());
    officerTransformHistory.get(officer.getUniqueId()).push(officerPreviousTransform);

    officer.sendMessage(Format.heading("Checking Grief Alert:"));
    officer.sendMessage(getSummary());
    return true;

  }

  public static int revertTransform(Player officer) {

    if (!officerTransformHistory.containsKey(officer.getUniqueId())){
      return -1;
    }

    Stack<Transform<World>> history = officerTransformHistory.get(officer.getUniqueId());

    if (history.isEmpty()) {
      return -1;
    }

    Transform<World> previousTransform = history.pop();

    if (!officer.setTransformSafely(previousTransform)) {
      Errors.sendCannotTeleportSafely(officer, previousTransform);
    }

    return history.size();

  }


  /**
   * Getter for the bare Text without interactivity.
   *
   * @return the Text representation of this Alert.
   */
  public Text getMessageText() {
    Text.Builder builder = Text.builder();
    builder.append(Text.of(
        General.formatPlayerName(getGriefer()),
        Format.space(),
        getEventColor(), getGriefEvent().getPastTense(),
        Format.space(),
        getTargetColor(), Grammar.addIndefiniteArticle(griefProfile.getTarget().replace("minecraft:", ""))));
    getTransform().ifPresent((transform -> builder.append(Text.of(
        TextColors.RED, " in the ",
        getDimensionColor(), transform.getExtent().getDimension().getType().getName()))));
    return builder.build();
  }

  public abstract Optional<Transform<World>> getTransform();

  public abstract Player getGriefer();

  public abstract GriefEvent getGriefEvent();

  /**
   * Getter for the integer which represents this cached item in the ongoing.
   * AlertCache, which implements a RotatingQueue.
   *
   * @return integer for cache code
   */
  public final int getCacheCode() {
    return cacheCode;
  }

  public boolean isSilent() {
    return silent;
  }

  public void setSilent(boolean silent) {
    this.silent = silent;
  }

  public void pushAndRun() {
    GriefAlert.getInstance().getAlertQueue().push(this);
    run();
  }

  @Override
  public void run() {
    if (!isSilent()) {
      Comms.getStaffBroadcastChannel().send(getFullText());
    }
  }

  /**
   * Get the final text version of the Alert to broadcast to staff.
   *
   * @return the Text for broadcasting.
   */
  public final Text getFullText() {
    return getFullText(new LinkedList<>());
  }

  /**
   * Get the final text version of the Alert to broadcast to staff with
   * other interactive items to access other alerts.
   *
   * @param otherCodes The list of other items to add to the text.
   * @return A full text to be broadcast to staff.
   */
  final Text getFullText(List<Integer> otherCodes) {
    try {
      Text.Builder builder = Text.builder().append(getMessageText());
      otherCodes.add(0, getCacheCode()); // Add this to the list
      otherCodes.forEach((i) -> {
        builder.append(Format.space());
        Alert other = GriefAlert.getInstance().getAlertQueue().get(i);
        try {
          builder.append(
              Format.command(String.valueOf(i),
                  "/ga check " + other.getCacheCode(),
                  Text.of(other.getSummary())));
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
      return builder.build();
    } catch (Exception e) {
      e.printStackTrace();
      return Text.EMPTY;
    }
  }

  public final boolean isRepeatOf(Alert other) {
    return other.getGriefer().getUniqueId().equals(getGriefer().getUniqueId())
        && other.griefProfile.equals(griefProfile);
  }

  protected final TextColor getEventColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.EVENT_COLOR).map(General::stringToColor)
        .orElse(Format.ALERT_EVENT_COLOR);
  }

  protected final TextColor getTargetColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.TARGET_COLOR).map(General::stringToColor)
        .orElse(Format.ALERT_TARGET_COLOR);
  }

  protected final TextColor getDimensionColor() {
    return griefProfile.getDataContainer()
        .getString(GriefProfileDataQueries.DIMENSION_COLOR).map(General::stringToColor)
        .orElse(Format.ALERT_DIMENSION_COLOR);
  }

  protected Text getSummary() {
    Text.Builder builder = Text.builder();

    builder.append(Text.of(TextColors.DARK_AQUA, "Player: ", TextColors.GRAY, getGriefer().getName()), Format.endLine());
    builder.append(Text.of(TextColors.DARK_AQUA, "Event: ", TextColors.GRAY, griefProfile.getGriefEvent().getId()), Format.endLine());
    builder.append(Text.of(TextColors.DARK_AQUA, "Target: ", TextColors.GRAY, griefProfile.getTarget().replace("minecraft:", "")), Format.endLine());
    getTransform().ifPresent((transform) ->
        builder.append(Text.of(TextColors.DARK_AQUA, "Location: ", TextColors.GRAY,  Format.location(
            transform.getLocation().getBlockX(),
            transform.getLocation().getBlockY(),
            transform.getLocation().getBlockZ(),
            transform.getLocation().getExtent(),
            false))));

    return builder.build();
  }


  public final void setCacheCode(int cacheCode) {
    this.cacheCode = cacheCode;
  }
}
