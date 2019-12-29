package com.minecraftonline.griefalert.storage;

import com.minecraftonline.griefalert.api.profiles.GriefProfile;
import com.minecraftonline.griefalert.api.storage.MySQLProfileQuery;
import org.spongepowered.api.data.DataContainer;

import java.sql.Connection;
import java.util.List;

public class MySQLProfileStorage {

  public boolean connect() throws Exception {
    // TODO: implement
    String address = sqlAddress();
    String username = sqlUsername();
    String password = sqlPassword();

    // ...
    return false;
  }

  public void createTable() throws Exception {
    // TODO: implement
  }

  public void write(GriefProfile profile) throws Exception {
    // TODO: implement
  }

  public List<DataContainer> query(MySQLProfileQuery query) {
    // TODO: implement after implementing MySQLProfileQuery
    return null;
  }

  private Connection getConnection() {
    // TODO: implement
    return null;
  }

  private String sqlAddress() {
    // TODO: implement... use ConfigHelper to get it from the configuration file
    return null;
  }

  private String sqlUsername() {
    // TODO: implement... use ConfigHelper to get it from the configuration file
    return null;
  }

  private String sqlPassword() {
    // TODO: implement... use ConfigHelper to get it from the configuration file
    return null;
  }

}
