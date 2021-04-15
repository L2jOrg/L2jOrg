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

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.Arrays;

import static java.lang.Math.max;

/**
 * @author JoeAlisson
 */
public class CharacterSettings implements Settings {

    public int partyRange;
    public boolean autoLootRaid;
    public int raidLootPrivilegeTime;
    public boolean autoLoot;
    public boolean initialEquipEvent;
    public boolean delevel;
    public float weightLimitMultiplier;
    public boolean removeCastleCirclets;
    public boolean restoreSummonOnReconnect;
    public int minEnchantAnnounceWeapon;
    public int minEnchantAnnounceArmor;
    public float restoreCPPercent;
    public float restoreHPPercent;
    public float restoreMPPercent;
    public boolean autoLearnSkillEnabled;
    public boolean autoLearnSkillFSEnabled;
    public byte maxBuffs;
    public byte maxTriggeredBuffs;
    public byte maxDances;
    public boolean dispelDanceAllowed;
    public boolean storeDances;
    public boolean breakCast;
    public boolean breakBowAttack;
    public boolean magicFailureAllowed;
    public boolean breakStun;
    public int effectTickRatio;
    public boolean autoLootHerbs;
    public boolean pledgeSkillsItemNeeded;
    public boolean divineInspirationBookNeeded;
    public boolean vitalityEnabled;
    public boolean raidBossUseVitality;
    public int maxRunSpeed;
    public int maxPcritRate;
    public int maxMcritRate;
    public int maxPAtkSpeed;
    public int maxMAtkSpeed;
    public int maxEvasion;
    public boolean teleportInBattle;
    public boolean craftEnabled;
    public long maxAdena;
    public boolean allowPKTeleport;
    public int maxFreeTeleportLevel;
    public int maxItemInPacket;
    public int maxSlotsQuestItem;
    public int clanMaxWarehouseSlot;
    public int maxSlotFreight;
    public int freightPrice;
    public boolean canAttackPkInPeaceZone;
    public boolean canPkShop;
    public boolean canPkTeleport;
    public boolean canPkTrade;
    public boolean canPkUseWareHouse;

    private IntSet autoLootItems;

    private int dwarfMaxSlotStoreSell;
    private int maxSlotStoreSell;
    private int dwarfMaxSlotStoreBuy;
    private int maxSlotStoreBuy;
    private int maxSlots;
    private int dwarfMaxSlots;
    private int gmMaxSlots;
    private int dwarfMaxSlotWarehouse;
    private int maxSlotWarehouse;
    private int[] nonAugmentedItems;


    @Override
    public void load(SettingsFile settingsFile) {
        partyRange = settingsFile.getInteger("AltPartyRange", 1600);

        autoLoot = settingsFile.getBoolean("AutoLoot", false);
        autoLootItems = settingsFile.getIntSet("AutoLootItemIds", ",");
        autoLootRaid = settingsFile.getBoolean("AutoLootRaids", false);
        raidLootPrivilegeTime = settingsFile.getInteger("RaidLootRightsInterval", 900) * 1000;
        autoLootHerbs = settingsFile.getBoolean("AutoLootHerbs", false);
        maxAdena = settingsFile.getLong("MaxAdena", Long.MAX_VALUE);
        if(maxAdena < 1) {
            maxAdena = Long.MAX_VALUE;
        }

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

        teleportInBattle = settingsFile.getBoolean("TeleportInBattle", true);
        allowPKTeleport = settingsFile.getBoolean("AltKarmaPlayerCanTeleport", true);
        maxFreeTeleportLevel = settingsFile.getInteger("MaxFreeTeleportLevel", 40);

        craftEnabled = settingsFile.getBoolean("CraftingEnabled", true);

        dwarfMaxSlotStoreSell = settingsFile.getInteger("MaxPvtStoreSellSlotsDwarf", 4);
        maxSlotStoreSell = settingsFile.getInteger("MaxPvtStoreSellSlotsOther", 3);
        dwarfMaxSlotStoreBuy = settingsFile.getInteger("MaxPvtStoreBuySlotsDwarf", 5);
        maxSlotStoreBuy = settingsFile.getInteger("MaxPvtStoreBuySlotsOther",  4);

        maxSlots = settingsFile.getInteger("MaximumSlotsForNoDwarf", 80);
        dwarfMaxSlots = settingsFile.getInteger("MaximumSlotsForDwarf", 100);
        gmMaxSlots = settingsFile.getInteger("MaximumSlotsForGMPlayer", 100);
        maxSlotsQuestItem = settingsFile.getInteger("MaximumSlotsForQuestItems", 80);

        dwarfMaxSlotWarehouse = settingsFile.getInteger("MaximumWarehouseSlotsForDwarf", 120);
        maxSlotWarehouse = settingsFile.getInteger("MaximumWarehouseSlotsForNoDwarf", 100);
        clanMaxWarehouseSlot = settingsFile.getInteger("MaximumWarehouseSlotsForClan", 150);

        maxSlotFreight = settingsFile.getInteger("MaximumFreightSlots", 200);
        freightPrice = settingsFile.getInteger("FreightPrice", 1000);

        maxItemInPacket = max(maxSlots, max(dwarfMaxSlots, gmMaxSlots));

        nonAugmentedItems = settingsFile.getIntArray("AugmentationBlackList", "6660,6661,6662");
        Arrays.sort(nonAugmentedItems);

        canAttackPkInPeaceZone = settingsFile.getBoolean("CanAttackPkInPeaceZone", false);
        canPkShop = settingsFile.getBoolean("CanPkShop", true);
        canPkTeleport = settingsFile.getBoolean("CanPkTeleport", false);
        canPkTrade = settingsFile.getBoolean("CanPkTrade", true);
        canPkUseWareHouse = settingsFile.getBoolean("CanPKUseWareHouse", true);
    }


    public boolean isAutoLoot(int item) {
        return autoLootItems.contains(item);
    }

    public int maxSlotStoreBuy(Race race) {
        return race == Race.DWARF ? dwarfMaxSlotStoreBuy : maxSlotStoreBuy;
    }
    public int maxSlotStoreSell(Race race) {
        return race == Race.DWARF ? dwarfMaxSlotStoreSell : maxSlotStoreSell;
    }

    public int maxSlotInventory(Player player) {
        if(player.isGM()) {
            return gmMaxSlots;
        }
        return player.getRace() == Race.DWARF ? dwarfMaxSlots : maxSlots;
    }

    public int maxSlotWarehouse(Race race) {
        return race == Race.DWARF ? dwarfMaxSlotWarehouse : maxSlotWarehouse;
    }

    public boolean canBeAugmented(int itemId) {
        return Arrays.binarySearch(nonAugmentedItems, itemId) < 0;
    }
}