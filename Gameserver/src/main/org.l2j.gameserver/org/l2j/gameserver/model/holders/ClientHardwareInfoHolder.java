/*
 * Copyright © 2019 L2J Mobius
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
    private final int _directxVersion;
    private final int _directxRevision;
    private final String _cpuName;
    private final int _cpuSpeed;
    private final int _cpuCoreCount;
    private final int _vgaCount;
    private final int _vgaPcxSpeed;
    private final int _physMemorySlot1;
    private final int _physMemorySlot2;
    private final int _physMemorySlot3;
    private final int _videoMemory;
    private final int _vgaVersion;
    private final String _vgaName;
    private final String _vgaDriverVersion;

    public ClientHardwareInfoHolder(String macAddress, int windowsPlatformId, int windowsMajorVersion, int windowsMinorVersion, int windowsBuildNumber, int directxVersion, int directxRevision, String cpuName, int cpuSpeed, int cpuCoreCount, int vgaCount, int vgaPcxSpeed, int physMemorySlot1, int physMemorySlot2, int physMemorySlot3, int videoMemory, int vgaVersion, String vgaName, String vgaDriverVersion) {
        _macAddress = macAddress;
        _windowsPlatformId = windowsPlatformId;
        _windowsMajorVersion = windowsMajorVersion;
        _windowsMinorVersion = windowsMinorVersion;
        _windowsBuildNumber = windowsBuildNumber;
        _directxVersion = directxVersion;
        _directxRevision = directxRevision;
        _cpuName = cpuName;
        _cpuSpeed = cpuSpeed;
        _cpuCoreCount = cpuCoreCount;
        _vgaCount = vgaCount;
        _vgaPcxSpeed = vgaPcxSpeed;
        _physMemorySlot1 = physMemorySlot1;
        _physMemorySlot2 = physMemorySlot2;
        _physMemorySlot3 = physMemorySlot3;
        _videoMemory = videoMemory;
        _vgaVersion = vgaVersion;
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
     * @return the directxVersion
     */
    public int getDirectxVersion() {
        return _directxVersion;
    }

    /**
     * @return the directxRevision
     */
    public int getDirectxRevision() {
        return _directxRevision;
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
     * @return the vgaCount
     */
    public int getVgaCount() {
        return _vgaCount;
    }

    /**
     * @return the vgaPcxSpeed
     */
    public int getVgaPcxSpeed() {
        return _vgaPcxSpeed;
    }

    /**
     * @return the physMemorySlot1
     */
    public int getPhysMemorySlot1() {
        return _physMemorySlot1;
    }

    /**
     * @return the physMemorySlot2
     */
    public int getPhysMemorySlot2() {
        return _physMemorySlot2;
    }

    /**
     * @return the physMemorySlot3
     */
    public int getPhysMemorySlot3() {
        return _physMemorySlot3;
    }

    /**
     * @return the videoMemory
     */
    public int getVideoMemory() {
        return _videoMemory;
    }

    /**
     * @return the vgaVersion
     */
    public int getVgaVersion() {
        return _vgaVersion;
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