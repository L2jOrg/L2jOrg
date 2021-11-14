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
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.commons.configuration.SettingsFile;
import org.l2j.commons.util.Rnd;

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
    private static boolean allowGuardAttackAggressiveMonster;
    private static float raidHpRegenMultiplier;
    private static float raidMpRegenMultiplier;
    private static float raidPDefenseMultiplier;
    private static float raidMDefenseMultiplier;
    private static float raidPAttackMultiplier;
    private static float raidMAttackMultiplier;
    private static int minionRespawnTime;
    private static IntIntMap customMinionRespawnTime;
    private static float raidRespawnMultiplier;
    private static boolean disableRaidCurse;
    private static long minionDespawnDelay;
    private static int raidChaosTime;
    private static int grandBossChaosTime;
    private static int minionChaosTime;
    private static int maxSlotsForPet;
    private static float petHpRegenMultiplier;
    private static float petMpRegenMultiplier;
    private static int vitalityConsumeByMonster;
    private static int vitalityConsumeByBoss;
    private static int limitBarrier;
    private static int antharasSpawnInterval;
    private static int antharasSpawnRandom;
    private static long baiumSpawnInterval;
    private static int coreSpawnInterval;
    private static int coreSpawnRandom;
    private static int orfenSpawnInterval;
    private static int orfenSpwanRandom;
    private static int queenAntSpawnInterval;
    private static int queenantSpawnRandom;
    private static int zakenSpawnInterval;
    private static int zakenSpawnRandom;
    private static int altharActivationChance;
    private static int maxActiveAlthars;
    private static int alharMinDuration;
    private static int altherMaxDuration;
    private static boolean isRaidBossZoneFlagEnabled;

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
        allowGuardAttackAggressiveMonster = settingsFile.getBoolean("GuardAttackAggroMob", false);

        raidHpRegenMultiplier = settingsFile.getFloat("RaidHpRegenMultiplier", 1);
        raidMpRegenMultiplier = settingsFile.getFloat("RaidMpRegenMultiplier", 1);
        raidPDefenseMultiplier = settingsFile.getFloat("RaidPDefenseMultiplier", 1);
        raidMDefenseMultiplier = settingsFile.getFloat("RaidMDefenseMultiplier", 1);
        raidPAttackMultiplier = settingsFile.getFloat("RaidPAttackMultiplier", 1);
        raidMAttackMultiplier = settingsFile.getFloat("RaidMAttackMultiplier", 1);

        minionRespawnTime = settingsFile.getInt("RaidMinionRespawnTime", 300000);
        customMinionRespawnTime = settingsFile.getIntIntMap("CustomMinionsRespawnTime", 1000);
        raidRespawnMultiplier = settingsFile.getFloat("RaidRespawnMultiplier", 1);
        minionDespawnDelay = settingsFile.getLong("DespawnDelayMinions", 20000);

        disableRaidCurse = settingsFile.getBoolean("DisableRaidCurse", true);
        raidChaosTime = settingsFile.getInt("RaidChaosTime", 10);
        grandBossChaosTime = settingsFile.getInt("GrandChaosTime", 10);
        minionChaosTime = settingsFile.getInt("MinionChaosTime", 10);

        maxSlotsForPet = settingsFile.getInt("MaximumSlotsForPet", 12);
        petHpRegenMultiplier = settingsFile.getFloat("PetHpRegenMultiplier", 1);
        petMpRegenMultiplier = settingsFile.getFloat("PetMpRegenMultiplier", 1);

        vitalityConsumeByMonster = settingsFile.getInt("VitalityConsumeByMob", 2250);
        vitalityConsumeByBoss = settingsFile.getInt("VitalityConsumeByBoss", 1125);
        limitBarrier = settingsFile.getInt("LimitBarrier", 500);

        antharasSpawnInterval = settingsFile.getInt("AntharasSpawnInterval", 264);
        antharasSpawnRandom = settingsFile.getInt("AntharasSpawnRandom", 72);

        baiumSpawnInterval = settingsFile.getInt("BaiumSpawnInterval", 168) * 3600000L;

        coreSpawnInterval = settingsFile.getInt("CoreSpawnInterval", 60);
        coreSpawnRandom = settingsFile.getInt("CoreSpawnRandom", 24);

        orfenSpawnInterval = settingsFile.getInt("OrfenSpawnInterval", 48);
        orfenSpwanRandom = settingsFile.getInt("OrfenSpawnRandom", 20);

        queenAntSpawnInterval = settingsFile.getInt("QueenAntSpawnInterval", 36);
        queenantSpawnRandom  = settingsFile.getInt("QueenAntSpawnRandom", 17);

        zakenSpawnInterval = settingsFile.getInt("ZakenSpawnInterval", 168);
        zakenSpawnRandom = settingsFile.getInt("ZakenSpawnRandom", 48);

        altharActivationChance = settingsFile.getInt("AltharsActivationChance", 70);
        maxActiveAlthars =  settingsFile.getInt("MaxActiveAlthars", 3);
        alharMinDuration = settingsFile.getInt("AltharsMinDuration", 240000);
        altherMaxDuration = settingsFile.getInt("AltharsMaxDuration", 480000);

        isRaidBossZoneFlagEnabled = settingsFile.getBoolean("EnableRaidBossZoneFlagEnable", false);
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
        return pveDamagePenalty.get(min(levelDiff, pveDamagePenalty.size() - 1));
    }

    public static double pveSkillChancePenaltyOf(int levelDiff) {
        return pveSkillChancePenalty.get(min(levelDiff, pveSkillChancePenalty.size() - 1));
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

    public static boolean allowGuardAttackAggressiveMonster() {
        return allowGuardAttackAggressiveMonster;
    }

    public static float raidHpRegenMultiplier() {
        return raidHpRegenMultiplier;
    }

    public static float raidMpRegenMultiplier() {
        return raidMpRegenMultiplier;
    }

    public static float raidPDefenseMultiplier() {
        return raidPDefenseMultiplier;
    }

    public static float raidMDefenseMultiplier() {
        return raidMDefenseMultiplier;
    }

    public static float raidPAttackMultiplier() {
        return raidPAttackMultiplier;
    }

    public static float raidMAttackMultiplier() {
        return raidMAttackMultiplier;
    }

    public static int minionRespawnTime() {
        return minionRespawnTime;
    }

    public static float raidRespawnMultiplier() {
        return raidRespawnMultiplier;
    }

    public static boolean disableRaidCurse() {
        return disableRaidCurse;
    }

    public static long minionDespawnDelay() {
        return minionDespawnDelay;
    }

    public static int raidChaosTime() {
        return raidChaosTime;
    }

    public static int grandBossChaosTime() {
        return grandBossChaosTime;
    }

    public static int minionChaosTime() {
        return minionChaosTime;
    }

    public static int maxSlotsForPet() {
        return maxSlotsForPet;
    }

    public static float petHpRegenMultiplier() {
        return petHpRegenMultiplier;
    }

    public static float petMpRegenMultiplier() {
        return petMpRegenMultiplier;
    }

    public static int vitalityConsumeByMonster() {
        return vitalityConsumeByMonster;
    }

    public static int vitalityConsumeByBoss() {
        return vitalityConsumeByBoss;
    }

    public static int limitBarrier() {
        return limitBarrier;
    }

    public static int minionRespawnTimeOf(int id) {
        return customMinionRespawnTime.getOrDefault(id, -1);
    }

    public static long antharasSpawnInterval() {
        return (antharasSpawnInterval + Rnd.get(-antharasSpawnRandom, antharasSpawnRandom)) * 3600000L;
    }

    public static long baiumSpawnInterval() {
        return baiumSpawnInterval;
    }

    public static long coreSpawnInterval() {
        return (coreSpawnInterval + Rnd.get(-coreSpawnRandom, coreSpawnRandom)) * 3600000L;
    }

    public static long orfenSpawnInterval() {
        return (orfenSpawnInterval + Rnd.get(-orfenSpwanRandom, orfenSpwanRandom)) * 3600000L;
    }

    public static long queenAntSpawnInterval() {
        return (queenAntSpawnInterval + Rnd.get(-queenantSpawnRandom, queenantSpawnRandom)) * 3600000L;
    }

    public static long zakenSpawnInterval() {
        return (zakenSpawnInterval + Rnd.get(-zakenSpawnRandom, zakenSpawnRandom)) * 3600000L ;
    }

    public static int altharActivationChance() {
        return altharActivationChance;
    }

    public static int maxActiveAlthars() {
        return maxActiveAlthars;
    }

    public static long altharDuration() {
        return Rnd.get(alharMinDuration, altherMaxDuration);
    }

    public static boolean isRaidBossZoneFlagEnabled() {
        return isRaidBossZoneFlagEnabled;
    }
}
