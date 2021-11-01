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

import static java.lang.Math.min;

/**
 * @author JoeAlisson
 */
public class NpcSettings {

    private static boolean allowAggroInPeaceZone;
    private static boolean allowAttackNpc;
    private static boolean allowViewNpc;
    private static boolean showNpcLevel;
    private static IntDoubleMap pveDamagePenalty;
    private static IntDoubleMap pveSkillChancePenalty;
    private static int spoiledCorpseExtendTime;
    private static int corpseConsumeAllowedTimeBeforeDecay;
    private static int maxDriftRange;

    private NpcSettings() {
        // Helper class
    }

    public static void load(SettingsFile settingsFile) {
       allowAggroInPeaceZone = settingsFile.getBoolean("AltMobAgroInPeaceZone", true);
       allowAttackNpc = settingsFile.getBoolean("AltAttackableNpcs", true);
       allowViewNpc = settingsFile.getBoolean("AltGameViewNpc", false);
       showNpcLevel = settingsFile.getBoolean("ShowNpcLevel", false);
       pveDamagePenalty = settingsFile.getPositionalValueMap("DmgPenaltyForLvLDifferences", "0.8, 0.6, 0.5, 0.42, 0.36, 0.32, 0.28, 0.25");
       pveSkillChancePenalty = settingsFile.getPositionalValueMap("SkillChancePenaltyForLvLDifferences", "2.5, 3.0, 3.25, 3.5");
       spoiledCorpseExtendTime = settingsFile.getInt("SpoiledCorpseExtendTime", 10);
       corpseConsumeAllowedTimeBeforeDecay = settingsFile.getInt("CorpseConsumeSkillAllowedTimeBeforeDecay", 2);
       maxDriftRange = settingsFile.getInt("MaxDriftRange", 300);
    }

    public static boolean allowAggroInPeaceZone() {
        return allowAggroInPeaceZone;
    }

    public static boolean allowAttackNpc() {
        return allowAttackNpc;
    }

    public static boolean allowViewNpc() {
        return allowViewNpc;
    }

    public static boolean showNpcLevel() {
        return showNpcLevel;
    }

    public static double pveDamagePenaltyOf(int levelDiff) {
        return pveDamagePenalty.get(min(levelDiff, pveDamagePenalty.size() -1));
    }

    public static double pveSkillChancePenaltyOf(int levelDiff) {
        return pveSkillChancePenalty.get(min(levelDiff, pveSkillChancePenalty.size() -1));
    }

    public static int spoiledCorpseExtendTime() {
        return spoiledCorpseExtendTime;
    }

    public static int corpseConsumeAllowedTimeBeforeDecay() {
        return corpseConsumeAllowedTimeBeforeDecay;
    }

    public static int maxDriftRange() {
        return maxDriftRange;
    }
}
