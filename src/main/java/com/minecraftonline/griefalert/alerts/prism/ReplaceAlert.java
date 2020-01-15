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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.time.Instant;
import java.util.Date;
import java.util.regex.Pattern;

public class ReplaceAlert extends PrismAlert {

  private final String replacementBlockId;

  public ReplaceAlert(GriefProfile griefProfile,
                      PrismRecordArchived prismRecord,
                      String replacementBlockId) {
    super(griefProfile, prismRecord);
    this.replacementBlockId = replacementBlockId;
  }

  /**
   * Special constructor for <code>Text</code> for a <code>ReplaceAlert</code>.
   *
   * @return The <code>Text</code>
   */
  @Nonnull
  public Text.Builder getMessageTextBuilder() {
    Text.Builder builder = Text.builder();
    builder.append(Text.of(
        Format.playerName(getGriefer()),
        Format.space(),
        getEventColor(), "replaced",
        Format.space(),
        getTargetColor(), Grammar.addIndefiniteArticle(Format.item(getTarget())),
        Format.space(), "with",
        Format.space(),
        getTargetColor(), Grammar.addIndefiniteArticle(Format.item(replacementBlockId))));
    builder.append(Text.of(
        TextColors.RED, " in the ",
        getDimensionColor(), getGrieferTransform().getExtent().getDimension().getType().getName()));
    return builder;
  }

  @Override
  public void addConditionsTo(Query query) {
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
