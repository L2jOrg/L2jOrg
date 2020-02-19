package org.l2j.gameserver.settings;

import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;

/**
 * @author JoeAlisson
 */
public class GeneralSettings implements Settings {

    private int generalChatLevel;
    private int whisperChatLevel;
    private int shoutChatLevel;
    private int tradeChatLevel;
    private int banChatAdenaAdsReportCount;
    private boolean auditGM;
    private boolean saveDroppedItems;
    private int autoDestroyItemTime;
    private int autoDestroyHerbTime;

    @Override
    public void load(SettingsFile settingsFile) {
        generalChatLevel = settingsFile.getInteger("MinimumGeneralChatLevel", 2);
        whisperChatLevel = settingsFile.getInteger("MinimumWhisperChatLevel", 2);
        shoutChatLevel = settingsFile.getInteger("MinimumShoutChatLevel", 10);
        tradeChatLevel = settingsFile.getInteger("MinimumTradeChatLevel", 15);

        banChatAdenaAdsReportCount = settingsFile.getInteger("BanChatAdenaADSReportCount", 10);

        auditGM = settingsFile.getBoolean("AuditGM", false);

        saveDroppedItems = settingsFile.getBoolean("SaveDroppedItem", false);
        autoDestroyItemTime = settingsFile.getInteger("AutoDestroyDroppedItemAfter", 600) * 1000;
        autoDestroyHerbTime = settingsFile.getInteger("AutoDestroyHerbTime", 120) * 1000;
     }

    public int generalChatLevel() {
        return generalChatLevel;
    }

    public int whisperChatLevel() {
        return whisperChatLevel;
    }

    public int shoutChatLevel() {
        return shoutChatLevel;
    }

    public int tradeChatLevel() {
        return tradeChatLevel;
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
}
