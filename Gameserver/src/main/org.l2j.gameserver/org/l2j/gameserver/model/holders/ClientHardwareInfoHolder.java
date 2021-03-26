/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.holders;

/**
 * @author Mobius
 */
public class ClientHardwareInfoHolder {
    private final String _macAddress;
    private int windowsPlatformId;
    private int windowsMajorVersion;
    private int windowsMinorVersion;
    private int windowsBuildNumber;
    private String cpuName;
    private int cpuSpeed;
    private int cpuCoreCount;
    private String vgaName;
    private String vgaDriverVersion;

    public ClientHardwareInfoHolder(String macAddress) {
        _macAddress = macAddress;
    }

    public ClientHardwareInfoHolder withWindows(int platform, int majorVersion, int minorVersion, int build) {
        windowsPlatformId = platform;
        windowsMajorVersion = majorVersion;
        windowsMinorVersion = minorVersion;
        windowsBuildNumber = build;
        return this;
    }

    public ClientHardwareInfoHolder withCPU(String name, int speed, int cores) {
        cpuName = name;
        cpuSpeed = speed;
        cpuCoreCount = cores;
        return this;
    }

    public ClientHardwareInfoHolder withVideo(String name, String version ) {
        vgaName = name;
        vgaDriverVersion = version;
        return this;
    }

    /**
     * @return the macAddress
     */
    public String getMacAddress() {
        return _macAddress;
    }

    /**
     * @return the windowsPlatformId
     */
    public int getWindowsPlatformId() {
        return windowsPlatformId;
    }

    /**
     * @return the windowsMajorVersion
     */
    public int getWindowsMajorVersion() {
        return windowsMajorVersion;
    }

    /**
     * @return the windowsMinorVersion
     */
    public int getWindowsMinorVersion() {
        return windowsMinorVersion;
    }

    /**
     * @return the windowsBuildNumber
     */
    public int getWindowsBuildNumber() {
        return windowsBuildNumber;
    }

    /**
     * @return the cpuName
     */
    public String getCpuName() {
        return cpuName;
    }

    /**
     * @return the cpuSpeed
     */
    public int getCpuSpeed() {
        return cpuSpeed;
    }

    /**
     * @return the cpuCoreCount
     */
    public int getCpuCoreCount() {
        return cpuCoreCount;
    }

    /**
     * @return the vgaName
     */
    public String getVgaName() {
        return vgaName;
    }

    /**
     * @return the vgaDriverVersion
     */
    public String getVgaDriverVersion() {
        return vgaDriverVersion;
    }
}