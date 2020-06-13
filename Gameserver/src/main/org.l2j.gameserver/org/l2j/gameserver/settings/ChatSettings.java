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

import org.l2j.commons.configuration.Settings;
import org.l2j.commons.configuration.SettingsFile;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.ChatType;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;

public class ChatSettings implements Settings {
    private int generalChatLevel;
    private int whisperChatLevel;
    private int shoutChatLevel;
    private int tradeChatLevel;

    private boolean l2WalkerProtectionEnabled;
    private String[] l2WalkerCommandList;
    private Set<ChatType> bannableChannels;
    private boolean logChat;
    private boolean enableChatFilter;
    private String[] filterList;
    private String filterChars;
    private String defaultGlobalChat;
    private String defaultTradeChat;
    private boolean silenceModeExclude;
    private boolean worldChatEnabled;
    private int worldChatMinLevel;
    private Duration worldChatInterval;

    @Override
    public void load(SettingsFile settingsFile) {
        generalChatLevel = settingsFile.getInteger("MinimumGeneralChatLevel", 2);
        whisperChatLevel = settingsFile.getInteger("MinimumWhisperChatLevel", 2);
        shoutChatLevel = settingsFile.getInteger("MinimumShoutChatLevel", 10);
        tradeChatLevel = settingsFile.getInteger("MinimumTradeChatLevel", 15);

        defaultGlobalChat = settingsFile.getString("GlobalChat", "ON");
        defaultTradeChat = settingsFile.getString("TradeChat", "ON");

        worldChatEnabled = settingsFile.getBoolean("WorldChatEnabled", true);
        worldChatMinLevel = settingsFile.getInteger("WorldChatMinLevel", 80);
        worldChatInterval = settingsFile.getDuration("WorldChatInterval", 20);

        silenceModeExclude = settingsFile.getBoolean("SilenceModeExclude", false);

        logChat = settingsFile.getBoolean("LogChat", false);

        enableChatFilter = settingsFile.getBoolean("EnableChatFilter", false);
        filterList = settingsFile.getStringArray("FilterList");
        filterChars = settingsFile.getString("ChatFilterChars", Util.STRING_EMPTY);

        bannableChannels = settingsFile.getEnumSet("BanChatChannels", ChatType.class, Collections.emptySet());

        l2WalkerProtectionEnabled = settingsFile.getBoolean("L2WalkerProtection", false);
        l2WalkerCommandList = l2WalkerProtectionEnabled ? settingsFile.getStringArray("L2WalkerCommands") : Util.STRING_ARRAY_EMPTY;
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


    public boolean l2WalkerProtectionEnabled() {
        return l2WalkerProtectionEnabled;
    }


    public Set<ChatType> bannableChannels() {
        return bannableChannels;
    }

    public boolean logChat() {
        return logChat;
    }

    public boolean enableChatFilter() {
        return enableChatFilter;
    }

    public boolean isL2WalkerCommand(String text) {
        for (String command : l2WalkerCommandList) {
            if(text.startsWith(command)) {
                return true;
            }
        }
        return false;
    }

    public String filterText(String text) {
        String filteredText  = text;
        for (String pattern : filterList) {
            filteredText = filteredText.replaceAll("(?i)" + pattern, filterChars);
        }
        return filteredText;
    }

    public String defaultGlobalChat() {
        return defaultGlobalChat;
    }

    public String defaultTradeChat() {
        return defaultTradeChat;
    }

    public boolean silenceModeExclude() {
        return silenceModeExclude;
    }

    public boolean worldChatEnabled() {
        return worldChatEnabled;
    }

    public int worldChatMinLevel() {
        return worldChatMinLevel;
    }

    public Duration worldChatInterval() {
        return worldChatInterval;
    }
}
