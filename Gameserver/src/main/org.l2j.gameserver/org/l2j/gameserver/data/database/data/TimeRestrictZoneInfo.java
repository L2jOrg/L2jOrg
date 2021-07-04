/*
 * Copyright © 2019-2021 L2JOrg
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
import org.l2j.gameserver.world.zone.type.TimeRestrictZone;

/**
 * @author JoeAlisson
 */
@Table("player_time_restrict_zones")
public class TimeRestrictZoneInfo {

    private int zone;
    @Column("player_id")
    private int playerId;
    @Column("remaining_time")
    private int remainingTime;
    @Column("recharged_time")
    private int rechargedTime;

    @NonUpdatable
    private long lastRemainingTimeUpdate;

    public void updateRemainingTime() {
        if(lastRemainingTimeUpdate > 0) {
            var currentTime = System.currentTimeMillis();
            remainingTime -= (currentTime - lastRemainingTimeUpdate)  / 1000.0;
            lastRemainingTimeUpdate = currentTime;
        }
    }

    public int remainingTime() {
        return remainingTime;
    }

    public void rechargeTime(int recharge, int zoneMaxRecharge) {
        var rechargeable = Math.min(zoneMaxRecharge - rechargedTime, recharge);
        rechargedTime += rechargeable;
        remainingTime += rechargeable;
    }

    public int getRechargedTime() {
        return rechargedTime;
    }

    public void setLastRemainingTimeUpdate(long lastRemainingTimeUpdate) {
        this.lastRemainingTimeUpdate = lastRemainingTimeUpdate;
    }

    public static TimeRestrictZoneInfo init(TimeRestrictZone timeRestrictZone, int playerId) {
        var info = new TimeRestrictZoneInfo();
        info.remainingTime = timeRestrictZone.getTime();
        info.zone = timeRestrictZone.getId();
        info.playerId = playerId;
        return info;
    }
}
