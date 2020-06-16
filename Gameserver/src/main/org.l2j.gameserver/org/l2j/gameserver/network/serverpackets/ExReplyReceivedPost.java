/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.SystemMessageId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * @author Migi, DS
 */
public class ExReplyReceivedPost extends AbstractItemPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExReplyReceivedPost.class);

    private final MailData _msg;
    private Collection<Item> _items = null;

    public ExReplyReceivedPost(MailData msg) {
        _msg = msg;
        if (msg.hasAttachments()) {
            final ItemContainer attachments = msg.getAttachment();
            if ((attachments != null) && (attachments.getSize() > 0)) {
                _items = attachments.getItems();
            } else {
                LOGGER.warn("Message {} has attachments but itemcontainer is empty.", msg.getId());
            }
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_REPLY_RECEIVED_POST);

        writeInt(_msg.getType().ordinal()); // GOD
        if (_msg.getType() == MailType.COMMISSION_ITEM_RETURNED) {
            writeInt(SystemMessageId.THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED.getId());
            writeInt(SystemMessageId.THE_AUCTION_HOUSE_REGISTRATION_PERIOD_HAS_EXPIRED_AND_THE_CORRESPONDING_ITEM_IS_BEING_FORWARDED.getId());
        } else if (_msg.getType() == MailType.COMMISSION_ITEM_SOLD) {
            writeInt(_msg.getItem());
            writeInt(_msg.getEnchant());
            for (int i = 0; i < AttributeType.ATTRIBUTE_TYPES.length; i++) {
                writeInt(0);
            }
            writeInt(SystemMessageId.THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD.getId());
            writeInt(SystemMessageId.S1_HAS_BEEN_SOLD.getId());
        }
        writeInt(_msg.getId());
        writeInt(_msg.isLocked() ? 1 : 0);
        writeInt(0x00); // Unknown
        writeString(_msg.getSenderName());
        writeString(_msg.getSubject());
        writeString(_msg.getContent());

        if ((_items != null) && !_items.isEmpty()) {
            writeInt(_items.size());
            for (Item item : _items) {
                writeItem(item);
                writeInt(item.getObjectId());
            }
        } else {
            writeInt(0x00);
        }

        writeLong(_msg.getFee());
        writeInt(_msg.hasAttachments() ? 1 : 0);
        writeInt(_msg.isReturned() ? 1 : 0);
    }

}
