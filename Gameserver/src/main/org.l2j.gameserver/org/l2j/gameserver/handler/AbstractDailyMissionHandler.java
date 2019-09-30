package org.l2j.gameserver.handler;

import org.l2j.gameserver.data.database.dao.DailyMissionDAO;
import org.l2j.gameserver.data.database.data.DailyMissionPlayerData;
import org.l2j.gameserver.data.xml.impl.DailyMissionData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.model.dailymission.DailyMissionStatus;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.network.serverpackets.dailymission.ExConnectedTimeAndGettableReward;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.falseIfNullOrElse;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public abstract class AbstractDailyMissionHandler extends ListenersContainer  {

    private final DailyMissionDataHolder holder;

    protected AbstractDailyMissionHandler(DailyMissionDataHolder holder) {
        this.holder = holder;
        init();
    }

    public DailyMissionDataHolder getHolder() {
        return holder;
    }

    public boolean isAvailable(Player player) {
        return holder.isDisplayable(player) && falseIfNullOrElse(getPlayerEntry(player, false), entry ->
             switch (entry.getStatus()) {
                case AVAILABLE -> true;
                case NOT_AVAILABLE -> {
                    if(entry.getProgress() >= getRequiredCompletion()) {
                        entry.setStatus(DailyMissionStatus.AVAILABLE);
                        storePlayerEntry(entry);
                        yield  true;
                    }
                    yield false;
                }
                default -> false;
            }
        );
    }

    public abstract void init();

    public int getStatus(Player player) {
        final var entry = getPlayerEntry(player, false);
        return nonNull(entry) ? entry.getStatus().getClientId() : DailyMissionStatus.NOT_AVAILABLE.getClientId();
    }

    protected int getRequiredCompletion() {
        return holder.getRequiredCompletions();
    }

    public int getProgress(Player player) {
        return zeroIfNullOrElse(getPlayerEntry(player, false), DailyMissionPlayerData::getProgress);
    }

    public boolean isRecentlyCompleted(Player player) {
        return falseIfNullOrElse(getPlayerEntry(player, false), DailyMissionPlayerData::isRecentlyCompleted);
    }

    public synchronized void reset() {
        getDAO(DailyMissionDAO.class).deleteById(holder.getId());
        DailyMissionData.getInstance().clearMissionData(holder.getId());
    }

    public void requestReward(Player player) {
        synchronized (holder) {
            if (isAvailable(player)) {
                final DailyMissionPlayerData entry = getPlayerEntry(player, true);
                entry.setStatus(DailyMissionStatus.COMPLETED);
                entry.setRecentlyCompleted(true);
                storePlayerEntry(entry);
                giveRewards(player);
            }
        }
    }

    private void giveRewards(Player player) {
        holder.getRewards().forEach(i -> player.addItem("One Day Reward", i, player, true));
    }

    protected void storePlayerEntry(DailyMissionPlayerData entry) {
        DailyMissionData.getInstance().storeMissionData(holder.getId(), entry);
        getDAO(DailyMissionDAO.class).save(entry);
    }

    protected DailyMissionPlayerData getPlayerEntry(Player player, boolean createIfNone) {

        final var playerMissions = DailyMissionData.getInstance().getStoredDailyMissionData(player);

        if (playerMissions.containsKey(holder.getId())) {
            return playerMissions.get(holder.getId());
        }

        DailyMissionPlayerData missionData = getDAO(DailyMissionDAO.class).findById(player.getObjectId(), holder.getId());

        if (isNull(missionData) && createIfNone) {
            missionData = new DailyMissionPlayerData(player.getObjectId(), holder.getId());
            var progress = getProgress(player);
            missionData.setProgress(progress);
            missionData.setStatus(progress >= getRequiredCompletion() ? DailyMissionStatus.AVAILABLE : DailyMissionStatus.NOT_AVAILABLE);
        }
        DailyMissionData.getInstance().storeMissionData(holder.getId(), missionData);
        return missionData;
    }

    protected void notifyAvailablesReward(Player player) {
        var playerMissions = DailyMissionData.getInstance().getStoredDailyMissionData(player).values();
        player.sendPacket(new ExConnectedTimeAndGettableReward((int) playerMissions.stream().filter(DailyMissionPlayerData::isAvailable).count()));

    }
}
