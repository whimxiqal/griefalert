package com.minecraftonline.griefalert.alerts.prism;

import com.helion3.prism.api.query.FieldCondition;
import com.helion3.prism.api.query.MatchRule;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.util.DataQueries;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.Grammar;

import javax.annotation.Nonnull;

import com.minecraftonline.griefalert.util.GriefEvents;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.time.Instant;
import java.util.Date;
import java.util.regex.Pattern;

public class ReplaceAlert extends PrismAlert {

  // the original block id is saved in getTarget()
  private final String replacementBlockId;

  public ReplaceAlert(GriefProfile griefProfile,
                      PrismRecordArchived prismRecord,
                      String replacementBlockId) {
    super(griefProfile, prismRecord);
    this.replacementBlockId = replacementBlockId;
    addSummaryContent("Replacement", Format.item(replacementBlockId));
    addSummaryContent("Tool", Format.item(getGriefer()
        .getItemInHand(HandTypes.MAIN_HAND)
        .map(itemStack -> itemStack.getType().getId())
        .orElse("none")));
  }

  @Override
  public void addQueryConditionsTo(Query query) {
    query.addCondition(FieldCondition.of(
        DataQueries.Player,
        MatchRule.EQUALS,
        this.getGriefer().getUniqueId().toString()));
    query.addCondition(FieldCondition.of(
        DataQueries.Target,
        MatchRule.EQUALS,
        Pattern.compile(this.replacementBlockId.replace('_', ' '))));
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
        GriefEvents.PLACE.getId()));
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
