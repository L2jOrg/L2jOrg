package org.l2j.gameserver.settings;

import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;

public class GeneralSettings implements Settings {

    private int generalChatLevel;
    private int whisperChatLevel;
    private int shoutChatLevel;
    private int tradeChatLevel;

    @Override
    public void load(SettingsFile settingsFile) {
        generalChatLevel = settingsFile.getInteger("MinimumGeneralChatLevel", 2);
        whisperChatLevel = settingsFile.getInteger("MinimumWhisperChatLevel", 2);
        shoutChatLevel = settingsFile.getInteger("MinimumShoutChatLevel", 10);
        tradeChatLevel = settingsFile.getInteger("MinimumTradeChatLevel", 15);
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
}
