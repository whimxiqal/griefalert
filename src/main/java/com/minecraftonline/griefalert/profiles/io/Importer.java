package com.minecraftonline.griefalert.profiles.io;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.minecraftonline.griefalert.GriefAlert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.GriefEvents;
import com.minecraftonline.griefalert.util.GriefProfileDataQueries;
import org.spongepowered.api.data.DataContainer;

public class Importer {

  private final Path filePath;

  public Importer(Path filePath) {
    this.filePath = filePath;
  }

  private File getFile() {
    return new File(filePath.toString());
  }

  /**
   * Scrapes all the information from the designated data file.
   *
   * @return A list of the saved objects
   */
  public List<GriefProfile> retrieve() throws FileNotFoundException {

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

    // parsing file "JSONExample.json"

    List<GriefProfile> output = new LinkedList<>();

    for (JsonElement element : (new JsonParser()).parse(new JsonReader(new FileReader(filePath.toFile()))).getAsJsonArray()) {

      DataContainer dataContainer = DataContainer.createNew();

      String event = "break";
      try {
        dataContainer.set(GriefProfileDataQueries.EVENT, GriefEvents.Registry.of(event));
      } catch (IllegalArgumentException e) {
        GriefAlert.getInstance().getLogger().debug("A GriefProfile could not be loaded "
            + "because of an invalid event: " + event);

      }

      output.add(GriefProfile.of(dataContainer));

    }


    return output;
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
