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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ClientHardwareInfoHolder;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.world.World;

/**
 * @author Mobius
 */
public final class RequestHardWareInfo extends ClientPacket {
    private String macAddress;
    private int windowsPlatformId;
    private int windowsMajorVersion;
    private int windowsMinorVersion;
    private int windowsBuildNumber;
    private String cpuName;
    private int cpuSpeed;
    private int cpuCoreCount;
    private String vgaName;
    private String vgaDriverVersion;

    @Override
    public void readImpl() {
        macAddress = readString();
        windowsPlatformId = readInt();
        windowsMajorVersion = readInt();
        windowsMinorVersion = readInt();
        windowsBuildNumber = readInt();
        readInt(); // directx Version
        readInt(); // directxRevision
        readBytes(new byte[16]);
        cpuName = readString();
        cpuSpeed = readInt();
        cpuCoreCount = readByte();
        readInt();
        readInt(); // vga count
        readInt(); // _vgaPcxSpeed
        readInt(); // _physMemorySlot1
        readInt(); // _physMemorySlot2
        readInt(); // _physMemorySlot3
        readByte();
        readInt(); // _videoMemory
        readInt();
        readShort(); // _vgaVersion
        vgaName = readString();
        vgaDriverVersion = readString();
    }

    @Override
    public void runImpl() {
        var hardwareInfo = new ClientHardwareInfoHolder(macAddress)
                .withWindows(windowsPlatformId, windowsMajorVersion, windowsMinorVersion, windowsBuildNumber)
                .withCPU(cpuName, cpuSpeed, cpuCoreCount)
                .withVideo(vgaName, vgaDriverVersion);

        client.setHardwareInfo(hardwareInfo);

        if (ServerSettings.isHardwareInfoEnabled() && ServerSettings.maxPlayerPerHWID() > 0) {
            int count = 0;
            for (Player player : World.getInstance().getPlayers()) {
                if (player.isOnline() && (player.getClient().getHardwareInfo().equals(client.getHardwareInfo()))) {
                    count++;
                }
            }
            if (count >= ServerSettings.maxPlayerPerHWID()) {
                Disconnection.of(client).logout(false);
            }
        }
    }
}
