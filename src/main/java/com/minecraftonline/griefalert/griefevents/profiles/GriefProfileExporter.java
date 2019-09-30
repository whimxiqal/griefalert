package com.minecraftonline.griefalert.griefevents.profiles;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.griefevents.GriefEvent;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GriefProfileExporter {
  private final GriefAlert plugin;
  private final Path filePath;

  /**
   * A new Exporter to save data from the game into text files.
   * The information is saved in the EinsteinsWorkshopEDU data folder.
   *
   * @param plugin           The instance of the current plugin
   * @param fileName         The name of the file in which the data is saved
   */
  public GriefProfileExporter(GriefAlert plugin, String fileName) {
    this.plugin = plugin;
    this.filePath = Paths.get(plugin.getDataDirectory().getPath(), fileName);
  }

  private File getFile() {
    return filePath.toFile();
  }

  /**
   * Store a list of elements into the text file associated with this Exporter.
   * This method removes all previous data!
   *
   * @param profile A Grief Profile to put into storage
   */
  public void store(@Nonnull GriefProfile profile) throws IOException {
    if (plugin.getDataDirectory().mkdirs() || getFile().createNewFile()){
      plugin.getLogger().info("New Grief Profiles file created");
    }
    OutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(filePath.toFile(), true);
      GriefProfileStorageLine storageLine = profile.toStorageLine();
      storageLine.setComment("Added from in-game builder");
      String line = "\n".concat(storageLine.toString());
      outputStream.write(line.getBytes(), 0, line.length());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (outputStream != null) {
          outputStream.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
