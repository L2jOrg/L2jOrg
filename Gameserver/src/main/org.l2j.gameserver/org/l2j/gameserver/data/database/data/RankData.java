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
 public class RankData {

    @Column("id")
    private int playerId;

    @Column("name")
    private String playerName;
    private long exp;
    private byte level;

    @Column("class")
    private short classId;
    private byte race;

    @Column("clan_name")
    private String clanName;

    private int rank;

    @Column("rank_race")
    private int rankRace;

    @Column("rank_snapshot")
    private int rankSnapshot;

    @Column("rank_race_snapshot")
    private int rankRaceSnapshot;

    public int getPlayerId() {
        return playerId;
    }

    public int getRank() {
      return rank;
   }

   public int getRankRace() {
      return rankRace;
   }

    public String getPlayerName() {
        return playerName;
    }

    public long getExp() {
        return exp;
    }

    public byte getLevel() {
        return level;
    }

    public short getClassId() {
        return classId;
    }

    public byte getRace() {
        return race;
    }

    public String getClanName() {
        return clanName;
    }

    public int getRankSnapshot() {
        return rankSnapshot;
    }

    public int getRankRaceSnapshot() {
        return rankRaceSnapshot;
    }
}
