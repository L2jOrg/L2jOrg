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
    private final int _windowsPlatformId;
    private final int _windowsMajorVersion;
    private final int _windowsMinorVersion;
    private final int _windowsBuildNumber;
    private final String _cpuName;
    private final int _cpuSpeed;
    private final int _cpuCoreCount;
    private final String _vgaName;
    private final String _vgaDriverVersion;

    public ClientHardwareInfoHolder(String macAddress, int windowsPlatformId, int windowsMajorVersion, int windowsMinorVersion, int windowsBuildNumber, String cpuName, int cpuSpeed, int cpuCoreCount, String vgaName, String vgaDriverVersion) {
        _macAddress = macAddress;
        _windowsPlatformId = windowsPlatformId;
        _windowsMajorVersion = windowsMajorVersion;
        _windowsMinorVersion = windowsMinorVersion;
        _windowsBuildNumber = windowsBuildNumber;
        _cpuName = cpuName;
        _cpuSpeed = cpuSpeed;
        _cpuCoreCount = cpuCoreCount;
        _vgaName = vgaName;
        _vgaDriverVersion = vgaDriverVersion;
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
        return _windowsPlatformId;
    }

    /**
     * @return the windowsMajorVersion
     */
    public int getWindowsMajorVersion() {
        return _windowsMajorVersion;
    }

    /**
     * @return the windowsMinorVersion
     */
    public int getWindowsMinorVersion() {
        return _windowsMinorVersion;
    }

    /**
     * @return the windowsBuildNumber
     */
    public int getWindowsBuildNumber() {
        return _windowsBuildNumber;
    }

    /**
     * @return the cpuName
     */
    public String getCpuName() {
        return _cpuName;
    }

    /**
     * @return the cpuSpeed
     */
    public int getCpuSpeed() {
        return _cpuSpeed;
    }

    /**
     * @return the cpuCoreCount
     */
    public int getCpuCoreCount() {
        return _cpuCoreCount;
    }

    /**
     * @return the vgaName
     */
    public String getVgaName() {
        return _vgaName;
    }

    /**
     * @return the vgaDriverVersion
     */
    public String getVgaDriverVersion() {
        return _vgaDriverVersion;
    }
}