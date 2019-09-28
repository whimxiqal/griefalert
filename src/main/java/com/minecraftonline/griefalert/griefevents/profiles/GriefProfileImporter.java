package com.minecraftonline.griefalert.griefevents.profiles;

import com.minecraftonline.griefalert.GriefAlert;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class GriefProfileImporter {

  private final GriefAlert plugin;
  private final Path filePath;

  /**
   * A new Importer to scrape data from a specific file in the
   * plugin's data folder.
   *
   * @param plugin   The instance of the current plugin
   * @param fileName The name of the file in which the data is saved
   */
  public GriefProfileImporter(GriefAlert plugin, String fileName) {
    this.plugin = plugin;
    this.filePath = Paths.get(plugin.getDataDirectory().getPath(), fileName);
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
    List<GriefProfile> griefEvents = new LinkedList<>();
    try {
      if (plugin.getDataDirectory().mkdirs() || getFile().createNewFile()){
        plugin.getLogger().info("New Grief Profiles file created");
        return new LinkedList<>();
      }
      Scanner scanner = new Scanner(getFile());
      while (scanner.hasNext()) {
        GriefProfileStorageLine line = new GriefProfileStorageLine(scanner.nextLine());
        if (line.hasData()) {
          try {
            griefEvents.add(new GriefProfile(line));
            plugin.getLogger().info("A new Grief Profile has been added: " + line);
          } catch (IllegalArgumentException e) {
            plugin.getLogger().error("An error occurred while reading this line. Line skipped: " + line.toString());
            plugin.getLogger().error(e.getMessage());
          }
        }
      }
      scanner.close();
      plugin.getLogger().info("'" + filePath.getFileName() + "' processed");
    } catch (Exception e) {
      plugin.getLogger().error("Exception while reading " + filePath.getFileName(), e);
    }
    return griefEvents;
  }

}
