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
import org.l2j.gameserver.mobius.gameserver.enums.MailType;
import org.l2j.gameserver.mobius.gameserver.model.entity.Message;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.ItemContainer;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;

import java.util.Collection;

/**
 * @author Migi, DS
 */
public class ExReplyReceivedPost extends AbstractItemPacket
{
	private final Message _msg;
	private Collection<L2ItemInstance> _items = null;
	
	public ExReplyReceivedPost(Message msg)
	{
		_msg = msg;
		if (msg.hasAttachments())
		{
			final ItemContainer attachments = msg.getAttachments();
			if ((attachments != null) && (attachments.getSize() > 0))
			{
				_items = attachments.getItems();
			}
			else
			{
				LOGGER.warning("Message " + msg.getId() + " has attachments but itemcontainer is empty.");
			}
		}
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_REPLY_RECEIVED_POST.writeId(packet);
		
		packet.writeD(_msg.getMailType().ordinal()); // GOD
		if (_msg.getMailType() == MailType.COMMISSION_ITEM_RETURNED)
		{
			packet.writeD(SystemMessageId.THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED.getId());
			packet.writeD(SystemMessageId.THE_AUCTION_HOUSE_REGISTRATION_PERIOD_HAS_EXPIRED_AND_THE_CORRESPONDING_ITEM_IS_BEING_FORWARDED.getId());
		}
		else if (_msg.getMailType() == MailType.COMMISSION_ITEM_SOLD)
		{
			packet.writeD(_msg.getItemId());
			packet.writeD(_msg.getEnchantLvl());
			for (int i = 0; i < 6; i++)
			{
				packet.writeD(_msg.getElementals()[i]);
			}
			packet.writeD(SystemMessageId.THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD.getId());
			packet.writeD(SystemMessageId.S1_HAS_BEEN_SOLD.getId());
		}
		packet.writeD(_msg.getId());
		packet.writeD(_msg.isLocked() ? 1 : 0);
		packet.writeD(0x00); // Unknown
		packet.writeS(_msg.getSenderName());
		packet.writeS(_msg.getSubject());
		packet.writeS(_msg.getContent());
		
		if ((_items != null) && !_items.isEmpty())
		{
			packet.writeD(_items.size());
			for (L2ItemInstance item : _items)
			{
				writeItem(packet, item);
				packet.writeD(item.getObjectId());
			}
		}
		else
		{
			packet.writeD(0x00);
		}
		
		packet.writeQ(_msg.getReqAdena());
		packet.writeD(_msg.hasAttachments() ? 1 : 0);
		packet.writeD(_msg.isReturned() ? 1 : 0);
		return true;
	}
}
