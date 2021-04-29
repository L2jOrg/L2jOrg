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
import org.l2j.commons.configuration.SettingsFile;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.Arrays;

import static java.lang.Math.max;

/**
 * @author JoeAlisson
 */
public class CharacterSettings  {

    public static boolean autoLootRaid;
    public static int raidLootPrivilegeTime;
    public static boolean autoLoot;
    public static boolean initialEquipEvent;
    public static boolean delevel;
    public static float weightLimitMultiplier;
    public static boolean removeCastleCirclets;
    public static boolean restoreSummonOnReconnect;
    public static int minEnchantAnnounceWeapon;
    public static int minEnchantAnnounceArmor;
    public static float restoreCPPercent;
    public static float restoreHPPercent;
    public static float restoreMPPercent;
    public static boolean autoLearnSkillEnabled;
    public static boolean autoLearnSkillFSEnabled;
    public static byte maxBuffs;
    public static byte maxTriggeredBuffs;
    public static byte maxDances;
    public static boolean dispelDanceAllowed;
    public static boolean storeDances;
    public static boolean breakCast;
    public static boolean breakBowAttack;
    public static boolean magicFailureAllowed;
    public static boolean breakStun;
    public static int effectTickRatio;
    public static boolean autoLootHerbs;
    public static boolean pledgeSkillsItemNeeded;
    public static boolean divineInspirationBookNeeded;
    public static boolean vitalityEnabled;
    public static boolean raidBossUseVitality;
    public static int maxRunSpeed;
    public static int maxPcritRate;
    public static int maxMcritRate;
    public static int maxPAtkSpeed;
    public static int maxMAtkSpeed;
    public static int maxEvasion;
    public static boolean teleportInBattle;
    public static boolean craftEnabled;
    public static long maxAdena;
    public static boolean allowPKTeleport;
    public static int maxFreeTeleportLevel;
    public static int maxItemInPacket;
    public static int maxSlotsQuestItem;
    public static int clanMaxWarehouseSlot;
    public static int maxSlotFreight;
    public static int freightPrice;
    public static boolean canAttackPkInPeaceZone;
    public static boolean canPkShop;
    public static boolean canPkTeleport;
    public static boolean canPkTrade;
    public static boolean canPkUseWareHouse;
    public static int maxFame;
    public static int fameTaskDelay;
    public static int fameTaskPoints;
    public static boolean fameForDeadPlayers;
    public static int criticalCraftRate;
    public static int dwarfRecipeLimit;
    public static int recipeLimit;
    public static boolean altGameCreation;
    public static double altGameCreationSpeed;
    public static double altGameCreationXpRate;
    public static double altGameCreationSpRate;
    public static double altGameCreationRareXpSpRate;

    private static IntSet autoLootItems;

    private static int dwarfMaxSlotStoreSell;
    private static int maxSlotStoreSell;
    private static int dwarfMaxSlotStoreBuy;
    private static int maxSlotStoreBuy;
    private static int maxSlots;
    private static int dwarfMaxSlots;
    private static int gmMaxSlots;
    private static int dwarfMaxSlotWarehouse;
    private static int maxSlotWarehouse;
    private static int[] nonAugmentedItems;
    private static byte startLevel;
    private static int startSP;

    public static void load(SettingsFile settingsFile) {
        startLevel = settingsFile.getByte("StartingLevel", (byte) 1);
        startSP = settingsFile.getInt("StartingSP", 0);
        autoLoot = settingsFile.getBoolean("AutoLoot", false);
        autoLootItems = settingsFile.getIntSet("AutoLootItemIds", ",");
        autoLootRaid = settingsFile.getBoolean("AutoLootRaids", false);
        raidLootPrivilegeTime = settingsFile.getInt("RaidLootRightsInterval", 900) * 1000;
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

        minEnchantAnnounceWeapon = settingsFile.getInt("MinimumEnchantAnnounceWeapon", 7);
        minEnchantAnnounceArmor = settingsFile.getInt("MinimumEnchantAnnounceArmor", 6);

        restoreCPPercent = settingsFile.getInt("RespawnRestoreCP", 0) / 100f;
        restoreHPPercent = settingsFile.getInt("RespawnRestoreHP", 65) / 100f;
        restoreMPPercent = settingsFile.getInt("RespawnRestoreMP", 0) / 100f;

        autoLearnSkillEnabled = settingsFile.getBoolean("AutoLearnSkills", false);
        autoLearnSkillFSEnabled = settingsFile.getBoolean("AutoLearnForgottenScrollSkills", false);
        pledgeSkillsItemNeeded = settingsFile.getBoolean("PledgeSkillsItemNeeded", true);
        divineInspirationBookNeeded = settingsFile.getBoolean("DivineInspirationSpBookNeeded", true);

        maxBuffs = settingsFile.getByte("MaxBuffAmount", (byte) 20);
        maxTriggeredBuffs = settingsFile.getByte("MaxTriggeredBuffAmount", (byte) 12);
        maxDances = settingsFile.getByte("MaxDanceAmount", (byte) 12);
        dispelDanceAllowed = settingsFile.getBoolean("DanceCancelBuff", false);
        storeDances = settingsFile.getBoolean("AltStoreDances", false);
        effectTickRatio = settingsFile.getInt("EffectTickRatio", 666);

        var cancelAttackType = settingsFile.getString("AltGameCancelByHit", "Cast");
        breakCast = cancelAttackType.equalsIgnoreCase("Cast") || cancelAttackType.equalsIgnoreCase("all");
        breakBowAttack = cancelAttackType.equalsIgnoreCase("Bow") || cancelAttackType.equalsIgnoreCase("all");
        breakStun = settingsFile.getBoolean("BreakStun", true);
        magicFailureAllowed = settingsFile.getBoolean("MagicFailures", true);

        vitalityEnabled = settingsFile.getBoolean("EnableVitality", false);
        raidBossUseVitality = settingsFile.getBoolean("RaidbossUseVitality", false);

        maxRunSpeed = settingsFile.getInt("MaxRunSpeed", 300);
        maxPcritRate = settingsFile.getInt("MaxPCritRate", 500);
        maxMcritRate = settingsFile.getInt("MaxMCritRate", 200);
        maxPAtkSpeed = settingsFile.getInt("MaxPAtkSpeed", 1500);
        maxMAtkSpeed = settingsFile.getInt("MaxMAtkSpeed", 1999);
        maxEvasion = settingsFile.getInt("MaxEvasion", 250);

        teleportInBattle = settingsFile.getBoolean("TeleportInBattle", true);
        allowPKTeleport = settingsFile.getBoolean("AltKarmaPlayerCanTeleport", true);
        maxFreeTeleportLevel = settingsFile.getInt("MaxFreeTeleportLevel", 40);

        craftEnabled = settingsFile.getBoolean("CraftingEnabled", true);

        dwarfMaxSlotStoreSell = settingsFile.getInt("MaxPvtStoreSellSlotsDwarf", 4);
        maxSlotStoreSell = settingsFile.getInt("MaxPvtStoreSellSlotsOther", 3);
        dwarfMaxSlotStoreBuy = settingsFile.getInt("MaxPvtStoreBuySlotsDwarf", 5);
        maxSlotStoreBuy = settingsFile.getInt("MaxPvtStoreBuySlotsOther",  4);

        maxSlots = settingsFile.getInt("MaximumSlotsForNoDwarf", 80);
        dwarfMaxSlots = settingsFile.getInt("MaximumSlotsForDwarf", 100);
        gmMaxSlots = settingsFile.getInt("MaximumSlotsForGMPlayer", 100);
        maxSlotsQuestItem = settingsFile.getInt("MaximumSlotsForQuestItems", 80);

        dwarfMaxSlotWarehouse = settingsFile.getInt("MaximumWarehouseSlotsForDwarf", 120);
        maxSlotWarehouse = settingsFile.getInt("MaximumWarehouseSlotsForNoDwarf", 100);
        clanMaxWarehouseSlot = settingsFile.getInt("MaximumWarehouseSlotsForClan", 150);

        maxSlotFreight = settingsFile.getInt("MaximumFreightSlots", 200);
        freightPrice = settingsFile.getInt("FreightPrice", 1000);

        maxItemInPacket = max(maxSlots, max(dwarfMaxSlots, gmMaxSlots));

        nonAugmentedItems = settingsFile.getIntArray("AugmentationBlackList", "6660,6661,6662");
        Arrays.sort(nonAugmentedItems);

        canAttackPkInPeaceZone = settingsFile.getBoolean("CanAttackPkInPeaceZone", false);
        canPkShop = settingsFile.getBoolean("CanPkShop", true);
        canPkTeleport = settingsFile.getBoolean("CanPkTeleport", false);
        canPkTrade = settingsFile.getBoolean("CanPkTrade", true);
        canPkUseWareHouse = settingsFile.getBoolean("CanPKUseWareHouse", true);

        maxFame = settingsFile.getInt("MaxPersonalFamePoints", 100000);
        fameTaskDelay = settingsFile.getInt("CastleZoneFameTaskFrequency", 300);
        fameTaskPoints = settingsFile.getInt("CastleZoneFameAcquirePoints", 125);
        fameForDeadPlayers = settingsFile.getBoolean("FameForDeadPlayers", true);

        criticalCraftRate = settingsFile.getInt("BaseCriticalCraftRate", 3);
        dwarfRecipeLimit = settingsFile.getInt("DwarfRecipeLimit", 100);
        recipeLimit = settingsFile.getInt("CommonRecipeLimit", 50);

        altGameCreation = settingsFile.getBoolean("AltGameCreation", false);
        altGameCreationSpeed = settingsFile.getDouble("AltGameCreationSpeed", 1);
        altGameCreationXpRate = settingsFile.getDouble("AltGameCreationXpRate", 1);
        altGameCreationSpRate = settingsFile.getDouble("AltGameCreationSpRate", 1);
        altGameCreationRareXpSpRate = settingsFile.getDouble("AltGameCreationRareXpSpRate", 2);
    }


    public static boolean isAutoLoot(int item) {
        return autoLootItems.contains(item);
    }

    public static int maxSlotStoreBuy(Race race) {
        return race == Race.DWARF ? dwarfMaxSlotStoreBuy : maxSlotStoreBuy;
    }
    public static int maxSlotStoreSell(Race race) {
        return race == Race.DWARF ? dwarfMaxSlotStoreSell : maxSlotStoreSell;
    }

    public static int maxSlotInventory(Player player) {
        if(player.isGM()) {
            return gmMaxSlots;
        }
        return player.getRace() == Race.DWARF ? dwarfMaxSlots : maxSlots;
    }

    public static int maxSlotWarehouse(Race race) {
        return race == Race.DWARF ? dwarfMaxSlotWarehouse : maxSlotWarehouse;
    }

    public static boolean canBeAugmented(int itemId) {
        return Arrays.binarySearch(nonAugmentedItems, itemId) < 0;
    }

    public static byte startLevel() {
        return startLevel;
    }

    public static int startSP() {
        return startSP;
    }
}