package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

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

    public short getStrength() {
        return strength;
    }

    public void setStrength(short strength) {
        this.strength = strength;
    }

    public short getDexterity() {
        return dexterity;
    }

    public void setDexterity(short dexterity) {
        this.dexterity = dexterity;
    }

    public short getConstitution() {
        return constitution;
    }

    public void setConstitution(short constitution) {
        this.constitution = constitution;
    }

    public short getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(short intelligence) {
        this.intelligence = intelligence;
    }

    public short getWitness() {
        return witness;
    }

    public void setWitness(short witness) {
        this.witness = witness;
    }

    public short getMentality() {
        return mentality;
    }

    public void setMentality(short mentality) {
        this.mentality = mentality;
    }
}
