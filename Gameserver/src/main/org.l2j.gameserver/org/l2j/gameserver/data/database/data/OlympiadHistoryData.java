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

/**
 * @author JoeAlisson
 */
public class OlympiadHistoryData {

    public static final OlympiadHistoryData DEFAULT = new OlympiadHistoryData();
    @Column("player_id")
    private int playerId;
    private int server;
    private int cycle;

    @Column("class_id")
    private int classId;
    private int points;
    private int battles;

    @Column("battles_won")
    private int battlesOwn;

    @Column("battles_lost")
    private int battlesLost;

    @Column("overall_rank")
    private int overallRank;

    @Column("overall_count")
    private int overallCount;

    @Column("overall_class_rank")
    private int overallClassRank;

    @Column("overall_class_count")
    private int overallClassCount;

    @Column("server_class_rank")
    private int serverClassRank;

    @Column("server_class_count")
    private int serverClassCount;

    public int getClassId() {
        return classId;
    }

    public int getPoints() {
        return points;
    }

    public int getBattles() {
        return battles;
    }

    public int getBattlesOwn() {
        return battlesOwn;
    }

    public int getBattlesLost() {
        return battlesLost;
    }

    public int getOverallRank() {
        return overallRank;
    }

    public int getOverallCount() {
        return overallCount;
    }

    public int getOverallClassRank() {
        return overallClassRank;
    }

    public int getOverallClassCount() {
        return overallClassCount;
    }

    public int getServerClassRank() {
        return serverClassRank;
    }

    public int getServerClassCount() {
        return serverClassCount;
    }
}
