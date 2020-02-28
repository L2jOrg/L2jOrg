package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;

/**
 * @author JoeAlisson
 */
 public class RankData {

    @Column("char_id")
    private int charId;

    @Column("char_name")
    private String charName;
    private long exp;

    @Column("class")
    private short classId;
    private byte race;

    @Column("clan_id")
    private int clanId;

    private int rank;

    @Column("rank_race")
    private int rankRace;


   public int getRank() {
      return rank;
   }

   public int getRankRace() {
      return rankRace;
   }
}
