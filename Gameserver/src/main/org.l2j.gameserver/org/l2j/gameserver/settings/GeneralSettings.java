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
import org.l2j.gameserver.enums.IllegalActionPunishmentType;

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
    private static boolean allowMail;
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

    private GeneralSettings() {
        // helper class
    }

    public static void load(SettingsFile settingsFile) {

        banChatAdenaAdsReportCount = settingsFile.getInt("BanChatAdenaADSReportCount", 10);

        auditGM = settingsFile.getBoolean("AuditGM", false);
        logItems = settingsFile.getBoolean("LogItems", false);
        smallLogItems = settingsFile.getBoolean("LogItemsSmallLog", true);

        saveDroppedItems = settingsFile.getBoolean("SaveDroppedItem", false);
        autoDestroyItemTime = settingsFile.getInt("AutoDestroyDroppedItemAfter", 600) * 1000;
        autoDestroyHerbTime = settingsFile.getInt("AutoDestroyHerbTime", 120) * 1000;
        saveDroppedItemInterval = settingsFile.getDuration("SaveDroppedItemInterval", ChronoUnit.MINUTES,60);
        clearDroppedItems = settingsFile.getBoolean("ClearDroppedItemTable", false);
        destroyPlayerDroppedItem = settingsFile.getBoolean("DestroyPlayerDroppedItem", false);
        destroyEquipableItem = settingsFile.getBoolean("DestroyEquipableItem", false);
        protectedItems = settingsFile.getIntSet("ListOfProtectedItems", ",");
        clearDroppedItemsAfterLoad = settingsFile.getBoolean("EmptyDroppedItemTableAfterLoad", false);

        allowMail = settingsFile.getBoolean("AllowMail", true);

        loadCustomBuyList = settingsFile.getBoolean("CustomBuyListLoad", false);

        cachePlayersName = settingsFile.getBoolean("CacheCharNames", true);

        defaultPunishment = settingsFile.getEnum("DefaultPunish", IllegalActionPunishmentType.class, IllegalActionPunishmentType.KICK);
        disableChatInJail = settingsFile.getBoolean("JailDisableChat", true);
        autoSavePlayerTime = settingsFile.getInt("PlayerDataStoreInterval", 20);
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

    public static boolean allowMail() {
        return allowMail;
    }

    public static boolean logItems() {
        return logItems;
    }

    public static boolean smallLogItems() {
        return smallLogItems;
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
}
