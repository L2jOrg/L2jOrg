package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.instancemanager.MailManager;
import org.l2j.gameserver.model.entity.Message;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.SystemMessageId;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author Migi, DS
 */
public class ExShowReceivedPostList extends IClientOutgoingPacket {
    private static final int MESSAGE_FEE = 100;
    private static final int MESSAGE_FEE_PER_SLOT = 1000;
    private final List<Message> _inbox;

    public ExShowReceivedPostList(int objectId) {
        _inbox = MailManager.getInstance().getInbox(objectId);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_RECEIVED_POST_LIST.writeId(packet);

        packet.putInt((int) (System.currentTimeMillis() / 1000));
        if ((_inbox != null) && (_inbox.size() > 0)) {
            packet.putInt(_inbox.size());
            for (Message msg : _inbox) {
                packet.putInt(msg.getMailType().ordinal());
                if (msg.getMailType() == MailType.COMMISSION_ITEM_SOLD) {
                    packet.putInt(SystemMessageId.THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD.getId());
                } else if (msg.getMailType() == MailType.COMMISSION_ITEM_RETURNED) {
                    packet.putInt(SystemMessageId.THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED.getId());
                }
                packet.putInt(msg.getId());
                writeString(msg.getSubject(), packet);
                writeString(msg.getSenderName(), packet);
                packet.putInt(msg.isLocked() ? 0x01 : 0x00);
                packet.putInt(msg.getExpirationSeconds());
                packet.putInt(msg.isUnread() ? 0x01 : 0x00);
                packet.putInt(((msg.getMailType() == MailType.COMMISSION_ITEM_SOLD) || (msg.getMailType() == MailType.COMMISSION_ITEM_RETURNED)) ? 0 : 1);
                packet.putInt(msg.hasAttachments() ? 0x01 : 0x00);
                packet.putInt(msg.isReturned() ? 0x01 : 0x00);
                packet.putInt(0x00); // SysString in some case it seems
            }
        } else {
            packet.putInt(0x00);
        }
        packet.putInt(MESSAGE_FEE);
        packet.putInt(MESSAGE_FEE_PER_SLOT);
    }
}
