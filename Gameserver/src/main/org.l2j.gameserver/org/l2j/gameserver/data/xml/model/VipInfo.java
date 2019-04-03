package org.l2j.gameserver.data.xml.model;

public class VipInfo {

    private final int level;
    private final int pointsRequired;
    private final int pointsDepreciated;
    private float xpSpBonus;
    private float itemDropBonus;
    private int worldChatBonus;
    private float deathPenaltyReduction;
    private float fishingXpBonus;
    private float pvEDamageBonus;
    private float pvPDamageBonus;
    private float silverCoinChance;
    private float rustyCoinChance;
    private boolean receiveDailyVIPBox;
    private int allCombatAttributeBonus;

    public VipInfo(Integer level, Integer pointsRequired, Integer pointsDepreciated) {
        this.level = level;
        this.pointsRequired = pointsRequired;
        this.pointsDepreciated = pointsDepreciated;
    }

    public void setXpSpBonus(float xpSp) {
        this.xpSpBonus = xpSp;
    }

    public void setItemDropBonus(float itemDrop) {
        itemDropBonus = itemDrop;
    }

    public void setWorldChatBonus(int worldChatBonus) {
        this.worldChatBonus = worldChatBonus;
    }

    public void setDeathPenaltyReduction(float deathPenaltyReduction) {
        this.deathPenaltyReduction = deathPenaltyReduction;
    }

    public void setFishingXpBonus(float fishingXpBonus) {
        this.fishingXpBonus = fishingXpBonus;
    }

    public void setPvEDamageBonus(Float pveDamage) {
        this.pvEDamageBonus = pveDamage;
    }

    public void setPvPDamageBonus(float pvPDamageBonus) {
        this.pvPDamageBonus = pvPDamageBonus;
    }

    public float getPvPDamageBonus() {
        return pvPDamageBonus;
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

    public void setReceiveDailyVIPBox(boolean receiveDailyVIPBox) {
        this.receiveDailyVIPBox = receiveDailyVIPBox;
    }

    public boolean getReceiveDailyVIPBox() {
        return receiveDailyVIPBox;
    }

    public void setAllCombatAttributeBonus(int allCombatAttributeBonus) {
        this.allCombatAttributeBonus = allCombatAttributeBonus;
    }

    public int getAllCombatAttributeBonus() {
        return allCombatAttributeBonus;
    }
}
