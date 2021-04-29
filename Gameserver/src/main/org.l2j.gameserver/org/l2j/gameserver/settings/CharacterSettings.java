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

    private static boolean autoLootRaid;
    private static int raidLootPrivilegeTime;
    private static boolean autoLoot;
    private static boolean initialEquipEvent;
    private static boolean delevel;
    private static float weightLimitMultiplier;
    private static boolean removeCastleCirclets;
    private static boolean restoreSummonOnReconnect;
    private static int minEnchantAnnounceWeapon;
    private static int minEnchantAnnounceArmor;
    private static float restoreCPPercent;
    private static float restoreHPPercent;
    private static float restoreMPPercent;
    private static boolean autoLearnSkillEnabled;
    private static boolean autoLearnSkillFSEnabled;
    private static byte maxBuffs;
    private static byte maxTriggeredBuffs;
    private static byte maxDances;
    private static boolean dispelDanceAllowed;
    private static boolean storeDances;
    private static boolean breakCast;
    private static boolean breakBowAttack;
    private static boolean magicFailureAllowed;
    private static boolean breakStun;
    private static int effectTickRatio;
    private static boolean autoLootHerbs;
    private static boolean pledgeSkillsItemNeeded;
    private static boolean divineInspirationBookNeeded;
    private static boolean vitalityEnabled;
    private static boolean raidBossUseVitality;
    private static int maxRunSpeed;
    private static int maxPcritRate;
    private static int maxMcritRate;
    private static int maxPAtkSpeed;
    private static int maxMAtkSpeed;
    private static int maxEvasion;
    private static boolean teleportInBattle;
    private static boolean craftEnabled;
    private static long maxAdena;
    private static boolean allowPKTeleport;
    private static int maxFreeTeleportLevel;
    private static int maxItemInPacket;
    private static int maxSlotsQuestItem;
    private static int clanMaxWarehouseSlot;
    private static int maxSlotFreight;
    private static int freightPrice;
    private static boolean canAttackPkInPeaceZone;
    private static boolean canPkShop;
    private static boolean canPkTeleport;
    private static boolean canPkTrade;
    private static boolean canPkUseWareHouse;
    private static int maxFame;
    private static int fameTaskDelay;
    private static int fameTaskPoints;
    private static boolean fameForDeadPlayers;
    private static int criticalCraftRate;
    private static int dwarfRecipeLimit;
    private static int recipeLimit;
    private static boolean altGameCreation;
    private static double altGameCreationSpeed;
    private static double altGameCreationXpRate;
    private static double altGameCreationSpRate;
    private static double altGameCreationRareXpSpRate;

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
    private static int lootRaidCommandChannelSize;
    private static boolean enableKeyboardMovement;
    private static int unstuckInterval;
    private static int spawnProtection;
    private static int teleportProtection;
    private static boolean randomRespawnEnabled;
    private static boolean offsetTeleportEnabled;
    private static int maxOffsetTeleport;
    private static boolean petitionAllowed;
    private static int maxPetitions;
    private static int maxPendingPetitions;
    private static int maxNewbieBuffLevel;
    private static int daysToDelete;
    private static boolean disableTutorial;
    private static boolean storeRecipeShopList;
    private static boolean storeUISettings;
    private static long npcTalkBlockingTime;

    private CharacterSettings() {
        // helper class
    }

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

        lootRaidCommandChannelSize = settingsFile.getInt("RaidLootRightsCCSize", 45);
        enableKeyboardMovement = settingsFile.getBoolean("KeyboardMovement", true);
        unstuckInterval = settingsFile.getInt("UnstuckInterval", 300);
        spawnProtection = settingsFile.getInt("PlayerSpawnProtection", 0);
        teleportProtection = settingsFile.getInt("PlayerTeleportProtection", 0);
        randomRespawnEnabled = settingsFile.getBoolean("RandomRespawnInTownEnabled", true);
        offsetTeleportEnabled = settingsFile.getBoolean("OffsetOnTeleportEnabled", true);
        maxOffsetTeleport = settingsFile.getInt("MaxOffsetOnTeleport", 50);
        petitionAllowed = settingsFile.getBoolean("PetitioningAllowed", true);
        maxPetitions = settingsFile.getInt("MaxPetitionsPerPlayer", 5);
        maxPendingPetitions = settingsFile.getInt("MaxPetitionsPending", 25);

        maxNewbieBuffLevel = settingsFile.getInt("MaxNewbieBuffLevel", 0);
        daysToDelete = settingsFile.getInt("DeleteCharAfterDays", 1);

        disableTutorial = settingsFile.getBoolean("DisableTutorial", false);
        storeRecipeShopList = settingsFile.getBoolean("StoreRecipeShopList", false);
        storeUISettings = settingsFile.getBoolean("StoreCharUiSettings", true);
        npcTalkBlockingTime = settingsFile.getInt("NpcTalkBlockingTime", 0) * 1000L;
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

    public static boolean autoLootRaid() {
        return autoLootRaid;
    }

    public static int raidLootPrivilegeTime() {
        return raidLootPrivilegeTime;
    }

    public static boolean autoLoot() {
        return autoLoot;
    }

    public static boolean initialEquipEvent() {
        return initialEquipEvent;
    }

    public static boolean delevel() {
        return delevel;
    }

    public static float weightLimitMultiplier() {
        return weightLimitMultiplier;
    }

    public static boolean removeCastleCirclets() {
        return removeCastleCirclets;
    }

    public static boolean restoreSummonOnReconnect() {
        return restoreSummonOnReconnect;
    }

    public static int minEnchantAnnounceWeapon() {
        return minEnchantAnnounceWeapon;
    }

    public static int minEnchantAnnounceArmor() {
        return minEnchantAnnounceArmor;
    }

    public static float restoreCPPercent() {
        return restoreCPPercent;
    }

    public static float restoreHPPercent() {
        return restoreHPPercent;
    }

    public static float restoreMPPercent() {
        return restoreMPPercent;
    }

    public static boolean autoLearnSkillEnabled() {
        return autoLearnSkillEnabled;
    }

    public static boolean autoLearnSkillFSEnabled() {
        return autoLearnSkillFSEnabled;
    }

    public static byte maxBuffs() {
        return maxBuffs;
    }

    public static byte maxTriggeredBuffs() {
        return maxTriggeredBuffs;
    }

    public static byte maxDances() {
        return maxDances;
    }

    public static boolean dispelDanceAllowed() {
        return dispelDanceAllowed;
    }

    public static boolean storeDances() {
        return storeDances;
    }

    public static boolean breakCast() {
        return breakCast;
    }

    public static boolean breakBowAttack() {
        return breakBowAttack;
    }

    public static boolean magicFailureAllowed() {
        return magicFailureAllowed;
    }

    public static boolean breakStun() {
        return breakStun;
    }

    public static int effectTickRatio() {
        return effectTickRatio;
    }

    public static boolean autoLootHerbs() {
        return autoLootHerbs;
    }

    public static boolean pledgeSkillsItemNeeded() {
        return pledgeSkillsItemNeeded;
    }

    public static boolean divineInspirationBookNeeded() {
        return divineInspirationBookNeeded;
    }

    public static boolean vitalityEnabled() {
        return vitalityEnabled;
    }

    public static boolean raidBossUseVitality() {
        return raidBossUseVitality;
    }

    public static int maxRunSpeed() {
        return maxRunSpeed;
    }

    public static int maxPcritRate() {
        return maxPcritRate;
    }

    public static int maxMcritRate() {
        return maxMcritRate;
    }

    public static int maxPAtkSpeed() {
        return maxPAtkSpeed;
    }

    public static int maxMAtkSpeed() {
        return maxMAtkSpeed;
    }

    public static int maxEvasion() {
        return maxEvasion;
    }

    public static boolean teleportInBattle() {
        return teleportInBattle;
    }

    public static boolean craftEnabled() {
        return craftEnabled;
    }

    public static long maxAdena() {
        return maxAdena;
    }

    public static boolean allowPKTeleport() {
        return allowPKTeleport;
    }

    public static int maxFreeTeleportLevel() {
        return maxFreeTeleportLevel;
    }

    public static int maxItemInPacket() {
        return maxItemInPacket;
    }

    public static int maxSlotsQuestItem() {
        return maxSlotsQuestItem;
    }

    public static int clanMaxWarehouseSlot() {
        return clanMaxWarehouseSlot;
    }

    public static int maxSlotFreight() {
        return maxSlotFreight;
    }

    public static int freightPrice() {
        return freightPrice;
    }

    public static boolean canAttackPkInPeaceZone() {
        return canAttackPkInPeaceZone;
    }

    public static boolean canPkShop() {
        return canPkShop;
    }

    public static boolean canPkTeleport() {
        return canPkTeleport;
    }

    public static boolean canPkTrade() {
        return canPkTrade;
    }

    public static boolean canPkUseWareHouse() {
        return canPkUseWareHouse;
    }

    public static int maxFame() {
        return maxFame;
    }

    public static int fameTaskDelay() {
        return fameTaskDelay;
    }

    public static int fameTaskPoints() {
        return fameTaskPoints;
    }

    public static boolean fameForDeadPlayers() {
        return fameForDeadPlayers;
    }

    public static int criticalCraftRate() {
        return criticalCraftRate;
    }

    public static int dwarfRecipeLimit() {
        return dwarfRecipeLimit;
    }

    public static int recipeLimit() {
        return recipeLimit;
    }

    public static boolean altGameCreation() {
        return altGameCreation;
    }

    public static double altGameCreationSpeed() {
        return altGameCreationSpeed;
    }

    public static double altGameCreationXpRate() {
        return altGameCreationXpRate;
    }

    public static double altGameCreationSpRate() {
        return altGameCreationSpRate;
    }

    public static double altGameCreationRareXpSpRate() {
        return altGameCreationRareXpSpRate;
    }

    public static int lootRaidCommandChannelSize() {
        return lootRaidCommandChannelSize;
    }

    public static boolean enableKeyboardMovement() {
        return enableKeyboardMovement;
    }

    public static int unstuckInterval() {
        return unstuckInterval;
    }

    public static int spawnProtection() {
        return spawnProtection;
    }

    public static int teleportProtection() {
        return teleportProtection;
    }

    public static boolean randomRespawnEnabled() {
        return randomRespawnEnabled;
    }

    public static boolean offsetTeleportEnabled() {
        return offsetTeleportEnabled;
    }

    public static int maxOffsetTeleport() {
        return maxOffsetTeleport;
    }

    public static boolean petitionAllowed() {
        return petitionAllowed;
    }

    public static int maxPetitions() {
        return maxPetitions;
    }

    public static int maxPendingPetitions() {
        return maxPendingPetitions;
    }

    public static int maxNewbieBuffLevel() {
        return maxNewbieBuffLevel;
    }

    public static int daysToDelete() {
        return daysToDelete;
    }

    public static boolean disableTutorial() {
        return disableTutorial;
    }

    public static boolean storeRecipeShopList() {
        return storeRecipeShopList;
    }

    public static boolean storeUISettings() {
        return storeUISettings;
    }

    public static long npcTalkBlockingTime() {
        return npcTalkBlockingTime;
    }
}