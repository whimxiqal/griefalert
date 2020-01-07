/* Created by PietElite */

package com.minecraftonline.griefalert.api.alerts;

import com.minecraftonline.griefalert.api.caches.AlertStack;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.World;


/**
 * An interface for an <code>Object</code> to store information and send messages
 * regarding events that match a certain {@link GriefProfile}.
 *
 * @author PietElite
 */
public interface Alert extends Runnable {

  /**
   * Get the <code>Player</code> responsible for triggering the <code>Alert</code>.
   *
   * @return The griefer
   */
  @Nonnull
  Player getGriefer();

  /**
   * Get the <code>Transform</code> of the griefer when the griefer
   * triggered the <code>Alert</code>.
   *
   * @return The <code>Transform</code>
   * @see Player
   */
  @Nonnull
  Transform<World> getGrieferTransform();

  /**
   * Construct the main message body for this <code>Alert</code>.
   *
   * @return the main <code>Text</code> for this <code>Alert</code>
   */
  @Nonnull
  Text getMessageText();

  /**
   * Get summary text for this <code>Alert</code>.
   *
   * @return Text representing a cohesive summary of the <code>Alert</code>
   */
  @Nonnull
  Text getSummary();

  /**
   * Get an <code>Optional</code> <code>String</code> that represent extra content for
   * help compiling the summary text.
   *
   * @return The extra content
   */
  @Nonnull
  Optional<String> getExtraSummaryContent();

  /**
   * Returns whether this <code>Alert</code> is silent.
   *
   * @return true if silent and staff are not notified of the <code>Alert</code>
   */
  boolean isSilent();

  /**
   * Sets whether this Alert will be silent when run.
   *
   * @param silent true if <code>Alert</code> is to be silent. False if
   *               <code>Alert</code> is to not be silent.
   */
  void setSilent(boolean silent);

  /**
   * Get the <code>GriefEvent</code> associated with this <code>Alert</code>.
   * This is always the <code>GriefEvent</code> associated with the
   * {@link GriefProfile}.
   *
   * @return The GriefEvent
   */
  @Nonnull
  GriefEvent getGriefEvent();

  /**
   * Get the target of the alert. This is always the target associated with
   * the {@link GriefProfile}.
   *
   * @return The String ID of the target
   */
  @Nonnull
  String getTarget();

  @Override
  void run();

  /**
   * Check the <code>Alert</code> with the given <code>Player</code>.
   *
   * @param officer The staff member
   * @return true if the player teleported correctly
   */
  @SuppressWarnings("UnusedReturnValue")
  boolean checkBy(@Nonnull final Player officer);

  /**
   * Get the number which correlates this <code>Alert</code> to the
   * {@link AlertStack} for
   * retrieval.
   *
   * @return The index within the <code>AlertStack</code>
   */
  int getStackIndex();

  /**
   * Get the <code>Text</code> of the <code>Alert</code> with this
   * <code>Alert</code>'s stack index appended to the end.
   *
   * @return The <code>Text</code>
   * @see AlertStack
   */
  @Nonnull
  Text getTextWithIndex();

  /**
   * Get the <code>Text</code> of the <code>Alert</code> with multiple
   * stack indices appended to the end. This is mainly used for chainging
   * similar <code>Alert</code>s together.
   *
   * @param allIndices The list of integers to append
   * @return The <code>Text</code>
   */
  @Nonnull
  Text getTextWithIndices(@Nonnull final List<Integer> allIndices);

  /**
   * Determine whether this <code>Alert</code> is a repeat of another <code>Alert</code>.
   * Used for silencing <code>Alert</code>s which occur immediately following each other.
   *
   * @param other The other <code>Alert</code>
   * @return true if other is a repeat of this <code>Alert</code>
   */
  boolean isRepeatOf(@Nonnull final Alert other);

  /**
   * Setter for the index corresponding to the index in the
   * {@link AlertStack} which
   * can retrieve this <code>Alert</code>.
   *
   * @param stackIndex the stack index
   */
  void setStackIndex(final int stackIndex);

  /**
   * Getter for the <code>TextColor</code> of the Event in this <code>Alert</code>'s
   * message.
   *
   * @return The Event <code>TextColor</code>
   */
  @Nonnull
  TextColor getEventColor();

  /**
   * Getter for the <code>TextColor</code> of the Target in this <code>Alert</code>'s
   * message.
   *
   * @return The Target <code>TextColor</code>
   */
  @Nonnull
  TextColor getTargetColor();

  /**
   * Getter for the <code>TextColor</code> of the Dimension in this <code>Alert</code>'s
   * message.
   *
   * @return The Dimension <code>TextColor</code>
   */
  @Nonnull
  TextColor getDimensionColor();
}
