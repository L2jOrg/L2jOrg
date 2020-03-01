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
