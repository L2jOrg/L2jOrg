package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.model.entity.Message;
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

    private final Message _msg;
    private Collection<Item> _items = null;

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
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_REPLY_RECEIVED_POST);

        writeInt(_msg.getMailType().ordinal()); // GOD
        if (_msg.getMailType() == MailType.COMMISSION_ITEM_RETURNED) {
            writeInt(SystemMessageId.THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED.getId());
            writeInt(SystemMessageId.THE_AUCTION_HOUSE_REGISTRATION_PERIOD_HAS_EXPIRED_AND_THE_CORRESPONDING_ITEM_IS_BEING_FORWARDED.getId());
        } else if (_msg.getMailType() == MailType.COMMISSION_ITEM_SOLD) {
            writeInt(_msg.getItemId());
            writeInt(_msg.getEnchantLvl());
            for (int i = 0; i < 6; i++) {
                writeInt(_msg.getElementals()[i]);
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

        writeLong(_msg.getReqAdena());
        writeInt(_msg.hasAttachments() ? 1 : 0);
        writeInt(_msg.isReturned() ? 1 : 0);
    }

}
