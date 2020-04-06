package com.minecraftonline.griefalert.api.alerts;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nonnull;


public class AlertCheck implements Serializable {

  private final UUID officerUuid;
  private final Date checked;

  public AlertCheck(@Nonnull UUID officerUuid, @Nonnull Date checked) {
    this.officerUuid = officerUuid;
    this.checked = checked;
  }

  public Date getChecked() {
    return checked;
  }

  public UUID getOfficerUuid() {
    return officerUuid;
  }
}
