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
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author -Wooden-, Sdw
 */
public class ExPledgeEmblem implements IClientOutgoingPacket
{
	private final int _crestId;
	private final int _clanId;
	private final byte[] _data;
	private final int _chunkId;
	private static final int TOTAL_SIZE = 65664;
	
	public ExPledgeEmblem(int crestId, byte[] chunkedData, int clanId, int chunkId)
	{
		_crestId = crestId;
		_data = chunkedData;
		_clanId = clanId;
		_chunkId = chunkId;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_PLEDGE_EMBLEM.writeId(packet);
		
		packet.writeD(Config.SERVER_ID);
		packet.writeD(_clanId);
		packet.writeD(_crestId);
		packet.writeD(_chunkId);
		packet.writeD(TOTAL_SIZE);
		if (_data != null)
		{
			packet.writeD(_data.length);
			packet.writeB(_data);
		}
		else
		{
			packet.writeD(0);
		}
		return true;
	}
}