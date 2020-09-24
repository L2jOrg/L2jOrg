/*
 * Copyright © 2019-2020 L2JOrg
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

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;

/**
 * @author JoeAlisson
 */
public class CharacterSettings implements Settings {

    private int partyRange;
    private IntSet autoLootItems;
    private boolean autoLootRaid;
    private int raidLootPrivilegeTime;
    private boolean autoLoot;
    private boolean initialEquipEvent;
    private boolean delevel;
    private float weightLimitMultiplier;
    private boolean removeCastleCirclets;
    private boolean restoreSummonOnReconnect;
    private int minEnchantAnnounceWeapon;
    private int minEnchantAnnounceArmor;
    private float restoreCPPercent;
    private float restoreHPPercent;
    private float restoreMPPercent;
    private boolean autoLearnSkillEnabled;
    private boolean autoLearnSkillFSEnabled;
    private byte maxBuffs;
    private byte maxTriggeredBuffs;
    private byte maxDances;
    private boolean dispelDanceAllowed;
    private boolean storeDances;
    private boolean breakCast;
    private boolean breakBowAttack;
    private boolean magicFailureAllowed;
    private boolean breakStun;
    private int effectTickRatio;
    private boolean autoLootHerbs;
    private boolean pledgeSkillsItemNeeded;
    private boolean divineInspirationBookNeeded;
    private boolean vitalityEnabled;
    private boolean raidBossUseVitality;
    private int maxRunSpeed;
    private int maxPcritRate;
    private int maxMcritRate;
    private int maxPAtkSpeed;
    private int maxMAtkSpeed;
    private int maxEvasion;

    @Override
    public void load(SettingsFile settingsFile) {
        partyRange = settingsFile.getInteger("AltPartyRange", 1600);

        autoLoot = settingsFile.getBoolean("AutoLoot", false);
        autoLootItems = settingsFile.getIntSet("AutoLootItemIds", ",");
        autoLootRaid = settingsFile.getBoolean("AutoLootRaids", false);
        raidLootPrivilegeTime = settingsFile.getInteger("RaidLootRightsInterval", 900) * 1000;
        autoLootHerbs = settingsFile.getBoolean("AutoLootHerbs", false);

        initialEquipEvent = settingsFile.getBoolean("InitialEquipmentEvent", false);

        delevel = settingsFile.getBoolean("Delevel", true);

        weightLimitMultiplier = settingsFile.getFloat("AltWeightLimit", 1f);

        removeCastleCirclets = settingsFile.getBoolean("RemoveCastleCirclets", true);
        restoreSummonOnReconnect = settingsFile.getBoolean("RestoreSummonOnReconnect", true);

        minEnchantAnnounceWeapon = settingsFile.getInteger("MinimumEnchantAnnounceWeapon", 7);
        minEnchantAnnounceArmor = settingsFile.getInteger("MinimumEnchantAnnounceArmor", 6);

        restoreCPPercent = settingsFile.getInteger("RespawnRestoreCP", 0) / 100f;
        restoreHPPercent = settingsFile.getInteger("RespawnRestoreHP", 65) / 100f;
        restoreMPPercent = settingsFile.getInteger("RespawnRestoreMP", 0) / 100f;

        autoLearnSkillEnabled = settingsFile.getBoolean("AutoLearnSkills", false);
        autoLearnSkillFSEnabled = settingsFile.getBoolean("AutoLearnForgottenScrollSkills", false);
        pledgeSkillsItemNeeded = settingsFile.getBoolean("PledgeSkillsItemNeeded", true);
        divineInspirationBookNeeded = settingsFile.getBoolean("DivineInspirationSpBookNeeded", true);

        maxBuffs = settingsFile.getByte("MaxBuffAmount", (byte) 20);
        maxTriggeredBuffs = settingsFile.getByte("MaxTriggeredBuffAmount", (byte) 12);
        maxDances = settingsFile.getByte("MaxDanceAmount", (byte) 12);
        dispelDanceAllowed = settingsFile.getBoolean("DanceCancelBuff", false);
        storeDances = settingsFile.getBoolean("AltStoreDances", false);
        effectTickRatio = settingsFile.getInteger("EffectTickRatio", 666);

        var cancelAttackType = settingsFile.getString("AltGameCancelByHit", "Cast");
        breakCast = cancelAttackType.equalsIgnoreCase("Cast") || cancelAttackType.equalsIgnoreCase("all");
        breakBowAttack = cancelAttackType.equalsIgnoreCase("Bow") || cancelAttackType.equalsIgnoreCase("all");
        breakStun = settingsFile.getBoolean("BreakStun", true);
        magicFailureAllowed = settingsFile.getBoolean("MagicFailures", true);

        vitalityEnabled = settingsFile.getBoolean("EnableVitality", false);
        raidBossUseVitality = settingsFile.getBoolean("RaidbossUseVitality", false);

        maxRunSpeed = settingsFile.getInteger("MaxRunSpeed", 300);
        maxPcritRate = settingsFile.getInteger("MaxPCritRate", 500);
        maxMcritRate = settingsFile.getInteger("MaxMCritRate", 200);
        maxPAtkSpeed = settingsFile.getInteger("MaxPAtkSpeed", 1500);
        maxMAtkSpeed = settingsFile.getInteger("MaxMAtkSpeed", 1999);
        maxEvasion = settingsFile.getInteger("MaxEvasion", 250);
    }

    public int partyRange() {
        return partyRange;
    }

    public boolean autoLoot() {
        return autoLoot;
    }

    public boolean isAutoLoot(int item) {
        return autoLootItems.contains(item);
    }

    public boolean autoLootRaid() {
        return autoLootRaid;
    }

    public int raidLootPrivilegeTime() {
        return raidLootPrivilegeTime;
    }

    public boolean autoLootHerbs() {
        return autoLootHerbs;
    }

    public boolean initialEquipEvent() {
        return initialEquipEvent;
    }

    public boolean delevel() {
        return delevel;
    }

    public float weightLimitMultiplier() {
        return weightLimitMultiplier;
    }

    public boolean removeCastleCirclets() {
        return removeCastleCirclets;
    }

    public boolean restoreSummonOnReconnect() {
        return restoreSummonOnReconnect;
    }

    public int minEnchantAnnounceWeapon() {
        return minEnchantAnnounceWeapon;
    }

    public int minEnchantAnnounceArmor() {
        return minEnchantAnnounceArmor;
    }

    public float restoreCPPercent() {
        return restoreCPPercent;
    }

    public float restoreHPPercent() {
        return restoreHPPercent;
    }

    public float restoreMPPercent() {
        return restoreMPPercent;
    }

    public boolean isAutoLearnSkillEnabled() {
        return autoLearnSkillEnabled;
    }

    public boolean isPledgeSkillsItemNeeded() {
        return pledgeSkillsItemNeeded;
    }

    public boolean isDivineInspirationBookNeeded() {
        return divineInspirationBookNeeded;
    }

    public boolean isAutoLearnSkillFSEnabled() {
        return autoLearnSkillFSEnabled;
    }

    public byte maxBuffs() {
        return maxBuffs;
    }

    public byte maxTriggeredBuffs() {
        return maxTriggeredBuffs;
    }

    public byte maxDances() {
        return maxDances;
    }

    public boolean isDispelDanceAllowed() {
        return dispelDanceAllowed;
    }

    public boolean storeDances() {
        return storeDances;
    }

    public boolean breakCast() {
        return breakCast;
    }

    public boolean breakBowAttack() {
        return breakBowAttack;
    }

    public boolean breakStun() {
        return breakStun;
    }

    public boolean isMagicFailureAllowed() {
        return magicFailureAllowed;
    }

    public int effectTickRatio() {
        return effectTickRatio;
    }

    public boolean isVitalityEnabled() {
        return vitalityEnabled;
    }

    public boolean raidBossUseVitality() {
        return raidBossUseVitality;
    }

    public int maxRunSpeed() {
        return maxRunSpeed;
    }

    public int maxPcritRate() {
        return maxPcritRate;
    }

    public int maxMcritRate() {
        return maxMcritRate;
    }

    public int maxPAtkSpeed() {
        return maxPAtkSpeed;
    }

    public int maxMAtkSpeed() {
        return maxMAtkSpeed;
    }

    public int maxEvasion() {
        return maxEvasion;
    }
}