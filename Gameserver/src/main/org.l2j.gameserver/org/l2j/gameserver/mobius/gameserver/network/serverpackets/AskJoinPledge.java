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

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.OutgoingPackets;

public final class AskJoinPledge implements IClientOutgoingPacket
{
	private final L2PcInstance _requestor;
	private final int _pledgeType;
	private final String _pledgeName;
	
	public AskJoinPledge(L2PcInstance requestor, int pledgeType, String pledgeName)
	{
		_requestor = requestor;
		_pledgeType = pledgeType;
		_pledgeName = pledgeName;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.ASK_JOIN_PLEDGE.writeId(packet);
		packet.writeD(_requestor.getObjectId());
		packet.writeS(_requestor.getName());
		packet.writeS(_pledgeName);
		if (_pledgeType != 0)
		{
			packet.writeD(_pledgeType);
		}
		return true;
	}
}
