package com.minecraftonline.griefalert.api.storage;

import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * An interface for persistent storage of {@link GriefProfile}s.
 *
 * @author PietElite
 */
public interface ProfileStorage {

  /**
   * Write a GriefProfile into persistent storage.
   *
   * @param profile The GriefProfile to add
   * @return false if a <code>GriefProfile</code> already exists too similar to the
   *         input <code>GriefProfile</code>
   * @throws Exception if error
   */
  boolean write(@Nonnull final GriefProfile profile) throws Exception;

  /**
   * Remove the {@link GriefProfile} from persistent storage.
   *
   * @param griefEvent The <code>GriefEvent</code> of this profile to remove
   * @param target     The target id of this profile to remove
   * @return false if a <code>GriefProfile</code> was not found
   * @throws Exception if error
   */
  boolean remove(@Nonnull final GriefEvent griefEvent, @Nonnull final String target)
      throws Exception;

  /**
   * Get all <code>GriefProfile</code>s saved in persistent storage.
   *
   * @return a list of <code>GriefProfile</code>s
   * @throws Exception if error
   */
  @Nonnull
  List<GriefProfile> retrieve() throws Exception;

}
