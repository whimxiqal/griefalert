package com.minecraftonline.griefalert.storage;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.data.GriefEvent;
import com.minecraftonline.griefalert.api.records.GriefProfile;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;

public class MySQLProfileStorage {

  public boolean connect() throws Exception {
    // TODO: implement
    String address = GriefAlert.getInstance().getConfigHelper().getSqlDatabaseAddress();
    String username = GriefAlert.getInstance().getConfigHelper().getSqlUsername();
    String password = GriefAlert.getInstance().getConfigHelper().getSqlPassword();

    // ...
    return false;
  }

  public void createTable() throws Exception {
    // TODO: implement
  }

  public void write(GriefProfile profile) throws Exception {
    // TODO: implement
  }

  public boolean remove(GriefEvent griefEvent, String target) {
    // TODO: implement
    return false;
  }

  public List<GriefProfile> retrieve() {
    // TODO: implement
    return Collections.emptyList();
  }

  private Connection getConnection() {
    // TODO: implement
    return null;
  }

}
