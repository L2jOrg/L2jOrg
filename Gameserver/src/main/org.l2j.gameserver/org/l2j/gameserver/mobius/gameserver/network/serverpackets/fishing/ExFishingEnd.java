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
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author -Wooden-
 */
public class ExFishingEnd implements IClientOutgoingPacket
{
	public enum FishingEndReason
	{
		LOSE(0),
		WIN(1),
		STOP(2);
		
		private final int _reason;
		
		FishingEndReason(int reason)
		{
			_reason = reason;
		}
		
		public int getReason()
		{
			return _reason;
		}
	}
	
	public enum FishingEndType
	{
		PLAYER_STOP,
		PLAYER_CANCEL,
		ERROR;
	}
	
	private final L2PcInstance _player;
	private final FishingEndReason _reason;
	
	public ExFishingEnd(L2PcInstance player, FishingEndReason reason)
	{
		_player = player;
		_reason = reason;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_FISHING_END.writeId(packet);
		packet.writeD(_player.getObjectId());
		packet.writeC(_reason.getReason());
		return true;
	}
}
