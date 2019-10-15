package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.annotation.Transient;
import org.l2j.gameserver.model.dailymission.MissionStatus;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
@Table("character_daily_missions")
public class MissionPlayerData {

    @Column("char_id")
    private int playerObjectId;
    @Column("mission_id")
    private int missionId;

    private MissionStatus status = MissionStatus.NOT_AVAILABLE;
    private int progress;

    @Transient
    private boolean recentlyCompleted;

    public MissionPlayerData() {
        // default
    }

    public MissionPlayerData(int objectId, int missionId) {
        playerObjectId = objectId;
        this.missionId = missionId;
    }

    public int getObjectId() {
        return playerObjectId;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
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
        return status == MissionStatus.AVAILABLE;
    }
}
