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
import org.l2j.gameserver.enums.IllegalActionPunishmentType;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author JoeAlisson
 */
public class GeneralSettings implements Settings {
    private int banChatAdenaAdsReportCount;
    private boolean auditGM;
    private boolean saveDroppedItems;
    private int autoDestroyItemTime;
    private int autoDestroyHerbTime;
    private boolean allowMail;
    private boolean logItems;
    private boolean smallLogItems;
    private boolean loadCustomBuyList;
    private boolean loadCustomMultisell;
    private boolean cachePlayersName;

    private IllegalActionPunishmentType defaultPunishment;
    private boolean disableChatInJail;
    private int defaultAccessLevel;
    private int autoSavePlayerTime;
    private Duration saveDroppedItemInterval;
    private boolean clearDroppedItems;
    private boolean destroyPlayerDroppedItem;
    private boolean destroyEquipableItem;
    private IntSet protectedItems;
    private boolean clearDroppedItemsAfterLoad;

    @Override
    public void load(SettingsFile settingsFile) {

        banChatAdenaAdsReportCount = settingsFile.getInteger("BanChatAdenaADSReportCount", 10);

        auditGM = settingsFile.getBoolean("AuditGM", false);
        logItems = settingsFile.getBoolean("LogItems", false);
        smallLogItems = settingsFile.getBoolean("LogItemsSmallLog", true);

        saveDroppedItems = settingsFile.getBoolean("SaveDroppedItem", false);
        autoDestroyItemTime = settingsFile.getInteger("AutoDestroyDroppedItemAfter", 600) * 1000;
        autoDestroyHerbTime = settingsFile.getInteger("AutoDestroyHerbTime", 120) * 1000;
        saveDroppedItemInterval = settingsFile.getDuration("SaveDroppedItemInterval", ChronoUnit.MINUTES,60);
        clearDroppedItems = settingsFile.getBoolean("ClearDroppedItemTable", false);
        destroyPlayerDroppedItem = settingsFile.getBoolean("DestroyPlayerDroppedItem", false);
        destroyEquipableItem = settingsFile.getBoolean("DestroyEquipableItem", false);
        protectedItems = settingsFile.getIntSet("ListOfProtectedItems", ",");
        clearDroppedItemsAfterLoad = settingsFile.getBoolean("EmptyDroppedItemTableAfterLoad", false);

        allowMail = settingsFile.getBoolean("AllowMail", true);

        loadCustomBuyList = settingsFile.getBoolean("CustomBuyListLoad", false);
        loadCustomMultisell = settingsFile.getBoolean("CustomMultisellLoad", false);

        cachePlayersName = settingsFile.getBoolean("CacheCharNames", true);

        defaultPunishment = settingsFile.getEnum("DefaultPunish", IllegalActionPunishmentType.class, IllegalActionPunishmentType.KICK);
        disableChatInJail = settingsFile.getBoolean("JailDisableChat", true);

        defaultAccessLevel = settingsFile.getInteger("DefaultAccessLevel", 0);

        autoSavePlayerTime = settingsFile.getInteger("PlayerDataStoreInterval", 20);
     }

    public int banChatAdenaAdsReportCount() {
        return banChatAdenaAdsReportCount;
    }

    public boolean auditGM() {
        return auditGM;
    }

    public boolean saveDroppedItems() {
        return saveDroppedItems;
    }

    public Duration saveDroppedItemInterval() {
        return saveDroppedItemInterval;
    }

    public boolean clearDroppedItems() {
        return clearDroppedItems;
    }

    public boolean destroyPlayerDroppedItem() {
        return destroyPlayerDroppedItem;
    }

    public boolean destroyEquipableItem() {
        return destroyEquipableItem;
    }

    public boolean isProtectedItem(int itemId) {
        return protectedItems.contains(itemId);
    }

    public boolean clearDroppedItemsAfterLoad() {
        return clearDroppedItemsAfterLoad;
    }

    public int autoDestroyItemTime() {
        return autoDestroyItemTime;
    }

    public int autoDestroyHerbTime() {
        return autoDestroyHerbTime;
    }

    public boolean allowMail() {
        return allowMail;
    }

    public boolean logItems() {
        return logItems;
    }

    public boolean smallLogItems() {
        return smallLogItems;
    }

    public boolean loadCustomBuyList() {
        return loadCustomBuyList;
    }

    public boolean loadCustomMultisell() {
        return loadCustomMultisell;
    }

    public boolean cachePlayersName() {
        return cachePlayersName;
    }

    public IllegalActionPunishmentType defaultPunishment() {
        return defaultPunishment;
    }

    public boolean disableChatInJail() {
        return disableChatInJail;
    }

    public int defaultAccessLevel() {
        return defaultAccessLevel;
    }

    public void setDefaultAccessLevel(int accessLevel) {
        defaultAccessLevel = accessLevel;
    }

    public int autoSavePlayerTime() {
        return autoSavePlayerTime;
    }
}
