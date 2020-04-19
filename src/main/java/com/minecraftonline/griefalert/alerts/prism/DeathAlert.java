/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.prism;

import com.helion3.prism.api.records.PrismRecord;
import com.minecraftonline.griefalert.api.records.GriefProfile;

public class DeathAlert extends PrismAlert {

  public DeathAlert(GriefProfile griefProfile, PrismRecord prismRecord) {
    super(griefProfile, prismRecord);
  }

//  @Override
//  protected void addQueryConditionsTo(Query query) {
//    query.addCondition(FieldCondition.of(
//        DataQueries.Player,
//        MatchRule.EQUALS,
//        this.getGriefer().getUniqueId().toString()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Target,
//        MatchRule.EQUALS,
//        Pattern.compile(this.getTarget().replace('_', ' ').replace("minecraft:", ""))));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Created,
//        MatchRule.GREATER_THAN_EQUAL,
//        Date.from(this.getCreated().toInstant().minusSeconds(1))));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Created,
//        MatchRule.LESS_THAN_EQUAL,
//        Date.from(this.getCreated().toInstant().plusSeconds(1))));
//    query.addCondition(FieldCondition.of(
//        DataQueries.EventName,
//        MatchRule.EQUALS,
//        this.getGriefEvent().getId()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Location.then(DataQueries.WorldUuid),
//        MatchRule.EQUALS,
//        this.getGriefLocation().getExtent().getUniqueId().toString()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Location.then(DataQueries.X),
//        MatchRule.EQUALS,
//        this.getGriefLocation().getPosition().getFloorX()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Location.then(DataQueries.Y),
//        MatchRule.EQUALS,
//        this.getGriefLocation().getPosition().getFloorY()));
//    query.addCondition(FieldCondition.of(
//        DataQueries.Location.then(DataQueries.Z),
//        MatchRule.EQUALS,
//        this.getGriefLocation().getPosition().getFloorZ()));
//  }
}
