package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;

public class RankHistoryData {

    @Column("id")
    private int playerId;
    private long exp;
    private int rank;
    private int date;

    public int getPlayerId() {
        return playerId;
    }

    public long getExp() {
        return exp;
    }

    public int getRank() {
        return rank;
    }

    public int getDate() {
        return date;
    }
}
