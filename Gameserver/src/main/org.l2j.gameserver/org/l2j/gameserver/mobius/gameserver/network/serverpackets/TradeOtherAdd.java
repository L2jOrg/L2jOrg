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
import org.l2j.gameserver.mobius.gameserver.model.TradeItem;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author Yme
 */
public final class TradeOtherAdd extends AbstractItemPacket
{
	private final int _sendType;
	private final TradeItem _item;
	
	public TradeOtherAdd(int sendType, TradeItem item)
	{
		_sendType = sendType;
		_item = item;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.TRADE_OTHER_ADD.writeId(packet);
		packet.writeC(_sendType);
		if (_sendType == 2)
		{
			packet.writeD(0x01);
		}
		packet.writeD(0x01);
		writeItem(packet, _item);
		return true;
	}
}
