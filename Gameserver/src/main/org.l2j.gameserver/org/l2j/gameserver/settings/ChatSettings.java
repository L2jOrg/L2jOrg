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

import org.l2j.commons.configuration.SettingsFile;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.ChatType;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;

/**
 * @author JoeAlisson
 */
public class ChatSettings {
    private static int generalChatLevel;
    private static int whisperChatLevel;
    private static int shoutChatLevel;
    private static int tradeChatLevel;
            
    private static boolean l2WalkerProtectionEnabled;
    private static String[] l2WalkerCommandList;
    private static Set<ChatType> bannableChannels;
    private static boolean logChat;
    private static boolean enableChatFilter;
    private static String[] filterList;
    private static String filterChars;
    private static String defaultGlobalChat;
    private static String defaultTradeChat;
    private static boolean silenceModeExclude;
    private static boolean worldChatEnabled;
    private static int worldChatMinLevel;
    private static Duration worldChatInterval;

    private ChatSettings() {
        // helper class
    }

    public static void load(SettingsFile settingsFile) {
        generalChatLevel = settingsFile.getInt("MinimumGeneralChatLevel", 2);
        whisperChatLevel = settingsFile.getInt("MinimumWhisperChatLevel", 2);
        shoutChatLevel = settingsFile.getInt("MinimumShoutChatLevel", 10);
        tradeChatLevel = settingsFile.getInt("MinimumTradeChatLevel", 15);

        defaultGlobalChat = settingsFile.getString("GlobalChat", "ON");
        defaultTradeChat = settingsFile.getString("TradeChat", "ON");

        worldChatEnabled = settingsFile.getBoolean("WorldChatEnabled", true);
        worldChatMinLevel = settingsFile.getInt("WorldChatMinLevel", 80);
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

    public static int generalChatLevel() {
        return generalChatLevel;
    }

    public static int whisperChatLevel() {
        return whisperChatLevel;
    }

    public static int shoutChatLevel() {
        return shoutChatLevel;
    }

    public static int tradeChatLevel() {
        return tradeChatLevel;
    }

    public static boolean l2WalkerProtectionEnabled() {
        return l2WalkerProtectionEnabled;
    }

    public static Set<ChatType> bannableChannels() {
        return bannableChannels;
    }

    public static boolean logChat() {
        return logChat;
    }

    public static boolean enableChatFilter() {
        return enableChatFilter;
    }

    public static boolean isL2WalkerCommand(String text) {
        for (String command : l2WalkerCommandList) {
            if(text.startsWith(command)) {
                return true;
            }
        }
        return false;
    }

    public static String filterText(String text) {
        String filteredText  = text;
        for (String pattern : filterList) {
            filteredText = filteredText.replaceAll("(?i)" + pattern, filterChars);
        }
        return filteredText;
    }

    public static String defaultGlobalChat() {
        return defaultGlobalChat;
    }

    public static String defaultTradeChat() {
        return defaultTradeChat;
    }

    public static boolean silenceModeExclude() {
        return silenceModeExclude;
    }

    public static boolean worldChatEnabled() {
        return worldChatEnabled;
    }

    public static int worldChatMinLevel() {
        return worldChatMinLevel;
    }

    public static Duration worldChatInterval() {
        return worldChatInterval;
    }
}
