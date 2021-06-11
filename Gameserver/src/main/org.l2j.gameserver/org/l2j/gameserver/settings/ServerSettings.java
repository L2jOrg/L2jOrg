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
import org.l2j.gameserver.ServerType;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author JoeAlisson
 */
public class ServerSettings {

    private static int serverId;
    private static boolean acceptAlternativeId;
    private static String authServerAddress;
    private static short authServerPort;

    private static byte ageLimit;
    private static boolean showBrackets;
    private static boolean isPvP;
    private static int type;
    private static short port;
    private static int maximumOnlineUsers;
    private static Path dataPackDirectory;

    private static int scheduledPoolSize;
    private static int threadPoolSize;
    private static int[] acceptedProtocols;
    private static boolean scheduleRestart;
    private static boolean useDeadLockDetector;
    private static int deadLockDetectorInterval;
    private static boolean restartOnDeadLock;
    private static int maxPlayers;
    private static Predicate<String> playerNamePattern;
    private static Predicate<String> petNamePattern;
    private static Predicate<String> clanNamePattern;
    private static String[] scheduleRestartHours;
    private static boolean hardwareInfoEnabled;
    private static int maxPlayerPerHWID;
    private static int maxThreadPoolSize;
    private static int parallelismThreshold;

    private ServerSettings() {
        // helper class
    }

    public static void load(SettingsFile settingsFile) {
        serverId = settingsFile.getInt("RequestServerID", 1);
        acceptAlternativeId = settingsFile.getBoolean("AcceptAlternateID", true);

        authServerAddress = settingsFile.getString("LoginHost", "127.0.0.1");
        authServerPort = settingsFile.getShort("LoginPort", (short) 9014);
        port = settingsFile.getShort("GameserverPort", (short) 7777);
        type = ServerType.maskOf(settingsFile.getStringArray("ServerListType"));

        maximumOnlineUsers = Math.max(1, settingsFile.getInt("MaximumOnlineUsers", 20));
        ageLimit = settingsFile.getByte("ServerListAge", (byte) 0);
        showBrackets = settingsFile.getBoolean("ServerListBrackets", false);
        isPvP = settingsFile.getBoolean("PvPServer", false);

        dataPackDirectory = Path.of(settingsFile.getString("DatapackRoot", "."));

        var processors = Runtime.getRuntime().availableProcessors();

        scheduledPoolSize = determinePoolSize(settingsFile, "ScheduledThreadPoolSize", processors);
        threadPoolSize = determinePoolSize(settingsFile, "ThreadPoolSize", processors);
        maxThreadPoolSize = determinePoolSize(settingsFile, "MaxThreadPoolSize", threadPoolSize * 10);
        parallelismThreshold = settingsFile.getInt("ParallelismThreshold", 1000);
        acceptedProtocols =  settingsFile.getIntArray("AllowedProtocolRevisions", ";");

        scheduleRestart = settingsFile.getBoolean("ServerRestartScheduleEnabled", false);
        scheduleRestartHours = settingsFile.getStringArray("ServerRestartSchedule");

        useDeadLockDetector = settingsFile.getBoolean("DeadLockDetector", true);
        deadLockDetectorInterval = settingsFile.getInt("DeadLockCheckInterval", 1800);
        restartOnDeadLock = settingsFile.getBoolean("RestartOnDeadlock", false);

        playerNamePattern = determineNamePattern(settingsFile, "CnameTemplate");
        petNamePattern = determineNamePattern(settingsFile, "PetNameTemplate");
        clanNamePattern = determineNamePattern(settingsFile, "ClanNameTemplate");

        maxPlayers = settingsFile.getInt("CharMaxNumber", 7);
        hardwareInfoEnabled = settingsFile.getBoolean("EnableHardwareInfo", false);
        maxPlayerPerHWID = settingsFile.getInt("MaxPlayersPerHWID", 0);
    }

    private static Predicate<String> determineNamePattern(SettingsFile settingsFile, String key) {
        try {
            return Pattern.compile(settingsFile.getString(key, ".*")).asMatchPredicate();
        } catch (PatternSyntaxException e) {
            return Util.ANY_PATTERN;
        }
    }

    private static int determinePoolSize(SettingsFile settingsFile, String property, int processors) {
        var size = settingsFile.getInt(property, processors);

        if(size < 2) {
            return processors;
        }
        return size;
    }

    public static int serverId() {
        return serverId;
    }

    public static void setServerId(int id) {
        serverId = id;
    }

    public static short port() {
        return port;
    }

    public static String authServerAddress() {
        return authServerAddress;
    }

    public static int authServerPort() {
        return authServerPort;
    }

    public static byte ageLimit() {
        return ageLimit;
    }

    public static boolean isShowingBrackets() {
        return showBrackets;
    }

    public static boolean isPvP() {
        return isPvP;
    }

    public static int type() {
        return type;
    }

    public static void setType(int value) {
        type = value;
    }

    public static int maximumOnlineUsers() {
        return maximumOnlineUsers;
    }

    public static boolean acceptAlternativeId() {
        return acceptAlternativeId;
    }

    public static Path dataPackDirectory() {
        return dataPackDirectory;
    }

    public static int scheduledPoolSize() {
        return scheduledPoolSize;
    }

    public static int threadPoolSize() {
        return threadPoolSize;
    }

    public static int maxThreadPoolSize() {
        return maxThreadPoolSize;
    }

    public static int parallelismThreshold() {
        return parallelismThreshold;
    }

    public static int[] acceptedProtocols() {
        return acceptedProtocols;
    }

    public static boolean scheduleRestart() {
        return scheduleRestart;
    }

    public static String[] scheduleRestartHours() {
        return scheduleRestartHours;
    }

    public static boolean useDeadLockDetector() {
        return useDeadLockDetector;
    }

    public static int deadLockDetectorInterval() {
        return deadLockDetectorInterval;
    }

    public static boolean restartOnDeadLock() {
        return restartOnDeadLock;
    }

    public static boolean acceptPlayerName(String name) {
        return playerNamePattern.test(name);
    }

    public static boolean acceptPetName(String name) {
        return petNamePattern.test(name);
    }

    public static boolean acceptClanName(String name) {
        return clanNamePattern.test(name);
    }

    public static int maxPlayersAllowed() {
        return maxPlayers;
    }

    public static boolean allowPlayersCount(int playerCount) {
        return maxPlayers <= 0 || maxPlayers >= playerCount;
    }

    public static void setAgeLimit(byte age) {
        ageLimit = age;
    }

    public static boolean isHardwareInfoEnabled() {
        return hardwareInfoEnabled;
    }

    public static int maxPlayerPerHWID() {
        return maxPlayerPerHWID;
    }
}
