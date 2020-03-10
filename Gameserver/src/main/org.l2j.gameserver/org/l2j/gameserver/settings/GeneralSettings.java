package org.l2j.gameserver.settings;

import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;
import org.l2j.gameserver.enums.IllegalActionPunishmentType;

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

    @Override
    public void load(SettingsFile settingsFile) {

        banChatAdenaAdsReportCount = settingsFile.getInteger("BanChatAdenaADSReportCount", 10);

        auditGM = settingsFile.getBoolean("AuditGM", false);
        logItems = settingsFile.getBoolean("LogItems", false);
        smallLogItems = settingsFile.getBoolean("LogItemsSmallLog", true);

        saveDroppedItems = settingsFile.getBoolean("SaveDroppedItem", false);
        autoDestroyItemTime = settingsFile.getInteger("AutoDestroyDroppedItemAfter", 600) * 1000;
        autoDestroyHerbTime = settingsFile.getInteger("AutoDestroyHerbTime", 120) * 1000;

        allowMail = settingsFile.getBoolean("AllowMail", true);

        loadCustomBuyList = settingsFile.getBoolean("CustomBuyListLoad", false);
        loadCustomMultisell = settingsFile.getBoolean("CustomMultisellLoad", false);

        cachePlayersName = settingsFile.getBoolean("CacheCharNames", true);

        defaultPunishment = settingsFile.getEnum("DefaultPunish", IllegalActionPunishmentType.class, IllegalActionPunishmentType.KICK);
        disableChatInJail = settingsFile.getBoolean("JailDisableChat", true);

        defaultAccessLevel = settingsFile.getInteger("DefaultAccessLevel", 0);
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
}
