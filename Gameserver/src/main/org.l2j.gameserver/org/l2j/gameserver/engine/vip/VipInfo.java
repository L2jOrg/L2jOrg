package org.l2j.gameserver.engine.vip;

/**
 * @author JoeAlisson
 */
class VipInfo {

    private final byte tier;
    private final long pointsRequired;
    private final long pointsDepreciated;
    private float silverCoinChance;
    private float rustyCoinChance;
    private int skill;

    VipInfo(byte tier, long pointsRequired, long pointsDepreciated) {
        this.tier = tier;
        this.pointsRequired = pointsRequired;
        this.pointsDepreciated = pointsDepreciated;
    }

    byte getTier() {
        return tier;
    }

    void setSilverCoinChance(float silverCoinChance) {
        this.silverCoinChance = silverCoinChance;
    }

    float getSilverCoinChance() {
        return silverCoinChance;
    }

    void setRustyCoinChance(float rustyCoinChance) {
        this.rustyCoinChance = rustyCoinChance;
    }

    float getRustyCoinChance() {
        return rustyCoinChance;
    }

    long getPointsRequired() {
        return pointsRequired;
    }

    long getPointsDepreciated() {
        return pointsDepreciated;
    }

    int getSkill() {
        return skill;
    }

    void setSkill(int skill) {
        this.skill = skill;
    }
}
