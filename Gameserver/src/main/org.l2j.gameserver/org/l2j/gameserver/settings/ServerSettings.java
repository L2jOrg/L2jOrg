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
import org.l2j.gameserver.ServerType;

import java.nio.file.Path;

/**
 * @author JoeAlisson
 */
public class ServerSettings implements Settings {

    private int serverId;
    private boolean acceptAlternativeId;
    private String authServerAddress;
    private short authServerPort;

    private byte ageLimit;
    private boolean showBrackets;
    private boolean isPvP;
    private int type;
    private short port;
    private int maximumOnlineUsers;
    private Path dataPackDirectory;

    private int scheduledPoolSize;
    private int threadPoolSize;
    private int[] acceptedProtocols;
    private boolean scheduleRestart;
    private boolean useDeadLockDetector;
    private int deadLockDetectorInterval;
    private boolean restartOnDeadLock;
    private int maxPlayers;

    @Override
    public void load(SettingsFile settingsFile) {
        serverId = settingsFile.getInteger("RequestServerID", 1);
        acceptAlternativeId = settingsFile.getBoolean("AcceptAlternateID", true);

        authServerAddress = settingsFile.getString("LoginHost", "127.0.0.1");
        authServerPort = settingsFile.getShort("LoginPort", (short) 9014);

        port = settingsFile.getShort("GameserverPort", (short) 7777);

        type = ServerType.maskOf(settingsFile.getStringArray("ServerListType"));

        maximumOnlineUsers = Math.max(1, settingsFile.getInteger("MaximumOnlineUsers", 20));
        ageLimit = settingsFile.getByte("ServerListAge", (byte) 0);
        showBrackets = settingsFile.getBoolean("ServerListBrackets", false);
        isPvP = settingsFile.getBoolean("PvPServer", false);

        dataPackDirectory = Path.of(settingsFile.getString("DatapackRoot", "."));

        var processors = Runtime.getRuntime().availableProcessors();

        scheduledPoolSize = determinePoolSize(settingsFile, "ScheduledThreadPoolSize", processors);
        threadPoolSize = determinePoolSize(settingsFile, "ThreadPoolSize", processors);
        acceptedProtocols =  settingsFile.getIntegerArray("AllowedProtocolRevisions", ";");

        scheduleRestart = settingsFile.getBoolean("ServerRestartScheduleEnabled", false);

        useDeadLockDetector = settingsFile.getBoolean("DeadLockDetector", true);
        deadLockDetectorInterval = settingsFile.getInteger("DeadLockCheckInterval", 1800);
        restartOnDeadLock = settingsFile.getBoolean("RestartOnDeadlock", false);
    }

    private int determinePoolSize(SettingsFile settingsFile, String property, int processors) {
        var size = settingsFile.getInteger(property, processors);

        if(size < 2) {
            return processors;
        }
        return size;
    }

    public int serverId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public short port() {
        return port;
    }

    public String authServerAddress() {
        return authServerAddress;
    }

    public int authServerPort() {
        return authServerPort;
    }

    public byte ageLimit() {
        return ageLimit;
    }

    public boolean isShowingBrackets() {
        return showBrackets;
    }

    public boolean isPvP() {
        return isPvP;
    }

    public int type() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int maximumOnlineUsers() {
        return maximumOnlineUsers;
    }

    public boolean acceptAlternativeId() {
        return acceptAlternativeId;
    }

    public Path dataPackDirectory() {
        return dataPackDirectory;
    }

    public int scheduledPoolSize() {
        return scheduledPoolSize;
    }

    public int threadPoolSize() {
        return threadPoolSize;
    }

    public int[] acceptedProtocols() {
        return acceptedProtocols;
    }

    public boolean scheduleRestart() {
        return scheduleRestart;
    }

    public boolean useDeadLockDetector() {
        return useDeadLockDetector;
    }

    public int deadLockDetectorInterval() {
        return deadLockDetectorInterval;
    }

    public boolean restartOnDeadLock() {
        return restartOnDeadLock;
    }


}
