package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;

/**
 * @author JoeAlisson
 */
 public class RankData {

    private int charId;

    @Column("char_name")
    private String charName;
    private long exp;

    @Column("class")
    private short classId;
    private byte race;

    @Column("clanid")
    private int clanId;

    private long rank;

}
