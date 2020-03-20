package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.model.stats.BaseStats;

/**
 * @author JoeAlisson
 */
@Table("player_stats_points")
public class PlayerStatsData {

    @Column("player_id")
    private int playerId;
    private short points;
    private short strength;
    private short dexterity;
    private short constitution;
    private short intelligence;
    private short witness;
    private short mentality;

    public static PlayerStatsData init(int playerId) {
        var data = new PlayerStatsData();
        data.playerId = playerId;
        return data;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public short getPoints() {
        return points;
    }

    public void setPoints(short points) {
        this.points = points;
    }

    public boolean update(short str, short dex, short con, short intt, short wit, short men) {
        if(strength + dexterity + constitution + intelligence + witness + mentality + str + dex + con + intt + wit + men <= points) {
            strength += str;
            dexterity += dex;
            constitution += con;
            intelligence += intt;
            witness += wit;
            mentality += men;
            return true;
        }
        return false;
    }

    public void reset() {
        strength = dexterity = constitution = intelligence = witness = mentality = 0;
    }

    public int getValue(BaseStats stat) {
        return switch (stat) {
            case CON -> constitution;
            case DEX -> dexterity;
            case MEN -> mentality;
            case STR -> strength;
            case WIT -> witness;
            case INT -> intelligence;
        };
    }
}
