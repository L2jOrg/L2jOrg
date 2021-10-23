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

import org.l2j.commons.configuration.SettingsFile;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.holders.ItemHolder;

import static org.l2j.commons.util.Util.isBetween;

/**
 * @author JoeAlisson
 */
public class ChampionSettings {

    private static boolean isEnabled;
    private static int frequency;
    private static String title;
    private static boolean showAura;
    private static int minLevel;
    private static int maxLevel;
    private static int hpMultiplier;
    private static float expSpMultiplier;
    private static float rewardChanceMultiplier;
    private static float rewardAmountMultiplier;
    private static float adenaChanceMultiplier;
    private static float adenaAmountMultiplier;
    private static float hpRegenMultiplier;
    private static float atkMultiplier;
    private static float atkSpeedMultiplier;
    private static int lowerLevelDropChance;
    private static int higherLevelDropChance;
    private static ItemHolder reward;
    private static boolean isVitalityEnabled;

    private ChampionSettings() {
        // Helper class
    }

    public static void load(SettingsFile settingsFile) {
        isEnabled = settingsFile.getBoolean("ChampionEnable", false);
        isVitalityEnabled = settingsFile.getBoolean("ChampionEnableVitality", false);
        frequency = settingsFile.getInt("ChampionFrequency", 5);
        title = settingsFile.getString("ChampionTitle", "Champion");
        showAura = settingsFile.getBoolean("ChampionAura", true);
        minLevel = settingsFile.getInt("ChampionMinLevel", 20);
        maxLevel = settingsFile.getInt("ChampionMaxLevel", 60);
        hpMultiplier = settingsFile.getInt("ChampionHp", 7);
        expSpMultiplier = settingsFile.getFloat("ChampionRewardsExpSp", 8);
        rewardChanceMultiplier = settingsFile.getFloat("ChampionRewardsChance", 8);
        rewardAmountMultiplier = settingsFile.getFloat("ChampionRewardsAmount", 1);
        adenaChanceMultiplier = settingsFile.getFloat("ChampionAdenasRewardsChance", 1);
        adenaAmountMultiplier = settingsFile.getFloat("ChampionAdenasRewardsAmount", 1);
        hpRegenMultiplier = settingsFile.getFloat("ChampionHpRegen", 1);
        atkMultiplier = settingsFile.getFloat("ChampionAtk", 1);
        atkSpeedMultiplier = settingsFile.getFloat("ChampionSpdAtk", 1);
        lowerLevelDropChance = settingsFile.getInt("ChampionRewardLowerLvlItemChance", 0);
        higherLevelDropChance = settingsFile.getInt("ChampionRewardHigherLvlItemChance", 100);

        var rewardId= settingsFile.getInt("ChampionRewardItemID", 6393);
        var rewardAmount = settingsFile.getInt("ChampionRewardItemQty", 1);
        reward = new ItemHolder(rewardId, rewardAmount);
    }

    public static boolean checkChampionChance(int level) {
        return isEnabled && isBetween(level, minLevel, maxLevel) && Rnd.chance(frequency);
    }

    public static boolean canDropItem(int killerLevel, int victimLevel) {
        if(killerLevel <= victimLevel) {
            return Rnd.chance(higherLevelDropChance);
        }
        return Rnd.chance(lowerLevelDropChance);
    }

    public static boolean isVitalityEnabled() {
        return isVitalityEnabled;
    }

    public static String title() {
        return title;
    }

    public static boolean showAura() {
        return showAura;
    }

    public static int hpMultiplier() {
        return hpMultiplier;
    }

    public static float expSpMultiplier() {
        return expSpMultiplier;
    }

    public static float rewardChanceMultiplier() {
        return rewardChanceMultiplier;
    }

    public static float rewardAmountMultiplier() {
        return rewardAmountMultiplier;
    }

    public static float adenaChanceMultiplier() {
        return adenaChanceMultiplier;
    }

    public static float adenaAmountMultiplier() {
        return adenaAmountMultiplier;
    }

    public static float hpRegenMultiplier() {
        return hpRegenMultiplier;
    }

    public static float atkMultiplier() {
        return atkMultiplier;
    }

    public static float atkSpeedMultiplier() {
        return atkSpeedMultiplier;
    }

    public static ItemHolder dropItem() {
        return reward;
    }
}
