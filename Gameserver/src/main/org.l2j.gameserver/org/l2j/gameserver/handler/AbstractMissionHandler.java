package org.l2j.gameserver.handler;

import org.l2j.gameserver.data.database.dao.MissionDAO;
import org.l2j.gameserver.data.database.data.MissionPlayerData;
import org.l2j.gameserver.engine.mission.MissionData;
import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.engine.mission.MissionStatus;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.network.serverpackets.mission.ExConnectedTimeAndGettableReward;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.commons.util.Util.falseIfNullOrElse;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public abstract class AbstractMissionHandler extends ListenersContainer  {

    private final MissionDataHolder holder;

    protected AbstractMissionHandler(MissionDataHolder holder) {
        this.holder = holder;
        init();
    }

    public MissionDataHolder getHolder() {
        return holder;
    }

    public boolean isAvailable(Player player) {
        return holder.isDisplayable(player) && falseIfNullOrElse(getPlayerEntry(player, false), entry ->
             switch (entry.getStatus()) {
                case AVAILABLE -> true;
                case NOT_AVAILABLE -> {
                    if(entry.getProgress() >= getRequiredCompletion()) {
                        entry.setStatus(MissionStatus.AVAILABLE);
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
        return nonNull(entry) ? entry.getStatus().getClientId() : MissionStatus.NOT_AVAILABLE.getClientId();
    }

    protected int getRequiredCompletion() {
        return holder.getRequiredCompletions();
    }

    public int getProgress(Player player) {
        return zeroIfNullOrElse(getPlayerEntry(player, false), MissionPlayerData::getProgress);
    }

    public boolean isRecentlyCompleted(Player player) {
        return falseIfNullOrElse(getPlayerEntry(player, false), MissionPlayerData::isRecentlyCompleted);
    }

    public synchronized void reset() {
        getDAO(MissionDAO.class).deleteById(holder.getId());
        MissionData.getInstance().clearMissionData(holder.getId());
    }

    public void requestReward(Player player) {
        synchronized (holder) {
            if (isAvailable(player)) {
                final MissionPlayerData entry = getPlayerEntry(player, true);
                entry.setStatus(MissionStatus.COMPLETED);
                entry.setRecentlyCompleted(true);
                storePlayerEntry(entry);
                giveRewards(player);
            }
        }
    }

    private void giveRewards(Player player) {
        holder.getRewards().forEach(i -> player.addItem("One Day Reward", i, player, true));
    }

    protected void storePlayerEntry(MissionPlayerData entry) {
        MissionData.getInstance().storeMissionData(holder.getId(), entry);
        getDAO(MissionDAO.class).save(entry);
    }

    protected MissionPlayerData getPlayerEntry(Player player, boolean createIfNone) {

        final var playerMissions = MissionData.getInstance().getStoredMissionData(player);

        if (playerMissions.containsKey(holder.getId())) {
            return playerMissions.get(holder.getId());
        }

        MissionPlayerData missionData = getDAO(MissionDAO.class).findById(player.getObjectId(), holder.getId());

        if (isNull(missionData) && createIfNone) {
            missionData = new MissionPlayerData(player.getObjectId(), holder.getId());
            var progress = getProgress(player);
            missionData.setProgress(progress);
            missionData.setStatus(progress >= getRequiredCompletion() ? MissionStatus.AVAILABLE : MissionStatus.NOT_AVAILABLE);
        }
        MissionData.getInstance().storeMissionData(holder.getId(), missionData);
        return missionData;
    }

    protected void notifyAvailablesReward(Player player) {
        var playerMissions = MissionData.getInstance().getStoredMissionData(player).values();
        player.sendPacket(new ExConnectedTimeAndGettableReward((int) playerMissions.stream().filter(MissionPlayerData::isAvailable).count()));

    }
}
