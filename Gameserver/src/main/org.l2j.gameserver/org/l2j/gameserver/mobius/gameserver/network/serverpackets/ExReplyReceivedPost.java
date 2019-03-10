package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.enums.MailType;
import org.l2j.gameserver.mobius.gameserver.model.entity.Message;
import org.l2j.gameserver.mobius.gameserver.model.itemcontainer.ItemContainer;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author Migi, DS
 */
public class ExReplyReceivedPost extends AbstractItemPacket {
    private final Message _msg;
    private Collection<L2ItemInstance> _items = null;

    public ExReplyReceivedPost(Message msg) {
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
        OutgoingPackets.EX_REPLY_RECEIVED_POST.writeId(packet);

        packet.putInt(_msg.getMailType().ordinal()); // GOD
        if (_msg.getMailType() == MailType.COMMISSION_ITEM_RETURNED) {
            packet.putInt(SystemMessageId.THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED.getId());
            packet.putInt(SystemMessageId.THE_AUCTION_HOUSE_REGISTRATION_PERIOD_HAS_EXPIRED_AND_THE_CORRESPONDING_ITEM_IS_BEING_FORWARDED.getId());
        } else if (_msg.getMailType() == MailType.COMMISSION_ITEM_SOLD) {
            packet.putInt(_msg.getItemId());
            packet.putInt(_msg.getEnchantLvl());
            for (int i = 0; i < 6; i++) {
                packet.putInt(_msg.getElementals()[i]);
            }
            packet.putInt(SystemMessageId.THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD.getId());
            packet.putInt(SystemMessageId.S1_HAS_BEEN_SOLD.getId());
        }
        packet.putInt(_msg.getId());
        packet.putInt(_msg.isLocked() ? 1 : 0);
        packet.putInt(0x00); // Unknown
        writeString(_msg.getSenderName(), packet);
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
        packet.putInt(_msg.hasAttachments() ? 1 : 0);
        packet.putInt(_msg.isReturned() ? 1 : 0);
    }
}
