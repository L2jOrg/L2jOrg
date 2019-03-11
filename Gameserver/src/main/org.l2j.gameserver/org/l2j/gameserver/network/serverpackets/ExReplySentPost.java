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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.model.itemcontainer.ItemContainer;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author Migi, DS
 */
public class ExReplySentPost extends AbstractItemPacket {
    private final Message _msg;
    private Collection<L2ItemInstance> _items = null;

    public ExReplySentPost(Message msg) {
        _msg = msg;
        if (msg.hasAttachments()) {
            final ItemContainer attachments = msg.getAttachments();
            if ((attachments != null) && (attachments.getSize() > 0)) {
                _items = attachments.getItems();
            } else {
                LOGGER.warn("Message " + msg.getId() + " has attachments but itemcontainer is empty.");
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_REPLY_SENT_POST.writeId(packet);

        packet.putInt(0x00); // GOD
        packet.putInt(_msg.getId());
        packet.putInt(_msg.isLocked() ? 1 : 0);
        writeString(_msg.getReceiverName(), packet);
        writeString(_msg.getSubject(), packet);
        writeString(_msg.getContent(), packet);

        if ((_items != null) && !_items.isEmpty()) {
            packet.putInt(_items.size());
            for (L2ItemInstance item : _items) {
                writeItem(packet, item);
                packet.putInt(item.getObjectId());
            }
        } else {
            packet.putInt(0x00);
        }
        packet.putLong(_msg.getReqAdena());
        packet.putInt(_msg.hasAttachments() ? 0x01 : 0x00);
        packet.putInt(_msg.isReturned() ? 0x01 : 00);
    }
}
