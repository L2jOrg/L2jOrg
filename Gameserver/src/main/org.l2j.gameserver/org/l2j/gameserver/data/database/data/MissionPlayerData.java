/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.engine.mission.MissionStatus;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
@Table("character_missions")
public class MissionPlayerData {

    @Column("char_id")
    private int playerObjectId;
    @Column("mission_id")
    private int missionId;

    private MissionStatus status = MissionStatus.NOT_AVAILABLE;
    private int progress;

    @NonUpdatable
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
