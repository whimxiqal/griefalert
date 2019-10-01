package com.minecraftonline.griefalert.griefevents.profiles;

import com.minecraftonline.griefalert.GriefAlert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Nonnull;

class GriefProfileExporter {
  private final GriefAlert plugin;
  private final Path filePath;

  GriefProfileExporter(GriefAlert plugin, String fileName) {
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
  void store(@Nonnull GriefProfile profile) throws IOException {
    if (plugin.getDataDirectory().mkdirs() || getFile().createNewFile()) {
      plugin.getLogger().info("New Grief Profiles file created");
    }
    OutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(filePath.toFile(), true);
      GriefProfileStorageLine storageLine = profile.toStorageLine();
      storageLine.addBuilderStamp();
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
