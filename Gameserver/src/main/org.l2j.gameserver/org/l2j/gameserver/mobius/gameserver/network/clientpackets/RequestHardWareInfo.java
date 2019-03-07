/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import com.l2jmobius.Config;
import com.l2jmobius.commons.network.PacketReader;
import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.ClientHardwareInfoHolder;
import com.l2jmobius.gameserver.network.Disconnection;
import com.l2jmobius.gameserver.network.L2GameClient;

/**
 * @author Mobius
 */
public final class RequestHardWareInfo implements IClientIncomingPacket
{
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
	public boolean read(L2GameClient client, PacketReader packet)
	{
		_macAddress = packet.readS();
		_windowsPlatformId = packet.readD();
		_windowsMajorVersion = packet.readD();
		_windowsMinorVersion = packet.readD();
		_windowsBuildNumber = packet.readD();
		_directxVersion = packet.readD();
		_directxRevision = packet.readD();
		packet.readB(16);
		_cpuName = packet.readS();
		_cpuSpeed = packet.readD();
		_cpuCoreCount = packet.readC();
		packet.readD();
		_vgaCount = packet.readD();
		_vgaPcxSpeed = packet.readD();
		_physMemorySlot1 = packet.readD();
		_physMemorySlot2 = packet.readD();
		_physMemorySlot3 = packet.readD();
		packet.readC();
		_videoMemory = packet.readD();
		packet.readD();
		_vgaVersion = packet.readH();
		_vgaName = packet.readS();
		_vgaDriverVersion = packet.readS();
		return true;
	}
	
	@Override
	public void run(L2GameClient client)
	{
		client.setHardwareInfo(new ClientHardwareInfoHolder(_macAddress, _windowsPlatformId, _windowsMajorVersion, _windowsMinorVersion, _windowsBuildNumber, _directxVersion, _directxRevision, _cpuName, _cpuSpeed, _cpuCoreCount, _vgaCount, _vgaPcxSpeed, _physMemorySlot1, _physMemorySlot2, _physMemorySlot3, _videoMemory, _vgaVersion, _vgaName, _vgaDriverVersion));
		if (Config.HARDWARE_INFO_ENABLED && (Config.MAX_PLAYERS_PER_HWID > 0))
		{
			int count = 0;
			for (L2PcInstance player : L2World.getInstance().getPlayers())
			{
				if ((player.isOnlineInt() == 1) && (player.getClient().getHardwareInfo().equals(client.getHardwareInfo())))
				{
					count++;
				}
			}
			if (count >= Config.MAX_PLAYERS_PER_HWID)
			{
				Disconnection.of(client).defaultSequence(false);
				return;
			}
		}
	}
}
