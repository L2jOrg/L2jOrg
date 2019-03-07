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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets.pledgebonus;

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.enums.ClanRewardType;
import com.l2jmobius.gameserver.network.OutgoingPackets;
import com.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author UnAfraid
 */
public class ExPledgeBonusUpdate implements IClientOutgoingPacket
{
	private final ClanRewardType _type;
	private final int _value;
	
	public ExPledgeBonusUpdate(ClanRewardType type, int value)
	{
		_type = type;
		_value = value;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_PLEDGE_BONUS_UPDATE.writeId(packet);
		packet.writeC(_type.getClientId());
		packet.writeD(_value);
		return true;
	}
}
