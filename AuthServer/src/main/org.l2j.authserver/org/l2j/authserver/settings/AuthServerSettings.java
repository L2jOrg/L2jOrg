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
package org.l2j.authserver.settings;

import org.l2j.commons.configuration.SettingsFile;

public class AuthServerSettings {

    private static String gameServerListenHost;
    private static int gameServerListenPort;
    private static String hostName;
    private static int port;
    private static boolean autoCreateAccount;
    private static int authTriesBeforeBan;
    private static int authBlockAfterBan;
    private static boolean acceptNewGameServer;
    private static String usernameTemplate;
    private static int gmMinimumLevel;

    private AuthServerSettings() {
        // helper class
    }

    public static void load(SettingsFile settingsFile) {
        gameServerListenHost = settingsFile.getString("GameServerListenHostname", "*");
        gameServerListenPort = settingsFile.getInt("GameServerListenPort", 9013);
        hostName = settingsFile.getString("Hostname", "*");
        port = settingsFile.getInt("Port", 2106);
        autoCreateAccount = settingsFile.getBoolean("AutoCreateAccounts", false);
        authTriesBeforeBan = settingsFile.getInt("AuthTriesBeforeBan", 10);
        authBlockAfterBan = settingsFile.getInt("AuthBlockAfterBan", 600);
        acceptNewGameServer = settingsFile.getBoolean("AcceptNewGameServer", false);
        usernameTemplate = settingsFile.getString("UsernameTemplate", "[A-Za-z0-9_]{5,32}");
        gmMinimumLevel = settingsFile.getInt("GMMinLevel", 100);
    }

    public static String gameServerListenHost() {
        return gameServerListenHost;
    }

    public static int gameServerListenPort() {
        return gameServerListenPort;
    }

    public static String listenHost() {
        return hostName;
    }

    public static int listenPort() {
        return port;
    }

    public static boolean isAutoCreateAccount(){
        return autoCreateAccount;
    }

    public static int authTriesBeforeBan(){
        return authTriesBeforeBan;
    }

    public static int loginBlockAfterBan() {
        return authBlockAfterBan;
    }

    public static boolean acceptNewGameServerEnabled() {
        return acceptNewGameServer;
    }

    public static String usernameTemplate() {
        return usernameTemplate;
    }

    public static int gmMinimumLevel() {
        return gmMinimumLevel;
    }

}
