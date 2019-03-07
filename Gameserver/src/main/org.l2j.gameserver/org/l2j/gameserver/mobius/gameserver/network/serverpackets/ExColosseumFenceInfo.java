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
import com.l2jmobius.gameserver.enums.FenceState;
import com.l2jmobius.gameserver.model.actor.instance.L2FenceInstance;
import com.l2jmobius.gameserver.network.OutgoingPackets;

/**
 * @author HoridoJoho / FBIagent
 */
public class ExColosseumFenceInfo implements IClientOutgoingPacket
{
	private final int _objId;
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _width;
	private final int _length;
	private final int _clientState;
	
	public ExColosseumFenceInfo(L2FenceInstance fence)
	{
		this(fence.getObjectId(), fence.getX(), fence.getY(), fence.getZ(), fence.getWidth(), fence.getLength(), fence.getState());
	}
	
	public ExColosseumFenceInfo(int objId, double x, double y, double z, int width, int length, FenceState state)
	{
		_objId = objId;
		_x = (int) x;
		_y = (int) y;
		_z = (int) z;
		_width = width;
		_length = length;
		_clientState = state.getClientId();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_COLOSSEUM_FENCE_INFO.writeId(packet);
		
		packet.writeD(_objId);
		packet.writeD(_clientState);
		packet.writeD(_x);
		packet.writeD(_y);
		packet.writeD(_z);
		packet.writeD(_width);
		packet.writeD(_length);
		
		return true;
	}
}