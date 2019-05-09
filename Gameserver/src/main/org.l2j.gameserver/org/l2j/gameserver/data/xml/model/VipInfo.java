package org.l2j.gameserver.data.xml.model;

public class VipInfo {

    private final byte tier;
    private final long pointsRequired;
    private final long pointsDepreciated;
    private float silverCoinChance;
    private float rustyCoinChance;
    private int skill;

    public VipInfo(byte tier, long pointsRequired, long pointsDepreciated) {
        this.tier = tier;
        this.pointsRequired = pointsRequired;
        this.pointsDepreciated = pointsDepreciated;
    }

    public byte getTier() {
        return tier;
    }

    public void setSilverCoinChance(float silverCoinChance) {
        this.silverCoinChance = silverCoinChance;
    }

    public float getSilverCoinChance() {
        return silverCoinChance;
    }

    public void setRustyCoinChance(float rustyCoinChance) {
        this.rustyCoinChance = rustyCoinChance;
    }

    public float getRustyCoinChance() {
        return rustyCoinChance;
    }

    public long getPointsRequired() {
        return pointsRequired;
    }

    public long getPointsDepreciated() {
        return pointsDepreciated;
    }

    public int getSkill() {
        return skill;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }
}
