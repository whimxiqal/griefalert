/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.prism;

import com.helion3.prism.api.query.FieldCondition;
import com.helion3.prism.api.query.MatchRule;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.util.DataQueries;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;

import java.time.Instant;
import java.util.Date;
import java.util.regex.Pattern;


public class DeathAlert extends PrismAlert {

  public DeathAlert(GriefProfile griefProfile, PrismRecordArchived prismRecord) {
    super(griefProfile, prismRecord);
  }

  @Override
  protected void addConditionsTo(Query query) {
    query.addCondition(FieldCondition.of(
        DataQueries.Player,
        MatchRule.EQUALS,
        this.getGriefer().getUniqueId().toString()));
    query.addCondition(FieldCondition.of(
        DataQueries.Target,
        MatchRule.EQUALS,
        Pattern.compile(this.getTarget().replace('_', ' ').replace("minecraft:", ""))));
    query.addCondition(FieldCondition.of(
        DataQueries.Created,
        MatchRule.GREATER_THAN_EQUAL,
        this.getCreated()));
    query.addCondition(FieldCondition.of(
        DataQueries.Created,
        MatchRule.LESS_THAN_EQUAL,
        Date.from(Instant.ofEpochMilli(this.getCreated().getTime()
            + PRISM_BUFFER_MILLISECONDS))));
    query.addCondition(FieldCondition.of(
        DataQueries.EventName,
        MatchRule.EQUALS,
        this.getGriefEvent().getId()));
    query.addCondition(FieldCondition.of(
        DataQueries.Location.then(DataQueries.WorldUuid),
        MatchRule.EQUALS,
        this.getGriefLocation().getExtent().getUniqueId().toString()));
    query.addCondition(FieldCondition.of(
        DataQueries.Location.then(DataQueries.X),
        MatchRule.EQUALS,
        this.getGriefLocation().getPosition().getFloorX()));
    query.addCondition(FieldCondition.of(
        DataQueries.Location.then(DataQueries.Y),
        MatchRule.EQUALS,
        this.getGriefLocation().getPosition().getFloorY()));
    query.addCondition(FieldCondition.of(
        DataQueries.Location.then(DataQueries.Z),
        MatchRule.EQUALS,
        this.getGriefLocation().getPosition().getFloorZ()));
  }
}
