/*
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.settings;

import io.github.joealisson.primitive.IntDoubleMap;
import org.l2j.commons.configuration.SettingsFile;
import org.l2j.gameserver.util.GameUtils;

/**
 * @author JoeAlisson
 */
public class RateSettings  {

    private static float xp;
    private static float vitalityExpMul;
    private static int maxItemsVitality;
    private static float vitalityLoss;
    private static float vitalityGain;
    private static float sp;
    private static float partyXp;
    private static float partySp;
    private static float raidPointsReward;
    private static float extractable;
    private static int dropManor;
    private static float questDrop;
    private static float questReward;
    private static float questRewardXp;
    private static float questRewardSp;
    private static float questRewardAdena;
    private static boolean isQuestRewardMultipliersEnabled;
    private static float questRewardPotion;
    private static float questRewardScroll;
    private static float questRewardRecipe;
    private static float questRewardMaterial;
    private static float karmaLost;
    private static float karmaXpLost;
    private static float siegeGuardsPrice;
    private static int playerDropLimit;
    private static int playerDrop;
    private static int playerDropItem;
    private static int playerDropEquip;
    private static int playerDropWeapon;
    private static float petXp;
    private static int petFood;
    private static int karmaDropLimit;
    private static int karmaDrop;
    private static int karmaDropItem;
    private static int karmaDropEquip;
    private static int karmaDropWeapon;
    private static float deathDropAmount;
    private static float spoilAmount;
    private static float raidDropAmount;
    private static float deathDropChance;
    private static float spoilChance;
    private static float herbDropChance;
    private static float raidDropChance;
    private static IntDoubleMap dropAmountByItem;
    private static IntDoubleMap dropChanceByItem;
    private static int dropMinLevelDiff;
    private static int dropMaxLevelDiff;
    private static double dropMinLevelGapChance;

    private RateSettings() {
        // helper class
    }

    public static void load(SettingsFile settingsFile) {
        xp = settingsFile.getFloat("RateXp", 1f);
        vitalityExpMul = settingsFile.getFloat("RateVitalityExpMultiplier", 2);
        maxItemsVitality = settingsFile.getInt("VitalityMaxItemsAllowed", 999);
        vitalityLoss = settingsFile.getFloat("RateVitalityLost", 1f);
        vitalityGain = settingsFile.getFloat("RateVitalityGain", 1f);
        sp = settingsFile.getFloat("RateSp", 1);
        partyXp = settingsFile.getFloat("RatePartyXp", 1);
        partySp = settingsFile.getFloat("RatePartySp", 1);
        raidPointsReward = settingsFile.getFloat("RateRaidbossPointsReward", 1);
        extractable = settingsFile.getFloat("RateExtractable", 1);
        dropManor = settingsFile.getInt("RateDropManor", 1);
        questDrop = settingsFile.getFloat("RateQuestDrop", 1);
        questReward = settingsFile.getFloat("RateQuestReward", 1);
        questRewardXp = settingsFile.getFloat("RateQuestRewardXP", 1);
        questRewardSp = settingsFile.getFloat("RateQuestRewardSP", 1);
        questRewardAdena = settingsFile.getFloat("RateQuestRewardAdena", 1);
        isQuestRewardMultipliersEnabled = settingsFile.getBoolean("UseQuestRewardMultipliers", false);
        questRewardPotion = settingsFile.getFloat("RateQuestRewardPotion", 1);
        questRewardScroll = settingsFile.getFloat("RateQuestRewardScroll", 1);
        questRewardRecipe = settingsFile.getFloat("RateQuestRewardRecipe", 1);
        questRewardMaterial = settingsFile.getFloat("RateQuestRewardMaterial", 1);
        karmaLost = settingsFile.getFloat("RateKarmaLost", xp);
        karmaXpLost = settingsFile.getFloat("RateKarmaExpLost", 1);
        siegeGuardsPrice = settingsFile.getFloat("RateSiegeGuardsPrice", 1);
        playerDropLimit = settingsFile.getInt("PlayerDropLimit", 3);
        playerDrop = settingsFile.getInt("PlayerRateDrop", 5);
        playerDropItem = settingsFile.getInt("PlayerRateDropItem", 70);
        playerDropEquip = settingsFile.getInt("PlayerRateDropEquip", 25);
        playerDropWeapon = settingsFile.getInt("PlayerRateDropEquipWeapon", 5);
        petXp = settingsFile.getFloat("PetXpRate", 1);
        petFood = settingsFile.getInt("PetFoodRate", 1);
        karmaDropLimit = settingsFile.getInt("KarmaDropLimit", 10);
        karmaDrop = settingsFile.getInt("KarmaRateDrop", 70);
        karmaDropItem = settingsFile.getInt("KarmaRateDropItem", 50);
        karmaDropEquip = settingsFile.getInt("KarmaRateDropEquip", 40);
        karmaDropWeapon = settingsFile.getInt("KarmaRateDropEquipWeapon", 10);
        deathDropAmount = settingsFile.getFloat("DeathDropAmountMultiplier", 1);
        spoilAmount = settingsFile.getFloat("SpoilDropAmountMultiplier", 1);
        raidDropAmount = settingsFile.getFloat("RaidDropAmountMultiplier", 1);
        deathDropChance = settingsFile.getFloat("DeathDropChanceMultiplier", 1);
        spoilChance = settingsFile.getFloat("SpoilDropChanceMultiplier", 1);
        herbDropChance = settingsFile.getFloat("HerbDropChanceMultiplier", 1);
        raidDropChance = settingsFile.getFloat("RaidDropChanceMultiplier", 1);
        dropAmountByItem = settingsFile.getIntDoubleMap("DropAmountMultiplierByItemId", "");
        dropChanceByItem = settingsFile.getIntDoubleMap("DropChanceMultiplierByItemId", "");

        dropMinLevelDiff = settingsFile.getInt("DropItemMinLevelDifference", 5);
        dropMaxLevelDiff = settingsFile.getInt("DropItemMaxLevelDifference", 10);
        dropMinLevelGapChance = settingsFile.getDouble("DropItemMinLevelGapChance", 10);
    }

    public static float xp() {
        return xp;
    }

    public static void setXp(float value) {
        xp = value;
    }

    public static float vitalityExpMul() {
        return vitalityExpMul;
    }

    public static int maxItemsVitality() {
        return maxItemsVitality;
    }

    public static float vitalityLoss() {
        return vitalityLoss;
    }

    public static float vitalityGain() {
        return vitalityGain;
    }

    public static float sp() {
        return sp;
    }

    public static void sp(float value) {
        sp = value;
    }

    public static float partyXp() {
        return partyXp;
    }

    public static float partySp() {
        return partySp;
    }

    public static float raidPointsReward() {
        return raidPointsReward;
    }

    public static float extractable() {
        return extractable;
    }

    public static int dropManor() {
        return dropManor;
    }

    public static float questDrop() {
        return questDrop;
    }

    public static float questReward() {
        return questReward;
    }

    public static float questRewardXp() {
        return questRewardXp;
    }

    public static float questRewardSp() {
        return questRewardSp;
    }

    public static float questRewardAdena() {
        return questRewardAdena;
    }

    public static boolean isQuestRewardMultipliersEnabled() {
        return isQuestRewardMultipliersEnabled;
    }

    public static float questRewardPotion() {
        return questRewardPotion;
    }

    public static float questRewardScroll() {
        return questRewardScroll;
    }

    public static float questRewardRecipe() {
        return questRewardRecipe;
    }

    public static float questRewardMaterial() {
        return questRewardMaterial;
    }

    public static float karmaLost() {
        return karmaLost;
    }

    public static float karmaXpLost() {
        return karmaXpLost;
    }

    public static float siegeGuardsPrice() {
        return siegeGuardsPrice;
    }

    public static int playerDropLimit() {
        return playerDropLimit;
    }

    public static int playerDrop() {
        return playerDrop;
    }

    public static int playerDropItem() {
        return playerDropItem;
    }

    public static int playerDropEquip() {
        return playerDropEquip;
    }

    public static int playerDropWeapon() {
        return playerDropWeapon;
    }

    public static float petXp() {
        return petXp;
    }

    public static int petFood() {
        return petFood;
    }

    public static int karmaDropLimit() {
        return karmaDropLimit;
    }

    public static int karmaDrop() {
        return karmaDrop;
    }

    public static int karmaDropItem() {
        return karmaDropItem;
    }

    public static int karmaDropEquip() {
        return karmaDropEquip;
    }

    public static int karmaDropWeapon() {
        return karmaDropWeapon;
    }

    public static float deathDropAmount() {
        return deathDropAmount;
    }

    public static float spoilAmount() {
        return spoilAmount;
    }

    public static float raidDropAmount() {
        return raidDropAmount;
    }

    public static float deathDropChance() {
        return deathDropChance;
    }

    public static float spoilChance() {
        return spoilChance;
    }

    public static void spoilChance(float value) {
        spoilChance = value;
    }

    public static float herbDropChance() {
        return herbDropChance;
    }

    public static float raidDropChance() {
        return raidDropChance;
    }

    public static double dropAmountOf(int itemId) {
        return dropAmountByItem.getOrDefault(itemId, 1);
    }

    public static double dropChanceOf(int itemId) {
        return dropChanceByItem.getOrDefault(itemId, 1);
    }

    public static double levelGapDropChance(int levelDifference) {
        return GameUtils.map(levelDifference, -dropMinLevelDiff, dropMaxLevelDiff, dropMinLevelGapChance, 100.0);
    }
}
