package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("character_spirits")
public class ElementalSpiritData {

    private int charId;
    private byte type;
    private byte level = 1;
    private byte stage = 1;
    private long experience;

    @Column("attack_points")
    private byte attackPoints;

    @Column("defense_points")
    private byte defensePoints;

    @Column("crit_rate_points")
    private byte critRatePoints;

    @Column("crit_damage_points")
    private byte critDamagePoints;

    @Column("in_use")
    private boolean inUse;


    public ElementalSpiritData() {
        // default
    }

    public ElementalSpiritData(byte type, int objectId) {
        this.charId = objectId;
        this.type = type;
    }


    public int getCharId() {
        return charId;
    }

    public void setCharId(int charId) {
        this.charId = charId;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public byte getStage() {
        return stage;
    }

    public void setStage(byte stage) {
        this.stage = stage;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public byte getAttackPoints() {
        return attackPoints;
    }

    public void setAttackPoints(byte attackPoints) {
        this.attackPoints = attackPoints;
    }

    public byte getDefensePoints() {
        return defensePoints;
    }

    public void setDefensePoints(byte defensePoints) {
        this.defensePoints = defensePoints;
    }

    public byte getCritRatePoints() {
        return critRatePoints;
    }

    public void setCritRatePoints(byte critRatePoints) {
        this.critRatePoints = critRatePoints;
    }

    public byte getCritDamagePoints() {
        return critDamagePoints;
    }

    public void setCritDamagePoints(byte critDamagePoints) {
        this.critDamagePoints = critDamagePoints;
    }

    public void addExperience(long experience) {
        this.experience += experience;
    }

    public void increaseLevel() {
        level++;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void addAttackPoints(byte attackPoints) {
        this.attackPoints += attackPoints;
    }

    public void addDefensePoints(byte defensePoints) {
        this.defensePoints += defensePoints;
    }

    public void addCritRatePoints(byte critRatePoints) {
        this.critRatePoints = critRatePoints;
    }

    public void addCritDamagePoints(byte critDamagePoints) {
        this.critDamagePoints += critDamagePoints;
    }

    public void increaseStage() {
        stage++;
    }
}
