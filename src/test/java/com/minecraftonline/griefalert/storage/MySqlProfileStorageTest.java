package com.minecraftonline.griefalert.storage;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.GriefEvents;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import org.junit.Test;
import org.spongepowered.api.data.DataContainer;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

public class MySqlProfileStorageTest {

  @Test
  public void all() {

    MySqlProfileStorage storage = new MySqlProfileStorage();
    GriefProfile testProfile = GriefProfile.of(
        DataContainer.createNew()
            .set(GriefProfileDataQueries.TARGET, "test_target")
            .set(GriefProfileDataQueries.EVENT, GriefEvents.PLACE.getId())
            .set(GriefProfileDataQueries.IGNORE_NETHER, true)
            .set(GriefProfileDataQueries.TARGET_COLOR, "yellow")
            .set(GriefProfileDataQueries.EVENT_COLOR, "yellow")
            .set(GriefProfileDataQueries.DIMENSION_COLOR, "yellow"));

    // Check closing doesn't work before connecting
    try {
      assertFalse("Closing before connecting returned successful", storage.close());
    } catch (SQLException e) {
      fail("Closing an unconnected database threw a SQLException");
      e.printStackTrace();
      return;
    }

    // Connect to the database
    try {
      storage.connect("jdbc:sqlite:D:/Projects/MC/MCO/Test Server/griefalert/griefalert.db");
    } catch (SQLException e) {
      e.printStackTrace();
      fail("Connecting to the database threw a SQLException");
      return;
    }

    // Retrieve the profiles
    List<GriefProfile> firstRetrieve;
    try {
      firstRetrieve = storage.retrieve();
    } catch (SQLException e) {
      e.printStackTrace();
      fail("Retrieving the GriefProfiles from the database threw a SQLException");
      return;
    }

    // Remove the profile to add if it was added previously in a different run of the test
    try {
      storage.remove(testProfile.getGriefEvent(), testProfile.getTarget());  // ignore success
    } catch (SQLException e) {
      e.printStackTrace();
      fail("Removing a GriefProfile (which may or may not have existed) threw a SQLException");
      return;
    }

    // Add a profile
    try {
      assertTrue("Write method returned unsuccessful", storage.write(testProfile));
    } catch (SQLException e) {
      e.printStackTrace();
      fail("Writing a GriefProfile to the database threw a SQLException");
      return;
    }

    // Retrieve the profiles
    List<GriefProfile> secondRetrieve;
    try {
      secondRetrieve = storage.retrieve();
      assertNotSame("The size of the lists of GriefProfiles retrieved from the database "
              + "are not different after writing a new GriefProfile to the database",
          firstRetrieve.size(),
          secondRetrieve.size());
    } catch (SQLException e) {
      e.printStackTrace();
      fail("Retrieving the GriefProfiles after a new write to the database threw a SQLException");
      return;
    }

    // Remove the original profile
    try {
      assertTrue("Removing an existing GriefProfile returned unsuccessful",
          storage.remove(testProfile.getGriefEvent(), testProfile.getTarget()));  // ignore success
    } catch (SQLException e) {
      e.printStackTrace();
      fail("Removing an existing GriefProfile threw a SQLException");
      return;
    }

    try {
      assertFalse("Removing a non-existent GriefProfile returned successful",
          storage.remove(testProfile.getGriefEvent(), testProfile.getTarget()));  // ignore success
    } catch (SQLException e) {
      e.printStackTrace();
      fail("Removing a non-existent GriefProfile threw a SQLException");
      return;
    }

    // Close
    try {
      assertTrue("Storage closure return unsuccessful after connecting", storage.close());
    } catch (SQLException e) {
      e.printStackTrace();
      fail("Closing the database threw a SQLException");
      return;
    }

  }

}