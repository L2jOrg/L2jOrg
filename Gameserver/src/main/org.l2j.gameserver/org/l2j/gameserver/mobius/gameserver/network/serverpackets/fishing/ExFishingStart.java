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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets.fishing;

import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author -Wooden-
 */
public class ExFishingStart implements IClientOutgoingPacket
{
	private final L2PcInstance _player;
	private final int _fishType;
	private final int _baitType;
	private final ILocational _baitLocation;
	
	/**
	 * @param player
	 * @param fishType
	 * @param baitType - 0 = newbie, 1 = normal, 2 = night
	 * @param baitLocation
	 */
	public ExFishingStart(L2PcInstance player, int fishType, int baitType, ILocational baitLocation)
	{
		_player = player;
		_fishType = fishType;
		_baitType = baitType;
		_baitLocation = baitLocation;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_FISHING_START.writeId(packet);
		packet.writeD(_player.getObjectId());
		packet.writeC(_fishType);
		packet.writeD(_baitLocation.getX());
		packet.writeD(_baitLocation.getY());
		packet.writeD(_baitLocation.getZ());
		packet.writeC(_baitType);
		return true;
	}
}
