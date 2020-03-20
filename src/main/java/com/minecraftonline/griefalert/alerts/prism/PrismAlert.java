/* Created by PietElite */

package com.minecraftonline.griefalert.alerts.prism;

import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.query.FieldCondition;
import com.helion3.prism.api.query.MatchRule;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.query.Sort;
import com.helion3.prism.api.records.Actionable;
import com.helion3.prism.api.records.Result;
import com.helion3.prism.util.DataQueries;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.AbstractAlert;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.api.records.PrismRecordArchived;
import com.minecraftonline.griefalert.util.PrismUtil;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


/**
 * <code>Alert</code> for all which derive from Prism.
 */
public abstract class PrismAlert extends AbstractAlert {

  static final long PRISM_BUFFER_MILLISECONDS = 2000L;

  private final PrismRecordArchived prismRecord;
  private final Transform<World> grieferTransform;
  private boolean reversed = false;

  PrismAlert(GriefProfile griefProfile, PrismRecordArchived prismRecord) {
    super(griefProfile);
    this.prismRecord = prismRecord;
    // Immediately set the transform of the griefer upon triggering the Alert
    grieferTransform = prismRecord.getDataContainer().getString(DataQueries.Player).flatMap(
        (s) ->
            Sponge.getServer()
                .getPlayer(UUID.fromString(s))
                .map(Player::getTransform)).orElseThrow(RuntimeException::new);
  }

  PrismRecordArchived getPrismRecord() {
    return prismRecord;
  }

  @Nonnull
  @Override
  public Transform<World> getGrieferTransform() {
    return grieferTransform;
  }

  @Nonnull
  @Override
  public Player getGriefer() {
    Optional<String> uuidOptional = prismRecord.getDataContainer().getString(DataQueries.Player);
    if (!uuidOptional.isPresent()) {
      GriefAlert.getInstance().getLogger().error("Could not get griefer UUID "
          + "from PrismRecord in a PrismAlert");
      GriefAlert.getInstance().getLogger().error(prismRecord.getDataContainer().toString());
      throw new NoSuchElementException();
    }
    Optional<Player> playerOptional = Sponge
        .getServer()
        .getPlayer(UUID.fromString(uuidOptional.get()));
    if (!playerOptional.isPresent()) {
      GriefAlert.getInstance()
          .getLogger()
          .error("Could not find player using UUID: " + uuidOptional.get());
      throw new NoSuchElementException();
    }

    return playerOptional.get();
  }

  @Nonnull
  @Override
  public Location<World> getGriefLocation() {
    return PrismUtil.getLocation(getPrismRecord())
        .map(location -> location.add(0.5, 0.2, 0.5))
        .orElseThrow(() ->
            new RuntimeException("Couldn't find the location in a PrismAlert"));
  }

  @Nonnull
  @Override
  public Date getCreated() {
    return PrismUtil.getCreated(this.prismRecord).orElse(Date.from(Instant.EPOCH));
  }

  /**
   * Rollback the event which caused this alert.
   *
   * @param src the source of the rollback request
   * @return whether rollback was successful
   */
  public final boolean rollback(@Nonnull CommandSource src) {
    AtomicBoolean success = new AtomicBoolean(false);
    QuerySession session = new QuerySession(src);
    session.setSortBy(Sort.NEWEST_FIRST);
    session.addFlag(Flag.NO_GROUP);
    try {
      Query query = session.newQuery();
      this.addQueryConditionsTo(query);

      // Iterate query results
      CompletableFuture<List<Result>> futureResults = com.helion3.prism.Prism.getInstance()
          .getStorageAdapter()
          .records().query(session, false);
      futureResults.thenAccept(results -> {
        if (results.isEmpty()) {
          GriefAlert.getInstance().getLogger().error(String.format(
              "Rollback query by %s return no results.",
              src.getName()));
        } else {
          try {
            // Iterate record results
            for (Result result : results) {
              if (result instanceof Actionable) {
                ((Actionable) result).rollback();
                reversed = true;
                success.set(true);
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
    }
    return success.get();
  }

  protected void addQueryConditionsTo(Query query) {
    query.addCondition(FieldCondition.of(
        DataQueries.Player,
        MatchRule.EQUALS,
        this.getGriefer().getUniqueId().toString()));
    query.addCondition(FieldCondition.of(
        DataQueries.Target,
        MatchRule.EQUALS,
        Pattern.compile(this.getTarget().replace('_', ' '))));
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

  public boolean isReversed() {
    return reversed;
  }
}
