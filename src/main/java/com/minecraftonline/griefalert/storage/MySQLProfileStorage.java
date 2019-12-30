package com.minecraftonline.griefalert.storage;

import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.storage.MySQLProfileQuery;
import org.spongepowered.api.data.DataContainer;

import java.sql.Connection;
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

  public List<DataContainer> query(MySQLProfileQuery query) {
    // TODO: implement after implementing MySQLProfileQuery
    return null;
  }

  private Connection getConnection() {
    // TODO: implement
    return null;
  }

}
