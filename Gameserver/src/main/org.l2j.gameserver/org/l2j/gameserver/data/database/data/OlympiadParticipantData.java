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
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author JoeAlisson
 */
@Table("olympiad_participants")
public class OlympiadParticipantData {

    @Column("player_id")
    private int playerId;

    @Column("battles_won")
    private short battlesWon;

    @Column("battles_lost")
    private short battlesLost;

    @Column("battles_today")
    private short battlesToday;
    private short battles;
    private short points;
    private int server;

    public short getBattlesWon() {
        return battlesWon;
    }

    public short getBattlesLost() {
        return battlesLost;
    }

    public short getBattlesToday() {
        return battlesToday;
    }

    public short getBattles() {
        return battles;
    }

    public short getPoints() {
        return points;
    }

    public static OlympiadParticipantData of(Player player, short points, int server) {
        var data = new OlympiadParticipantData();
        data.playerId = player.getObjectId();
        data.points = points;
        data.server = server;
        return data;
    }

    public void updatePoints(int points) {
        this.points = (short) Math.max(0, this.points + points);
    }

    public void increaseBattlesToday() {
        battlesToday++;
        battles++;
    }

    public void increaseDefeats() {
        battlesLost++;
    }

    public void increaseVictory() {
        battlesWon++;
    }

    public void resetBattlesToday() {
        battlesToday = 0;
    }
}
