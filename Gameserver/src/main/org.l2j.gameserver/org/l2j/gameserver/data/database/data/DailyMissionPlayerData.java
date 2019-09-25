package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.annotation.Transient;
import org.l2j.gameserver.model.dailymission.DailyMissionStatus;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
@Table("character_daily_missions")
public class DailyMissionPlayerData {

    @Column("char_id")
    private int playerObjectId;
    @Column("mission_id")
    private int missionId;

    private DailyMissionStatus status = DailyMissionStatus.NOT_AVAILABLE;
    private int progress;

    @Transient
    private boolean recentlyCompleted;

    public DailyMissionPlayerData() {
        // default
    }

    public DailyMissionPlayerData(int objectId, int missionId) {
        playerObjectId = objectId;
        this.missionId = missionId;
    }

    public int getObjectId() {
        return playerObjectId;
    }

    public DailyMissionStatus getStatus() {
        return status;
    }

    public void setStatus(DailyMissionStatus status) {
        this.status = status;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int increaseProgress() {
        return ++progress;
    }

    public boolean isRecentlyCompleted() {
        return recentlyCompleted;
    }

    public void setRecentlyCompleted(boolean recentlyCompleted) {
        this.recentlyCompleted = recentlyCompleted;
    }

    public int getMissionId() {
        return missionId;
    }

    public boolean isAvailable() {
        return status == DailyMissionStatus.AVAILABLE;
    }
}
