package com.minecraftonline.griefalert.profiles.io;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.profiles.GriefProfile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.minecraftonline.griefalert.util.ProfileStorageKeys.*;

public class Importer {

  private final Path filePath;

  /**
   * A new Importer to scrape data from a specific file in the
   * plugin's data folder.
   *
   * @param fileName The name of the file in which the data is saved
   */
  public Importer(String fileName) {
    this.filePath = Paths.get(GriefAlert.getInstance().getDataDirectory().getPath(), fileName);
  }

  private File getFile() {
    return new File(filePath.toString());
  }

  /**
   * Scrapes all the information from the designated data file.
   *
   * @return A list of the saved objects
   */
  public List<GriefProfile> retrieve() {

    // TODO: Write a better retrieval program to account for a new Profile saving organization -> JSON

    // **USE STORAGE KEYS**

    // Example JSON storage:
    //    {
    //      "event":"break",
    //      "target":"minecraft:cobblestone",
    //      "ignoredDimensions":[
    //        "nether",
    //        "the_end"
    //      ],
    //      "colors": {
    //        "event":"light_blue",
    //        "target":"dark_purple",
    //        "dimension":"white"
    //      }
    //    }

    return new LinkedList<>();
//    List<GriefProfile> griefProfiles = new LinkedList<>();
//    try {
//      if (GriefAlert.getInstance().getDataDirectory().mkdirs() || getFile().createNewFile()) {
//        GriefAlert.getInstance().getLogger().info("New Grief Profiles file created");
//        return new LinkedList<>();
//      }
//      Scanner scanner = new Scanner(getFile());
//      while (scanner.hasNext()) {
//        StorageLine line = new StorageLine(scanner.nextLine());
//        if (line.hasData()) {
//          try {
//            griefProfiles.add(GriefProfiles.fromStorage(line));
//            GriefAlert.getInstance().getLogger().info("A new Grief Profile has been added: " + line);
//          } catch (IllegalArgumentException e) {
//            GriefAlert.getInstance().getLogger().error("An error occurred while reading this line. Line skipped: "
//                + line.toString());
//            GriefAlert.getInstance().getLogger().error(e.getMessage());
//          }
//        }
//      }
//      scanner.close();
//      GriefAlert.getInstance().getLogger().info("'" + filePath.getFileName() + "' processed");
//    } catch (Exception e) {
//      GriefAlert.getInstance().getLogger().error("Exception while reading " + filePath.getFileName(), e);
//    }
//    return griefProfiles;
  }

}
