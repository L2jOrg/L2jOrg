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
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.IllegalActionPunishmentType;
import org.l2j.gameserver.world.zone.type.PeaceZone;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author JoeAlisson
 */
public class GeneralSettings {

    private static int banChatAdenaAdsReportCount;
    private static boolean auditGM;
    private static boolean saveDroppedItems;
    private static int autoDestroyItemTime;
    private static int autoDestroyHerbTime;
    private static boolean logItems;
    private static boolean smallLogItems;
    private static boolean loadCustomBuyList;
    private static boolean cachePlayersName;

    private static IllegalActionPunishmentType defaultPunishment;
    private static boolean disableChatInJail;
    private static int autoSavePlayerTime;
    private static Duration saveDroppedItemInterval;
    private static boolean clearDroppedItems;
    private static boolean destroyPlayerDroppedItem;
    private static boolean destroyEquipableItem;
    private static IntSet protectedItems;
    private static boolean clearDroppedItemsAfterLoad;
    private static boolean logItemEnchants;
    private static boolean skillCheckEnabled;
    private static boolean noSpawn;
    private static boolean logQuestsLoading;
    private static boolean allowDiscardItem;
    private static boolean updateItemsOnCharStore;
    private static boolean destroyAnyItem;
    private static boolean deleteInvalidQuest;
    private static int minNpcAnimation;
    private static int maxNpcAnimation;
    private static int neighborRegionTurnOnTime;
    private static int neighborRegionTurnOffTime;
    private static PeaceZone.Mode peaceZoneMode;
    private static boolean cacheWarehouse;
    private static long cacheWarehouseTime;
    private static boolean allowRefund;
    private static boolean allowWear;
    private static long wearDelay;
    private static int wearPrice;
    private static int instanceFinishTime;
    private static boolean restoreInstance;
    private static int instanceEjectDeadTime;
    private static boolean allowWater;
    private static boolean allowFishing;
    private static boolean enableCommunity;
    private static String bbsDefault;
    private static int worldChatPointsPerDay;

    private GeneralSettings() {
        // helper class
    }

    public static void load(SettingsFile settingsFile) {

        banChatAdenaAdsReportCount = settingsFile.getInt("BanChatAdenaADSReportCount", 10);

        auditGM = settingsFile.getBoolean("AuditGM", false);
        logItems = settingsFile.getBoolean("LogItems", false);
        smallLogItems = settingsFile.getBoolean("LogItemsSmallLog", true);
        logItemEnchants = settingsFile.getBoolean("LogItemEnchants", false);
        logQuestsLoading = settingsFile.getBoolean("AltDevShowQuestsLoadInLogs", false);

        saveDroppedItems = settingsFile.getBoolean("SaveDroppedItem", false);
        autoDestroyItemTime = settingsFile.getInt("AutoDestroyDroppedItemAfter", 600) * 1000;
        autoDestroyHerbTime = settingsFile.getInt("AutoDestroyHerbTime", 120) * 1000;
        saveDroppedItemInterval = settingsFile.getDuration("SaveDroppedItemInterval", ChronoUnit.MINUTES,60);
        clearDroppedItems = settingsFile.getBoolean("ClearDroppedItemTable", false);
        destroyPlayerDroppedItem = settingsFile.getBoolean("DestroyPlayerDroppedItem", false);
        destroyEquipableItem = settingsFile.getBoolean("DestroyEquipableItem", false);
        protectedItems = settingsFile.getIntSet("ListOfProtectedItems", ",");
        clearDroppedItemsAfterLoad = settingsFile.getBoolean("EmptyDroppedItemTableAfterLoad", false);

        loadCustomBuyList = settingsFile.getBoolean("CustomBuyListLoad", false);

        cachePlayersName = settingsFile.getBoolean("CacheCharNames", true);

        defaultPunishment = settingsFile.getEnum("DefaultPunish", IllegalActionPunishmentType.class, IllegalActionPunishmentType.KICK);
        disableChatInJail = settingsFile.getBoolean("JailDisableChat", true);
        autoSavePlayerTime = settingsFile.getInt("PlayerDataStoreInterval", 20);

        skillCheckEnabled = settingsFile.getBoolean("SkillCheckEnable", false);
        noSpawn = settingsFile.getBoolean("AltDevNoSpawns", false);

        allowDiscardItem = settingsFile.getBoolean("AllowDiscardItem", true);
        updateItemsOnCharStore = settingsFile.getBoolean("UpdateItemsOnCharStore", false);
        destroyAnyItem = settingsFile.getBoolean("DestroyAllItems", false);

        deleteInvalidQuest = settingsFile.getBoolean("AutoDeleteInvalidQuestData", false);

        minNpcAnimation = settingsFile.getInt("MinNpcAnimation", 30);
        maxNpcAnimation = settingsFile.getInt("MaxNpcAnimation", 120);

        neighborRegionTurnOnTime = settingsFile.getInt("GridNeighborTurnOnTime", 1);
        neighborRegionTurnOffTime = settingsFile.getInt("GridNeighborTurnOffTime", 90);

        peaceZoneMode = settingsFile.getEnum("PeaceZoneMode", PeaceZone.Mode.class, PeaceZone.Mode.PEACE);

        cacheWarehouse = settingsFile.getBoolean("WarehouseCache", false);
        cacheWarehouseTime = settingsFile.getInt("WarehouseCacheTime", 15) * 60000L;

        allowRefund = settingsFile.getBoolean("AllowRefund", true);

        allowWear = settingsFile.getBoolean("AllowWear", true);
        wearDelay = settingsFile.getInt("WearDelay", 5) * 1000L;
        wearPrice = settingsFile.getInt("WearPrice", 10);

        instanceFinishTime = settingsFile.getInt("InstanceFinishTime", 5);
        restoreInstance = settingsFile.getBoolean("RestoreInstance", false);
        instanceEjectDeadTime = settingsFile.getInt("EjectDeadPlayerTime", 1);

        allowWater = settingsFile.getBoolean("AllowWater", true);
        allowFishing = settingsFile.getBoolean("AllowFishing", true);
        enableCommunity = settingsFile.getBoolean("EnableCommunityBoard", true);
        bbsDefault = settingsFile.getString("BBSDefault", "_bbshome");
        worldChatPointsPerDay = settingsFile.getInt("WorldChatPointsPerDay", 10);
     }

    public static int banChatAdenaAdsReportCount() {
        return banChatAdenaAdsReportCount;
    }

    public static boolean auditGM() {
        return auditGM;
    }

    public static boolean saveDroppedItems() {
        return saveDroppedItems;
    }

    public static Duration saveDroppedItemInterval() {
        return saveDroppedItemInterval;
    }

    public static boolean clearDroppedItems() {
        return clearDroppedItems;
    }

    public static boolean destroyPlayerDroppedItem() {
        return destroyPlayerDroppedItem;
    }

    public static boolean destroyEquipableItem() {
        return destroyEquipableItem;
    }

    public static boolean isProtectedItem(int itemId) {
        return protectedItems.contains(itemId);
    }

    public static boolean clearDroppedItemsAfterLoad() {
        return clearDroppedItemsAfterLoad;
    }

    public static int autoDestroyItemTime() {
        return autoDestroyItemTime;
    }

    public static int autoDestroyHerbTime() {
        return autoDestroyHerbTime;
    }

    public static boolean logItems() {
        return logItems;
    }

    public static boolean smallLogItems() {
        return smallLogItems;
    }

    public static boolean logItemEnchants() {
        return logItemEnchants;
    }

    public static boolean logQuestsLoading() {
        return logQuestsLoading;
    }

    public static boolean loadCustomBuyList() {
        return loadCustomBuyList;
    }

    public static boolean cachePlayersName() {
        return cachePlayersName;
    }

    public static IllegalActionPunishmentType defaultPunishment() {
        return defaultPunishment;
    }

    public static boolean disableChatInJail() {
        return disableChatInJail;
    }

    public static int autoSavePlayerTime() {
        return autoSavePlayerTime;
    }

    public static boolean skillCheckEnabled() {
        return skillCheckEnabled;
    }

    public static boolean noSpawn() {
        return noSpawn;
    }

    public static boolean allowDiscardItem() {
        return allowDiscardItem;
    }

    public static boolean updateItemsOnCharStore() {
        return updateItemsOnCharStore;
    }

    public static boolean destroyAnyItem() {
        return destroyAnyItem;
    }

    public static boolean deleteInvalidQuest() {
        return deleteInvalidQuest;
    }

    public static int randomNpcAnimation() {
        return Rnd.get(minNpcAnimation, maxNpcAnimation);
    }

    public static int maxNpcAnimation() {
        return maxNpcAnimation;
    }

    public static int neighborRegionTurnOnTime() {
        return neighborRegionTurnOnTime;
    }

    public static int neighborRegionTurnOffTime() {
        return neighborRegionTurnOffTime;
    }

    public static PeaceZone.Mode peaceZoneMode() {
        return peaceZoneMode;
    }

    public static boolean cacheWarehouse() {
        return cacheWarehouse;
    }

    public static long cacheWarehouseTime() {
        return cacheWarehouseTime;
    }

    public static boolean allowRefund() {
        return allowRefund;
    }

    public static boolean allowWear() {
        return allowWear;
    }

    public static long wearDelay() {
        return wearDelay;
    }

    public static int wearPrice() {
        return wearPrice;
    }

    public static int instanceFinishTime() {
        return instanceFinishTime;
    }

    public static boolean restoreInstance() {
        return restoreInstance;
    }

    public static int instanceEjectDeadTime() {
        return instanceEjectDeadTime;
    }

    public static boolean allowWater() {
        return allowWater;
    }

    public static boolean allowFishing() {
        return allowFishing;
    }

    public static boolean enableCommunity() {
        return enableCommunity;
    }

    public static String bbsDefault() {
        return bbsDefault;
    }

    public static int worldChatPointsPerDay() {
        return worldChatPointsPerDay;
    }
}
