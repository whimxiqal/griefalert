package com.minecraftonline.griefalert.profiles.io;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.records.GriefProfileOld;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Nonnull;

public class Exporter {

  // ------------------
  // CURRENTLY OBSOLETE
  // ------------------

  private final Path filePath;

  public Exporter(String fileName) {
    this.filePath = Paths.get(GriefAlert.getInstance().getDataDirectory().getPath(), fileName);
  }

  private File getFile() {
    return filePath.toFile();
  }

  public void store(@Nonnull GriefProfileOld profile) throws IOException {
    if (GriefAlert.getInstance().getDataDirectory().mkdirs() && getFile().createNewFile()) {
      GriefAlert.getInstance().getLogger().info("New Grief Profiles file created");
    }

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

  }
}
