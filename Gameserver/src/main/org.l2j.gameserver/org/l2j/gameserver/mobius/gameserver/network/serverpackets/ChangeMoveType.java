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
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

public class ChangeMoveType implements IClientOutgoingPacket
{
	public static final int WALK = 0;
	public static final int RUN = 1;
	
	private final int _charObjId;
	private final boolean _running;
	
	public ChangeMoveType(L2Character character)
	{
		_charObjId = character.getObjectId();
		_running = character.isRunning();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.CHANGE_MOVE_TYPE.writeId(packet);
		
		packet.writeD(_charObjId);
		packet.writeD(_running ? RUN : WALK);
		packet.writeD(0); // c2
		return true;
	}
}
