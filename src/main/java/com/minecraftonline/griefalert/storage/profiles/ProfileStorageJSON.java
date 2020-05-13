/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert.storage.profiles;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.storage.ProfileStorage;
import com.minecraftonline.griefalert.util.enums.DefaultProfiles;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class ProfileStorageJSON implements ProfileStorage {

  private final File profilesFile = new File(GriefAlert.getInstance().getConfigDirectory(), "profiles.json");

  public ProfileStorageJSON() throws Exception {
    if (profilesFile.createNewFile()) {
      GriefAlert.getInstance().getLogger().info("Created grief profiles file");
      replaceAll(DefaultProfiles.GET_ALL());
    }
  }

  @Override
  public boolean write(@Nonnull GriefProfile profile) throws Exception {
    if (!profilesFile.exists()) {
      GriefAlert.getInstance().getLogger().error("Tried to save profiles, but could not find " + profilesFile.getName());
      return false;
    }
    GriefProfile toRemove = null;
    Collection<GriefProfile> profiles = retrieve();
    for (GriefProfile existing : profiles) {
      if (profile.equals(existing)) {
        return false;
      }
    }
    profiles.add(profile);
    replaceAll(profiles);
    return true;
  }

  private boolean replaceAll(@Nonnull Collection<GriefProfile> profiles) throws Exception {
    if (!profilesFile.exists()) {
      GriefAlert.getInstance().getLogger().error("Tried to save profiles, but could not find " + profilesFile.getName());
      return false;
    }
    String json = new GsonBuilder().setPrettyPrinting().create().toJson(profiles);
    try (FileWriter writer = new FileWriter(profilesFile)) {
      writer.write(json);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }

  @Override
  public boolean remove(@Nonnull GriefEvent griefEvent, @Nonnull String target) throws Exception {
    if (!profilesFile.exists()) {
      GriefAlert.getInstance().getLogger().error("Tried to save profiles, but could not find " + profilesFile.getName());
      return false;
    }
    GriefProfile toRemove = null;
    Collection<GriefProfile> profiles = retrieve();
    for (GriefProfile profile : profiles) {
      if (profile.getGriefEvent().equals(griefEvent) && profile.getTarget().equals(target)) {
        toRemove = profile;
        break;
      }
    }
    if (toRemove == null) {
      return false;
    } else {
      profiles.remove(toRemove);
      replaceAll(profiles);
      return true;
    }
  }

  @Nonnull
  @Override
  public Collection<GriefProfile> retrieve() throws Exception {
    if (!profilesFile.exists()) {
      GriefAlert.getInstance().getLogger().error("Tried to get profiles, but could not find " + profilesFile.getName());
      return Collections.emptyList();
    }
    try {
      GriefProfile[] profilesArray = new Gson().fromJson(new FileReader(profilesFile), GriefProfile[].class);
      return Lists.newArrayList(profilesArray);
    } catch (Exception e) {
      e.printStackTrace();
      return Collections.emptyList();
    }
  }
}
