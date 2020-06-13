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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ClientHardwareInfoHolder;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.world.World;

/**
 * @author Mobius
 */
public final class RequestHardWareInfo extends ClientPacket {
    private String _macAddress;
    private int _windowsPlatformId;
    private int _windowsMajorVersion;
    private int _windowsMinorVersion;
    private int _windowsBuildNumber;
    private int _directxVersion;
    private int _directxRevision;
    private String _cpuName;
    private int _cpuSpeed;
    private int _cpuCoreCount;
    private int _vgaCount;
    private int _vgaPcxSpeed;
    private int _physMemorySlot1;
    private int _physMemorySlot2;
    private int _physMemorySlot3;
    private int _videoMemory;
    private int _vgaVersion;
    private String _vgaName;
    private String _vgaDriverVersion;

    @Override
    public void readImpl() {
        _macAddress = readString();
        _windowsPlatformId = readInt();
        _windowsMajorVersion = readInt();
        _windowsMinorVersion = readInt();
        _windowsBuildNumber = readInt();
        _directxVersion = readInt();
        _directxRevision = readInt();
        readBytes(new byte[16]);
        _cpuName = readString();
        _cpuSpeed = readInt();
        _cpuCoreCount = readByte();
        readInt();
        _vgaCount = readInt();
        _vgaPcxSpeed = readInt();
        _physMemorySlot1 = readInt();
        _physMemorySlot2 = readInt();
        _physMemorySlot3 = readInt();
        readByte();
        _videoMemory = readInt();
        readInt();
        _vgaVersion = readShort();
        _vgaName = readString();
        _vgaDriverVersion = readString();
    }

    @Override
    public void runImpl() {
        client.setHardwareInfo(new ClientHardwareInfoHolder(_macAddress, _windowsPlatformId, _windowsMajorVersion, _windowsMinorVersion, _windowsBuildNumber, _directxVersion, _directxRevision, _cpuName, _cpuSpeed, _cpuCoreCount, _vgaCount, _vgaPcxSpeed, _physMemorySlot1, _physMemorySlot2, _physMemorySlot3, _videoMemory, _vgaVersion, _vgaName, _vgaDriverVersion));
        if (Config.HARDWARE_INFO_ENABLED && (Config.MAX_PLAYERS_PER_HWID > 0)) {
            int count = 0;
            for (Player player : World.getInstance().getPlayers()) {
                if ((player.isOnlineInt() == 1) && (player.getClient().getHardwareInfo().equals(client.getHardwareInfo()))) {
                    count++;
                }
            }
            if (count >= Config.MAX_PLAYERS_PER_HWID) {
                Disconnection.of(client).defaultSequence(false);
                return;
            }
        }
    }
}
