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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import com.l2jmobius.Config;
import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author UnAfraid
 */
public class ExWorldChatCnt implements IClientOutgoingPacket
{
	private final int _points;
	
	public ExWorldChatCnt(L2PcInstance activeChar)
	{
		_points = activeChar.getLevel() < Config.WORLD_CHAT_MIN_LEVEL ? 0 : Math.max(activeChar.getWorldChatPoints() - activeChar.getWorldChatUsed(), 0);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_WORLD_CHAT_CNT.writeId(packet);
		
		packet.writeD(_points);
		return true;
	}
}
