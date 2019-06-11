package org.l2j.gameserver.handler;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.CHashIntObjectMap;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.data.database.dao.DailyMissionDAO;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.model.dailymission.DailyMissionPlayerData;
import org.l2j.gameserver.model.dailymission.DailyMissionStatus;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author Sdw
 */
public abstract class AbstractDailyMissionHandler extends ListenersContainer  {
    private final IntObjectMap<DailyMissionPlayerData> entries = new CHashIntObjectMap<>();
    private final DailyMissionDataHolder holder;
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    protected AbstractDailyMissionHandler(DailyMissionDataHolder holder) {
        this.holder = holder;
        init();
    }

    public DailyMissionDataHolder getHolder() {
        return holder;
    }

    public boolean isAvailable(L2PcInstance player) {
        var entry = getPlayerEntry(player.getObjectId(), false);
        if (nonNull(entry))
        {
            switch (entry.getStatus())
            {
                case NOT_AVAILABLE:
                {
                    if (entry.getProgress() >= getRequiredCompletition())
                    {
                        entry.setStatus(DailyMissionStatus.AVAILABLE);
                        storePlayerEntry(entry);
                    }
                    break;
                }
                case AVAILABLE:
                {
                    return true;
                }
            }
        }
        return false;
    }

    public abstract void init();

    public int getStatus(L2PcInstance player) {
        final DailyMissionPlayerData entry = getPlayerEntry(player.getObjectId(), false);
        return entry != null ? entry.getStatus().getClientId() : DailyMissionStatus.NOT_AVAILABLE.getClientId();
    }

    protected int getRequiredCompletition() {
        return holder.getRequiredCompletions();
    }

    public int getProgress(L2PcInstance player) {
        final DailyMissionPlayerData entry = getPlayerEntry(player.getObjectId(), false);
        return entry != null ? entry.getProgress() : 0;
    }

    public boolean getRecentlyCompleted(L2PcInstance player) {
        final DailyMissionPlayerData entry = getPlayerEntry(player.getObjectId(), false);
        return (entry != null) && entry.getRecentlyCompleted();
    }

    public synchronized void reset() {
        getDAO(DailyMissionDAO.class).deleteById(holder.getId());
        entries.clear();
    }

    public boolean requestReward(L2PcInstance player) {
        if (isAvailable(player)) {
            giveRewards(player);

            final DailyMissionPlayerData entry = getPlayerEntry(player.getObjectId(), true);
            entry.setStatus(DailyMissionStatus.COMPLETED);
            entry.setLastCompleted(System.currentTimeMillis());
            entry.setRecentlyCompleted(true);
            storePlayerEntry(entry);

            return true;
        }
        return false;
    }

    protected void giveRewards(L2PcInstance player) {
        holder.getRewards().forEach(i -> player.addItem("One Day Reward", i, player, true));
    }

    protected void storePlayerEntry(DailyMissionPlayerData entry) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("REPLACE INTO character_daily_missions(char_id, mission_id, status, progress, last_completed) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, entry.getObjectId());
            ps.setInt(2, entry.getRewardId());
            ps.setInt(3, entry.getStatus().getClientId());
            ps.setInt(4, entry.getProgress());
            ps.setLong(5, entry.getLastCompleted());
            ps.execute();

            // Cache if not exists
            entries.computeIfAbsent(entry.getObjectId(), id -> entry);
        } catch (Exception e) {
            LOGGER.warn("Error while saving reward " + entry.getRewardId() + " for player: " + entry.getObjectId() + " in database: ", e);
        }
    }

    protected DailyMissionPlayerData getPlayerEntry(int objectId, boolean createIfNone) {
        final DailyMissionPlayerData existingEntry = entries.get(objectId);
        if (existingEntry != null) {
            return existingEntry;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM character_daily_missions WHERE char_id = ? AND mission_id = ?")) {
            ps.setInt(1, objectId);
            ps.setInt(2, holder.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final DailyMissionPlayerData entry = new DailyMissionPlayerData(rs.getInt("charId"), rs.getInt("rewardId"), rs.getInt("status"), rs.getInt("progress"), rs.getLong("lastCompleted"));
                    entries.put(objectId, entry);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Error while loading reward " + holder.getId() + " for player: " + objectId + " in database: ", e);
        }

        if (createIfNone) {
            final DailyMissionPlayerData entry = new DailyMissionPlayerData(objectId, holder.getId());
            entries.put(objectId, entry);
            return entry;
        }
        return null;
    }
}
