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

import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

public class MonRaceInfo implements IClientOutgoingPacket
{
	private final int _unknown1;
	private final int _unknown2;
	private final L2Npc[] _monsters;
	private final int[][] _speeds;
	
	public MonRaceInfo(int unknown1, int unknown2, L2Npc[] monsters, int[][] speeds)
	{
		/*
		 * -1 0 to initial the race 0 15322 to start race 13765 -1 in middle of race -1 0 to end the race
		 */
		_unknown1 = unknown1;
		_unknown2 = unknown2;
		_monsters = monsters;
		_speeds = speeds;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.MON_RACE_INFO.writeId(packet);
		
		packet.writeD(_unknown1);
		packet.writeD(_unknown2);
		packet.writeD(0x08);
		
		for (int i = 0; i < 8; i++)
		{
			packet.writeD(_monsters[i].getObjectId()); // npcObjectID
			packet.writeD(_monsters[i].getTemplate().getId() + 1000000); // npcID
			packet.writeD(14107); // origin X
			packet.writeD(181875 + (58 * (7 - i))); // origin Y
			packet.writeD(-3566); // origin Z
			packet.writeD(12080); // end X
			packet.writeD(181875 + (58 * (7 - i))); // end Y
			packet.writeD(-3566); // end Z
			packet.writeF(_monsters[i].getTemplate().getfCollisionHeight()); // coll. height
			packet.writeF(_monsters[i].getTemplate().getfCollisionRadius()); // coll. radius
			packet.writeD(120); // ?? unknown
			for (int j = 0; j < 20; j++)
			{
				if (_unknown1 == 0)
				{
					packet.writeC(_speeds[i][j]);
				}
				else
				{
					packet.writeC(0x00);
				}
			}
		}
		return true;
	}
}
