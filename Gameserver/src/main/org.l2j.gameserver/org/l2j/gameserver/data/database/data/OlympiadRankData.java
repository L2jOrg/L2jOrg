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
import org.l2j.commons.database.annotation.Table;

/**
 * @author JoeAlisson
 */
@Table("olympiad_rankers")
public class OlympiadRankData {

    @Column("player_id")
    private int playerId;
    private int server;

    @Column("player_name")
    private String name;

    @Column("clan_name")
    private String clanName;
    private int rank;

    @Column("previous_rank")
    private int previousRank;
    private int level;

    @Column("class_id")
    private int classId;

    @Column("clan_level")
    private int clanLevel;

    @Column("battles_won")
    private int battlesWon;

    @Column("battles_lost")
    private int battlesLost;
    private int points;

    @Column("hero_count")
    private int heroCount;

    @Column("legend_count")
    private int legendCount;

    public int getServer() {
        return server;
    }

    public String getName() {
        return name;
    }

    public String getClanName() {
        return clanName;
    }

    public int getRank() {
        return rank;
    }

    public int getPreviousRank() {
        return previousRank;
    }

    public int getLevel() {
        return level;
    }

    public int getClassId() {
        return classId;
    }

    public int getClanLevel() {
        return clanLevel;
    }

    public int getBattlesWon() {
        return battlesWon;
    }

    public int getBattlesLost() {
        return battlesLost;
    }

    public int getPoints() {
        return points;
    }

    public int getHeroCount() {
        return heroCount;
    }

    public int getLegendCount() {
        return legendCount;
    }
}
