package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.SystemMessageId;

import java.util.List;

/**
 * @author Migi, DS
 */
public class ExShowReceivedPostList extends ServerPacket {
    private static final int MESSAGE_FEE = 100;
    private static final int MESSAGE_FEE_PER_SLOT = 1000;
    private final List<Message> _inbox;

    public ExShowReceivedPostList(int objectId) {
        _inbox = MailManager.getInstance().getInbox(objectId);
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_SHOW_RECEIVED_POST_LIST);

        writeInt((int) (System.currentTimeMillis() / 1000));
        if ((_inbox != null) && (_inbox.size() > 0)) {
            writeInt(_inbox.size());
            for (Message msg : _inbox) {
                writeInt(msg.getMailType().ordinal());
                if (msg.getMailType() == MailType.COMMISSION_ITEM_SOLD) {
                    writeInt(SystemMessageId.THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD.getId());
                } else if (msg.getMailType() == MailType.COMMISSION_ITEM_RETURNED) {
                    writeInt(SystemMessageId.THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED.getId());
                }
                writeInt(msg.getId());
                writeString(msg.getSubject());
                writeString(msg.getSenderName());
                writeInt(msg.isLocked() ? 0x01 : 0x00);
                writeInt(msg.getExpirationSeconds());
                writeInt(msg.isUnread() ? 0x01 : 0x00);
                writeInt(((msg.getMailType() == MailType.COMMISSION_ITEM_SOLD) || (msg.getMailType() == MailType.COMMISSION_ITEM_RETURNED)) ? 0 : 1);
                writeInt(msg.hasAttachments() ? 0x01 : 0x00);
                writeInt(msg.isReturned() ? 0x01 : 0x00);
                writeInt(0x00); // SysString in some case it seems
            }
        } else {
            writeInt(0x00);
        }
        writeInt(MESSAGE_FEE);
        writeInt(MESSAGE_FEE_PER_SLOT);
    }

}
